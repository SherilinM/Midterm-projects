package com.module.Application.controller.impl;
import com.module.Application.controller.interfaces.IStudentCheckingController;
import com.module.Application.models.StudentCheckingAcc;
import com.module.Application.service.interfaces.IStudentCheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class StudentCheckingAccController implements IStudentCheckingController {

    @Autowired
    private IStudentCheckingAccService studentCheckingAccService;

    /** Method to find all student checking accounts (you have to be and ADMIN) **/
    @GetMapping("/admin/sCheckAcc")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentCheckingAcc> showAll() {
        return studentCheckingAccService.showAll();
    }

    /** Method to find one student checking account by id (you have to be and ADMIN) **/
    @GetMapping("/admin/sCheckAcc/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<StudentCheckingAcc> find(@PathVariable Long id) {
        return studentCheckingAccService.find(id);
    }
}