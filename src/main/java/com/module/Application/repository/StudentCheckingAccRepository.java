package com.module.Application.repository;
import com.module.Application.models.StudentCheckingAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingAccRepository extends JpaRepository<StudentCheckingAcc, Long> {
}
