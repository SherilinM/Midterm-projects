package com.module.Application.repository;
import com.module.Application.models.Account;
import com.module.Application.models.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /** Method to find by user name **/
    public Account findByPrimaryOwner(AccountHolder accountHolder);
}
