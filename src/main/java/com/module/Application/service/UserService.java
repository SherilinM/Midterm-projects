package com.module.Application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.module.Application.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;




}