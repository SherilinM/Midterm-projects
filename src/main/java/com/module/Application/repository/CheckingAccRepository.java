package com.module.Application.repository;
import com.module.Application.models.CheckingAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingAccRepository extends JpaRepository<CheckingAcc, Long> {
}