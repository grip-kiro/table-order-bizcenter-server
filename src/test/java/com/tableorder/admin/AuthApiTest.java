package com.tableorder.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.repository.AdminAccountRepository;
import com.tableorder.admin.repository.StoreRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StoreRepository storeRepository;
    @Autowired AdminAccountRepository adminAccountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long storeId;

    @BeforeAll
    void setUp() {
        var store = TestHelper.createStore(storeRepository);
        storeId = store.getId();
        TestHelper.createAdmin(adminAccountRepository, passwordEncoder, storeId);
    }

    @Test
    @Order(1)
    @DisplayName("관리자 로그인 성공")
    void loginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"storeId": %d, "username": "admin", "password": "admin1234"}
                            """.formatted(storeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void loginWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"storeId": %d, "username": "admin", "password": "wrong"}
                            """.formatted(storeId)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @Order(3)
    @DisplayName("토큰 갱신 성공")
    void refreshSuccess() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"storeId": %d, "username": "admin", "password": "admin1234"}
                            """.formatted(storeId)))
                .andReturn();

        String rt = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"refreshToken": "%s"}
                            """.formatted(rt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"storeId": %d, "username": "admin", "password": "admin1234"}
                            """.formatted(storeId)))
                .andReturn();

        String rt = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"refreshToken": "%s"}
                            """.formatted(rt)))
                .andExpect(status().isOk());

        // 로그아웃 후 refresh 시도 → 실패
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"refreshToken": "%s"}
                            """.formatted(rt)))
                .andExpect(status().isUnauthorized());
    }
}
