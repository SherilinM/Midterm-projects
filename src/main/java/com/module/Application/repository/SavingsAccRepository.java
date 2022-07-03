package com.module.Application.repository;
import com.module.Application.models.SavingsAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccRepository extends JpaRepository<SavingsAcc, Long> {
}
