package com.module.Application.repository;
import com.module.Application.models.CreditCardAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardAccRepository extends JpaRepository<CreditCardAcc, Long> {
}