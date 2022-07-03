package com.module.Application.controller.impl;

import com.module.Application.enums.SystemRole;
import com.module.Application.models.Admin;
import com.module.Application.models.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.Application.controller.dto.users.AccountHolderDto;
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
import com.module.Application.repository.AccountRepository;
import com.module.Application.repository.AdminRepository;
import com.module.Application.repository.RoleRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountHolderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AdminRepository adminRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        Admin admin1= new Admin("Carlos Perez", "carl", "santander");
        adminRepository.save(admin1);
        roleRepository.save(new Role(SystemRole.ADMIN, admin1));
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void create_ValidDtoWithAdminLogged_newAccHolder() throws Exception {
        AccountHolderDto accountHolderDTO = new AccountHolderDto();
        accountHolderDTO.setName("Pedro");
        accountHolderDTO.setUsername("pedrogolosho");
        accountHolderDTO.setPassword("54321");
        accountHolderDTO.setDateOfBirth(LocalDate.of(1985,4,2));
        accountHolderDTO.setPrimaryAddress("C/ como estas, 2.");
        accountHolderDTO.setMailingAddress("C/ como estas, 76.");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pedro"));
    }

    @Test
    void create_ValidDtoWithNoAdminLogged_NoAccHolder() throws Exception {
        AccountHolderDto accountHolderDTO = new AccountHolderDto();
        accountHolderDTO.setName("Pedro");
        accountHolderDTO.setUsername("pedrogolosho");
        accountHolderDTO.setPassword("12345");
        accountHolderDTO.setDateOfBirth(LocalDate.of(1985,4,2));
        accountHolderDTO.setPrimaryAddress("C/ como estas, 2.");
        accountHolderDTO.setMailingAddress("C/ como estas, 76.");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void create_ValidDtoWithNoUserLogged_NoAccHolder() throws Exception {
        AccountHolderDto accountHolderDTO = new AccountHolderDto();
        accountHolderDTO.setName("Pedro");
        accountHolderDTO.setUsername("pedrogolosho");
        accountHolderDTO.setPassword("12345");
        accountHolderDTO.setDateOfBirth(LocalDate.of(1985,4,2));
        accountHolderDTO.setPrimaryAddress("C/ como estas, 2.");
        accountHolderDTO.setMailingAddress("C/ como estas, 76.");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void create_invalidDto_NoAccHolder() throws Exception {
        AccountHolderDto accountHolderDTO = new AccountHolderDto();
        accountHolderDTO.setName("Pedro");
        accountHolderDTO.setDateOfBirth(LocalDate.of(1985,4,2));
        accountHolderDTO.setPrimaryAddress("C/ como estas, 2.");
        accountHolderDTO.setMailingAddress("C/ como estas, 76.");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }



}