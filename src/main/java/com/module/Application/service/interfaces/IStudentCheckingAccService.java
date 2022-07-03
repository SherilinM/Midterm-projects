package com.module.Application.service.interfaces;

import com.module.Application.models.StudentCheckingAcc;

import java.util.List;
import java.util.Optional;

public interface IStudentCheckingAccService{

    /** Method to find all student checking accounts (you have to be and ADMIN) **/
    public List<StudentCheckingAcc> showAll();

    /** Method to find one student checking account by id (you have to be and ADMIN) **/
    public Optional<StudentCheckingAcc> find(Long id);
}