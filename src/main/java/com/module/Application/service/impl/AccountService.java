package com.module.Application.service.impl;

import com.module.Application.classes.Money;
import com.module.Application.classes.Utils;
import com.module.Application.enums.AccountStatus;
import com.module.Application.models.*;
import com.module.Application.controller.dto.accounts.OperationDto;
import com.module.Application.repository.*;
import com.module.Application.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private CheckingAccRepository checkingAccRepository;

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** Method to check any account balance, adding if necessary the appropriate interest to current balance **/
    public Money checkBalance(Long id, Principal principal) {
        Money balance;
        Optional<Account> account = accountRepository.findById(id);

        if (principal==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You have to log before");
        }
        String username = principal.getName();
        // If the account is in the database
        if (account.isPresent()){

            // Check if have permissions
            if (!hasPermissionOrAdmin(account.get(), username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permissions ");
            }

            if (account.get() instanceof SavingsAcc) {
                int years = Utils.calculateYears(account.get().getLastInterestUpdate());
                if (years>=1) {

                    // If its a Savings Account and More than a year has passed since the last time interest was added
                    BigDecimal amount = new BigDecimal(String.valueOf(account.get().getBalance().getAmount()));
                    BigDecimal interest = new BigDecimal(String.valueOf(((SavingsAcc) account.get()).getInterestRate()));

                    amount = CalculateInterest(amount, interest, years);

                    account.get().setBalance(new Money(amount));
                    // Update last interest update datetime
                    account.get().setLastInterestUpdate(account.get().getLastInterestUpdate().plusYears(years));
                    savingsAccRepository.save((SavingsAcc) account.get());
                }
            }
            if (account.get() instanceof CreditCardAcc) {
                int months = Utils.calculateYears(account.get().getLastInterestUpdate())*12+
                        Utils.calculateMonths(account.get().getLastInterestUpdate());
                if (Utils.calculateMonths(account.get().getLastInterestUpdate())>=1) {

                    // If its a Credit Card Account and More than a month has passed since the last time interest was added
                    BigDecimal amount = new BigDecimal(String.valueOf(account.get().getBalance().getAmount()));
                    BigDecimal interest = new BigDecimal(String.valueOf(((CreditCardAcc) account.get()).getInterestRate())).divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);

                    amount = CalculateInterest(amount, interest, months);

                    account.get().setBalance(new Money(amount));
                    account.get().setLastInterestUpdate(account.get().getLastInterestUpdate().plusMonths(months));
                    creditCardAccRepository.save((CreditCardAcc) account.get());
                }
            }
            if (account.get() instanceof CheckingAcc){
                // If its a Checking account and more than a month has passed since the last monthly penalty fee was applied
                updateMaintenanceFee((CheckingAcc) account.get());
            }
            // If its not a Credit, Checking or Savings Account
            balance = new Money(new BigDecimal(String.valueOf(accountRepository.findById(id).get().getBalance().getAmount())));

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return balance;
    }


    /** Method to make transfers between accounts in database **/
    public Operation transfer(OperationDto operationDto, Principal principal) {

        if (principal==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You have to log in");
        }

        // Verify that origin account exists
        Optional<Account> originAccount = accountRepository.findById(operationDto.getOriginAccountId());

        // Verify that destination account exists
        Optional<Account> destinationAccount = accountRepository.findById(operationDto.getDestinationAccountId());

        // If one of them not exist, stop the transfer
        if (!originAccount.isPresent() || !destinationAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin or destination account not founds");
        }

        // If both exist, continue. Get the origin account and the account holder username
        Account accountOrigin = originAccount.get();
        Account accountDestination = destinationAccount.get();
        String username = principal.getName();

        // Verify if user has the permissions to make the transfer
        if (!hasPermission(accountOrigin, username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permissions ");
        }

        // Verify if destination name is a real account holder
        if (!checkDestinationNameIsDestinationOwner(operationDto, destinationAccount.get())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The destination account holder is not found");
        }

        // Verify if origin account has enough money
        if (accountOrigin.getBalance().getAmount().subtract(operationDto.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have enough money to make the transfer");
        }

        // Check if account is frozen
        if (checkFreeze(accountOrigin) || checkFreeze(accountDestination)){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Check fraud
        if (checkFraud(accountOrigin, operationDto.getAmount())){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Update balance to origin and destination accounts
        accountOrigin.getBalance().decreaseAmount(operationDto.getAmount());
        accountDestination.getBalance().increaseAmount(operationDto.getAmount());

        // If after updating balance the current amount of origin account is below the minimum balance, apply penalty fee
        if (checkPenaltyFee(accountOrigin)) {
            accountOrigin.getBalance().decreaseAmount(accountOrigin.getPenaltyFee());
            if (accountOrigin instanceof SavingsAcc) {
                ((SavingsAcc) accountOrigin).setBelowMinimumBalance(true);}
        }
        if (accountOrigin instanceof CheckingAcc) {
            ((CheckingAcc) accountOrigin).setBelowMinimumBalance(true);
        }

        // If after updating balance of destination account, balance was below minimum and now is over minimum balance
        if (accountDestination instanceof SavingsAcc) {
            if (accountDestination.getBalance().getAmount().compareTo(((SavingsAcc) accountDestination).getMinimumBalance().getAmount())>0 &&
                    ((SavingsAcc) accountDestination).getBelowMinimumBalance()){
                ((SavingsAcc) accountDestination).setBelowMinimumBalance(false);
            }
        }
        if (accountDestination instanceof CheckingAcc) {
            if (accountDestination.getBalance().getAmount().compareTo(((CheckingAcc) accountDestination).getMinimumBalance().getAmount())>0 &&
                    ((CheckingAcc) accountDestination).getBelowMinimumBalance()){
                ((CheckingAcc) accountDestination).setBelowMinimumBalance(false);
            }
        }

        // Update values in database
        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestination);

        // Make the transfer object
        Operation operation = new Operation(accountOrigin, accountDestination, new Money(operationDto.getAmount()), operationDto.getName());

        // Save the transfer in database
        return operationRepository.save(operation);

    }

    /** Method to make transfers to third-party accounts from normal accounts **/
    public Operation transferToThirdParty(String hashKey, String secretKey, OperationDto operationDto) {

        Optional<Account> originAccount = accountRepository.findById(operationDto.getOriginAccountId());
        Optional<ThirdParty> thirdParty = thirdPartyRepository.findById(operationDto.getDestinationAccountId());

        // Check if origin account from DTO exists in repository:
        if (!originAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account with ID: " + operationDto.getOriginAccountId());
        }
        // Check if provided secret key matches with origin account:
        if(!checkSecretKey(originAccount.get(), secretKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Secret Key for account with ID: " + operationDto.getOriginAccountId());
        }
        // Check if third party from DTO exists in repository:
        if (!thirdParty.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Third Party with ID: " + operationDto.getDestinationAccountId());
        }
        // Check if provided hash key matches with third party:
        if (!passwordEncoder.matches(hashKey, thirdParty.get().getHashKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong HashKey for Third party " + thirdParty.get().getId());
        }
        // Check if provided name matches with third party name
        if (!operationDto.getName().equals(thirdParty.get().getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided name for third party is incorrect");
        }
        // Verify if origin account has enough money
        if (originAccount.get().getBalance().getAmount().subtract(operationDto.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have enough money to make the transfer");
        }

        // Check if account is frozen
        if (checkFreeze(originAccount.get())){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Check fraud
        if (checkFraud(originAccount.get(), operationDto.getAmount())){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Update balance to origin account
        originAccount.get().getBalance().decreaseAmount(operationDto.getAmount());

        // If after updating balance the current amount of origin account is below the minimum balance, apply penalty fee
        if (checkPenaltyFee(originAccount.get())) {
            originAccount.get().getBalance().decreaseAmount(originAccount.get().getPenaltyFee());
            if (originAccount.get() instanceof SavingsAcc) {
                ((SavingsAcc) originAccount.get()).setBelowMinimumBalance(true);}
        }
        if (originAccount.get() instanceof CheckingAcc) {
            ((CheckingAcc) originAccount.get()).setBelowMinimumBalance(true);
        }

        // Save origin account in the repository
        accountRepository.save(originAccount.get());

        // Make the transfer object
        Operation operation = new Operation(originAccount.get(),
                null,
                new Money(operationDto.getAmount()),
                operationDto.getName());

        return operationRepository.save(operation);
    }


    /** Method to make transfers from third-party accounts to normal accounts **/
    public Operation transferFromThirdParty(String hashKey, String secretKey, OperationDto operationDto){

        Optional<Account> destinationAccount = accountRepository.findById(operationDto.getDestinationAccountId());
        Optional<ThirdParty> thirdParty = thirdPartyRepository.findById(operationDto.getOriginAccountId());

        // Check if third party from DTO exists in repository:
        if (!thirdParty.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Third Party with ID: " + operationDto.getOriginAccountId());
        }
        // Check if provided hash key matches with third party:
        if (!passwordEncoder.matches(hashKey, thirdParty.get().getHashKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong HashKey for Third party " + thirdParty.get().getId());
        }
        // Check if destination account from DTO exists in repository:
        if (!destinationAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no destination account with ID: " + operationDto.getDestinationAccountId());
        }
        // Check if provided name matches destination account name
        if (!checkDestinationNameIsDestinationOwner(operationDto, destinationAccount.get())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided name for destination account is incorrect");
        }
        // Check if provided secret key matches with destination account:
        if(!checkSecretKey(destinationAccount.get(), secretKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Secret Key for account with ID: " + operationDto.getDestinationAccountId());
        }

        // Check if account is frozen
        if (checkFreeze(destinationAccount.get())){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Check fraud
        if (checkFraud(destinationAccount.get(), operationDto.getAmount())){
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Something suspicious is happening");
        }

        // Make transfer
        destinationAccount.get().getBalance().increaseAmount(operationDto.getAmount());

        // If after updating balance of destination account, balance was below minimum and now is over minimum balance
        if (destinationAccount.get() instanceof SavingsAcc) {
            if (destinationAccount.get().getBalance().getAmount().compareTo(((SavingsAcc) destinationAccount.get()).getMinimumBalance().getAmount())>0 &&
                    ((SavingsAcc) destinationAccount.get()).getBelowMinimumBalance()){
                ((SavingsAcc) destinationAccount.get()).setBelowMinimumBalance(false);
            }
        }
        if (destinationAccount.get() instanceof CheckingAcc) {
            if (destinationAccount.get().getBalance().getAmount().compareTo(((CheckingAcc) destinationAccount.get()).getMinimumBalance().getAmount())>0 &&
                    ((CheckingAcc) destinationAccount.get()).getBelowMinimumBalance()){
                ((CheckingAcc) destinationAccount.get()).setBelowMinimumBalance(false);
            }
        }

        // Save destination account in the repository
        accountRepository.save(destinationAccount.get());

        // Make the transfer object and save in the repository
        Operation operation = new Operation(null,
                destinationAccount.get(),
                new Money(operationDto.getAmount()),
                operationDto.getName());

        return operationRepository.save(operation);
    }

    /** Method to check the fraud **/
    public Boolean checkFraud(Account account, BigDecimal amount){

        // check last second fraud
        if (operationRepository.operationsWithinOneSecond(account.getId()).size() >= 2){
            account.setAccountStatus(AccountStatus.FROZEN);
            accountRepository.save(account);
            return true;
        }

        // check last 24 hours fraud
        if(operationRepository.maxQuantityIn24HoursInHistory(account.getId()) != null) {
            if(operationRepository.totalLast24Hours(account.getId())==null){
                if (amount.compareTo(operationRepository.maxQuantityIn24HoursInHistory(account.getId()).multiply(new BigDecimal("1.5")))>0){
                    account.setAccountStatus(AccountStatus.FROZEN);
                    accountRepository.save(account);
                    return true;
                }
            } else if ((operationRepository.totalLast24Hours(account.getId()).add(amount).compareTo(operationRepository.maxQuantityIn24HoursInHistory(account.getId()).multiply(new BigDecimal("1.5"))))>0){
                account.setAccountStatus(AccountStatus.FROZEN);
                accountRepository.save(account);
                return true;
            }
        }
        return false;
    }

    /** Method to check freeze **/
    public Boolean checkFreeze(Account account){
        // check if account is freeze
        if (account.getAccountStatus().equals(AccountStatus.FROZEN)){
            return true;
        }
        return false;
    }


    /** Method to add the cumulated interest **/
    private BigDecimal CalculateInterest(BigDecimal amount, BigDecimal interest, int iterations){

        BigDecimal result = amount;
        for (int i=0; i<iterations; i++) {
            result = result.multiply(interest.add(new BigDecimal("1")));
        }
        return result;
    }

    /** Method to verify if user has permissions or is ADMIN **/
    public Boolean hasPermissionOrAdmin(Account account, String username) {
        Optional <User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (adminRepository.findById(user.get().getId()).isPresent()){
                return true;
            }
        }
        if (accountHolderRepository.findById(user.get().getId()).isPresent()){
            if (account.getPrimaryOwner().getUsername().equals(username)) {
                return true;
            } else if (account.getSecondaryOwner()!=null) {
                if (account.getSecondaryOwner().getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Method to verify if normal user has permissions **/
    public Boolean hasPermission(Account account, String username) {
        User user = userRepository.findByUsername(username).get();
        if (accountHolderRepository.findById(user.getId()).isPresent()){
            if (account.getPrimaryOwner().getUsername().equals(username)) {
                return true;
            } else if (account.getSecondaryOwner()!=null) {
                if (account.getSecondaryOwner().getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Method to verify if the destination user of Operation is one of the account holders of destination account **/
    public Boolean checkDestinationNameIsDestinationOwner(OperationDto operationDto, Account destinationAccount) {
        if (operationDto.getName().equals(destinationAccount.getPrimaryOwner().getName())) {
            return true;
        } else if (destinationAccount.getSecondaryOwner() != null) {
            if (operationDto.getName().equals(destinationAccount.getSecondaryOwner().getName())){
                return true;
            }
        }
        return false;
    }

    /** Method to check if an account is below minimum balance **/
    public Boolean checkPenaltyFee(Account originAccount) {
        if (originAccount instanceof SavingsAcc) {
            if (originAccount.getBalance().getAmount().compareTo(((SavingsAcc) originAccount).getMinimumBalance().getAmount()) < 0  &&
                    !((SavingsAcc) originAccount).getBelowMinimumBalance()) {
                return true;
            }
        } else if (originAccount instanceof CheckingAcc) {
            if (originAccount.getBalance().getAmount().compareTo(((CheckingAcc) originAccount).getMinimumBalance().getAmount()) < 0 &&
                    !((CheckingAcc) originAccount).getBelowMinimumBalance() ) {
                return true;
            }
        }
        return false;
    }

    /** Method to check the secret key **/
    public Boolean checkSecretKey(Account account, String secretKey){

        if (account instanceof CreditCardAcc) {
            return true;
        } else if (account instanceof SavingsAcc){
            return passwordEncoder.matches(secretKey, ((SavingsAcc) account).getSecretKey());
        } else if (account instanceof CheckingAcc) {
            return passwordEncoder.matches(secretKey, ((CheckingAcc) account).getSecretKey());
        } else if (account instanceof StudentCheckingAcc){
            return passwordEncoder.matches(secretKey, ((StudentCheckingAcc) account).getSecretKey());
        }
        return false;
    }

    /** Method to update monthly maintenance fee to Checking account **/
    public void updateMaintenanceFee(CheckingAcc checkingAcc){
        // Get months from last update and check if its more than 1
        int fromUpdate = Utils.calculateYears(checkingAcc.getLasMonthlyMaintenance())*12
                + Utils.calculateMonths(checkingAcc.getLasMonthlyMaintenance());
        if (fromUpdate >= 1) {
            for( int i=0; i <= fromUpdate; i++) {
                checkingAcc.getBalance().decreaseAmount(checkingAcc.getMonthlyMaintenanceFee());
            }
            checkingAcc.setLasMonthlyMaintenance(checkingAcc.getLasMonthlyMaintenance().plusMonths(fromUpdate));
            checkingAccRepository.save(checkingAcc);
        }
    }



}
