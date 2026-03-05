package com.tableorder.admin;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StoreRepository storeRepository;
    @Autowired AdminAccountRepository adminAccountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long storeId;
    String token;
    Long categoryId;

    @BeforeAll
    void setUp() throws Exception {
        var store = TestHelper.createStore(storeRepository);
        storeId = store.getId();
        TestHelper.createAdmin(adminAccountRepository, passwordEncoder, storeId);
        token = TestHelper.login(mockMvc, objectMapper, storeId);
    }

    @Test
    @Order(1)
    @DisplayName("카테고리 등록")
    void createCategory() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "메인"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("메인"))
                .andExpect(jsonPath("$.displayOrder").value(1))
                .andReturn();

        categoryId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("카테고리 목록 조회")
    void getCategories() throws Exception {
        mockMvc.perform(get("/api/stores/{storeId}/categories", storeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("메인"));
    }

    @Test
    @Order(3)
    @DisplayName("카테고리 수정")
    void updateCategory() throws Exception {
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "메인요리"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("메인요리"));
    }

    @Test
    @Order(4)
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
