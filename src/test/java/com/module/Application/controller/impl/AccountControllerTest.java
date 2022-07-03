package com.module.Application.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.Application.classes.Address;
import com.module.Application.classes.Money;
import com.module.Application.enums.SystemRole;
import com.module.Application.models.*;
import com.module.Application.controller.dto.accounts.OperationDto;
import com.module.Application.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private OperationRepository operationRepository;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        Address address1=new Address("Calle del olvido, 45. Madrid");
        Address address2=new Address("Calle del perdon, 27. Cuenca");
        Address address3=new Address("Calle del martirio, 23. Albacete");

        AccountHolder owner1= new AccountHolder("Pepe Botijo", "elbotijero", "botijo", LocalDate.of(1960, 10, 5), address1, address2);
        AccountHolder owner2= new AccountHolder("Fernando Rado", "rader", "radero", LocalDate.of(2002, 10, 5), address3);
        AccountHolder owner3= new AccountHolder("Rosa Melano", "melaner", "melania", LocalDate.of(1999, 10, 5), address2);

        accountHolderRepository.saveAll(List.of(owner1, owner2, owner3));

        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner1));
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner2));
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner3));

        Admin admin1= new Admin("Emilio Botín", "botin", "santander");
        adminRepository.save(admin1);
        roleRepository.save(new Role(SystemRole.ADMIN, admin1));

        ThirdParty thirdParty1= new ThirdParty("tercera fiesta", "fiesta");
        thirdPartyRepository.save(thirdParty1);

        CheckingAcc checkingAcc1 = new CheckingAcc(owner1, new Money(new BigDecimal("15000")), "tengohambre");
        StudentCheckingAcc studentCheckingAcc1 = new StudentCheckingAcc(owner2, new Money(new BigDecimal("1000")), "estudiante");
        SavingsAcc savingsAcc1 = new SavingsAcc(owner1, owner2, new Money(new BigDecimal("1000")), "perro");
        savingsAcc1.setLastInterestUpdate(LocalDate.of(2019, 1, 10));
        CreditCardAcc creditCardAcc1 = new CreditCardAcc(owner3, new Money(new BigDecimal("9000")));
        creditCardAcc1.setLastInterestUpdate(LocalDate.of(2021, 1, 10));

        checkingAccRepository.save(checkingAcc1);
        studentCheckingAccRepository.save(studentCheckingAcc1);
        savingsAccRepository.save(savingsAcc1);
        creditCardAccRepository.save(creditCardAcc1);



    }


    @AfterEach
    void tearDown() {
        operationRepository.deleteAll();
        checkingAccRepository.deleteAll();
        studentCheckingAccRepository.deleteAll();
        savingsAccRepository.deleteAll();
        creditCardAccRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void checkBalance_validOwner_showBalance() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accounts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("elbotijero")
                                .password("botijo")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    public void checkBalance_Admin_showBalance() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accounts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("botin")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    public void checkBalance_NoUserLogged_noShowBalance() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accounts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void checkBalance_invalidOwner_noShowBalance() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accounts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("rader")
                                .password("radero")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }



    @Test
    public void checkBalance_validOwner_showBalanceWithMonthlyInterest() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accounts.get(3).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("mari")
                                .password("maria")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isOk()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("7250.30"));
    }

    @Test
    public void checkBalance_validOwnerInvalidId_noShowBalance() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        MvcResult result = mockMvc.perform(
                get("/check-balance/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("casandre")
                                .password("casandra")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transfer_correctDtoCorrectOwner_transferOk() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(accounts.get(0).getId());
        operationDto.setDestinationAccountId(accounts.get(1).getId());
        operationDto.setAmount(new BigDecimal("1000"));
        operationDto.setName("Ingrid Paz");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("casandre")
                                .password("casandra")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }

    @Test
    public void transfer_incorrectDtoCorrectOwner_transferKo() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(accounts.get(0).getId());
        operationDto.setDestinationAccountId(accounts.get(1).getId());
        operationDto.setAmount(new BigDecimal("-1000"));
        operationDto.setName("Ingrid Paz");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("casandre")
                                .password("casandra")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void transfer_correctDtoNoLoggedUser_transferKo() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(accounts.get(0).getId());
        operationDto.setDestinationAccountId(accounts.get(1).getId());
        operationDto.setAmount(new BigDecimal("1000"));
        operationDto.setName("Ingrid Paz");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void transfer_correctDtoIncorrectDestinationName_transferKo() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(accounts.get(0).getId());
        operationDto.setDestinationAccountId(accounts.get(1).getId());
        operationDto.setAmount(new BigDecimal("1000"));
        operationDto.setName("Ingridi");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("casandre")
                                .password("casandra")
                                .roles("ACCOUNT_HOLDER"))

        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void transferToThirdParty_correctDtoCorrectOwner_transferOk() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(accounts.get(0).getId());
        operationDto.setDestinationAccountId(thirdPartyRepository.findAll().get(0).getId());
        operationDto.setAmount(new BigDecimal("1000"));
        operationDto.setName("viaje vacaciones");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer/to-third-party/fiesta/vacaciones")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }

    @Test
    public void transferFromThirdParty_correctDtoCorrectOwner_transferOk() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        OperationDto operationDto = new OperationDto();
        operationDto.setOriginAccountId(thirdPartyRepository.findAll().get(0).getId());
        operationDto.setDestinationAccountId(accounts.get(0).getId());
        operationDto.setAmount(new BigDecimal("1000"));
        operationDto.setName("Pepa Bol");
        String body = objectMapper.writeValueAsString(operationDto);
        MvcResult result = mockMvc.perform(
                patch("/transfer/from-third-party/fiesta/vacaciones")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }
}