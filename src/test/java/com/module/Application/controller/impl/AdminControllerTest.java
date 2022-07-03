package com.module.Application.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.Application.classes.Address;
import com.module.Application.enums.SystemRole;
import com.module.Application.classes.Money;
import com.module.Application.models.*;
import com.module.Application.controller.dto.accounts.MoneyDto;
import com.module.Application.controller.dto.users.AdminDto;
import com.module.Application.controller.dto.users.ThirdPartyDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdminControllerTest {

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

        AccountHolder owner1= new AccountHolder("Pepa Bol", "elbotijero", "botijo", LocalDate.of(1960, 10, 5), address1, address2);
        AccountHolder owner2= new AccountHolder("Fernando Rado", "rader", "radero", LocalDate.of(2002, 10, 5), address3);
        AccountHolder owner3= new AccountHolder("Rosa Melano", "melaner", "melania", LocalDate.of(1999, 10, 5), address2);

        accountHolderRepository.saveAll(List.of(owner1, owner2, owner3));

        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner1));
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner2));
        roleRepository.save(new Role(SystemRole.ACCOUNT_HOLDER, owner3));

        Admin admin1= new Admin("Carlos Perez", "carl", "santander");
        adminRepository.save(admin1);
        roleRepository.save(new Role(SystemRole.ADMIN, admin1));

        CheckingAcc checkingAcc1 = new CheckingAcc(owner1, new Money(new BigDecimal("15000")), "tengohambre");
        StudentCheckingAcc studentCheckingAcc1 = new StudentCheckingAcc(owner2, new Money(new BigDecimal("1000")), "estudiante");
        SavingsAcc savingsAcc1 = new SavingsAcc(owner1, owner2, new Money(new BigDecimal("1000")), "perro");
        savingsAcc1.setLastInterestUpdate(LocalDate.of(2019, 1, 10));
        CreditCardAcc creditCardAcc1 = new CreditCardAcc(owner1, new Money(new BigDecimal("9000")));

        checkingAccRepository.save(checkingAcc1);
        studentCheckingAccRepository.save(studentCheckingAcc1);
        savingsAccRepository.save(savingsAcc1);
        creditCardAccRepository.save(creditCardAcc1);

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
    void createAdmin_CorrectDtoAdminLogged_CreateAdmin() throws Exception {
        AdminDto adminDto = new AdminDto();
        adminDto.setName("santander");
        adminDto.setUsername("santander2021");
        adminDto.setPassword("holacomoestas");
        String body = objectMapper.writeValueAsString(adminDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-new-admin")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("santander2021"));
    }

    @Test
    void createAdmin_CorrectDtoNoAdminLogged_NoCreateAdmin() throws Exception {
        AdminDto adminDto = new AdminDto();
        adminDto.setName("santander");
        adminDto.setUsername("santander2021");
        adminDto.setPassword("holacomoestas");
        String body = objectMapper.writeValueAsString(adminDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-new-admin")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void createAdmin_IncorrectDtoAdminLogged_NoCreateAdmin() throws Exception {
        AdminDto adminDto = new AdminDto();
        adminDto.setName("santander");
        adminDto.setPassword("holacomoestas");
        String body = objectMapper.writeValueAsString(adminDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-new-admin")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void createThirdParty_CorrectDtoAdminLogged_CreateThirdParty() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setName("clientedelBBVA");
        thirdPartyDto.setHashKey("bebeuvea");
        String body = objectMapper.writeValueAsString(thirdPartyDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("clientedelBBVA"));
    }

    @Test
    void createThirdParty_CorrectDtoNoAdminLogged_NoCreateThirdParty() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setName("clientedelBBVA");
        thirdPartyDto.setHashKey("bebeuvea");
        String body = objectMapper.writeValueAsString(thirdPartyDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void createThirdParty_IncorrectDtoAdminLogged_NoCreateThirdParty() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setHashKey("bebeuvea");
        String body = objectMapper.writeValueAsString(thirdPartyDto);
        MvcResult result = mockMvc.perform(
                post("/admin/create-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void modifyBalance_OfCheckingAccAdminLogged_BalanceModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = checkingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    void modifyBalance_OfCheckingAccNoAdminLogged_BalanceNoModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = checkingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void modifyBalance_OfCheckingAccInvalidDto_BalanceNoModified() throws Exception {
        Long accountId = checkingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString("moneyDto");
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void modifyBalance_OfStudentCheckAccAdminLogged_BalanceModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = studentCheckingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    void modifyBalance_OfStudentCheckAccNoAdminLogged_BalanceNoModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = studentCheckingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void modifyBalance_OfStudentCheckAccInvalidDto_BalanceNoModified() throws Exception {
        Long accountId = studentCheckingAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString("moneyDto");
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void modifyBalance_OfSavingsAccAdminLogged_BalanceModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = savingsAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    void modifyBalance_OfSavingsAccNoAdminLogged_BalanceNoModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = savingsAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void modifyBalance_OfSavingsAccInvalidDto_BalanceNoModified() throws Exception {
        Long accountId = savingsAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString("moneyDto");
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void modifyBalance_OfCreditAccAdminLogged_BalanceModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = creditCardAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("15000"));
    }

    @Test
    void modifyBalance_OfCreditAccNoAdminLogged_BalanceNoModified() throws Exception {
        MoneyDto moneyDto = new MoneyDto(new BigDecimal("15000"));
        Long accountId = creditCardAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(moneyDto);
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void modifyBalance_OfCreditAccInvalidDto_BalanceNoModified() throws Exception {
        Long accountId = creditCardAccRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString("moneyDto");
        MvcResult result = mockMvc.perform(
                patch("/admin/modify-balance/"+accountId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("carl")
                                .password("santander")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }
}