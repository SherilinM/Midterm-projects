package com.module.Application.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.Application.classes.Address;
import com.module.Application.enums.SystemRole;
import com.module.Application.models.AccountHolder;
import com.module.Application.models.Admin;
import com.module.Application.models.Role;
import com.module.Application.controller.dto.accounts.CreditCardAccDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CreditCardAccControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
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
    private UserRepository userRepository;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        Address address1=new Address("Calle del carmen, 32. Madrid");
        Address address2=new Address("Calle del puente, 45. Cuenca");
        Address address3=new Address("Calle del mar, 56. Albacete");

        AccountHolder owner1= new AccountHolder("Fernando Rado", "rader", "radero", LocalDate.of(2006, 10, 5), address3);
        AccountHolder owner2= new AccountHolder("Pepa Bol", "elbotijero", "botijo", LocalDate.of(1960, 10, 5), address1, address2);

        accountHolderRepository.saveAll(List.of(owner1, owner2));

        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner1));
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner2));

        Admin admin1= new Admin("Carlos Perez", "carl", "santander");
        adminRepository.save(admin1);
        roleRepository.save(new Role(SystemRole.ADMIN, admin1));

    }

    @AfterEach
    void tearDown() {
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
    void create_correctDtoLoggedAdmin_createAccount() throws Exception {
        CreditCardAccDto creditCardAccDto = new CreditCardAccDto();
        creditCardAccDto.setPrimaryOwnerId(accountHolderRepository.findAll().get(0).getId());
        creditCardAccDto.setBalance(new BigDecimal("1000"));
        creditCardAccDto.setCreditLimit(new BigDecimal("3000"));
        creditCardAccDto.setInterestRate(new BigDecimal("0.12"));
        String body = objectMapper.writeValueAsString(creditCardAccDto);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-creditCardAcc")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1000"));

    }

    @Test
    void create_correctDtoNoLoggedAdmin_noCreateAccount() throws Exception {
        CreditCardAccDto creditCardAccDto = new CreditCardAccDto();
        creditCardAccDto.setPrimaryOwnerId(accountHolderRepository.findAll().get(0).getId());
        creditCardAccDto.setBalance(new BigDecimal("1000"));
        creditCardAccDto.setCreditLimit(new BigDecimal("3000"));
        creditCardAccDto.setInterestRate(new BigDecimal("0.12"));
        String body = objectMapper.writeValueAsString(creditCardAccDto);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-creditCardAcc")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void create_incorrectDtoLoggedAdmin_noCreateAccount() throws Exception {
        CreditCardAccDto creditCardAccDto = new CreditCardAccDto();
        creditCardAccDto.setPrimaryOwnerId(accountHolderRepository.findAll().get(0).getId());
        creditCardAccDto.setBalance(new BigDecimal("1000"));
        creditCardAccDto.setCreditLimit(new BigDecimal("-3000"));
        creditCardAccDto.setInterestRate(new BigDecimal("0.12"));
        String body = objectMapper.writeValueAsString(creditCardAccDto);
        System.out.println(body);
        MvcResult result = mockMvc.perform(
                post("/admin/create-creditCardAcc")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }
}
