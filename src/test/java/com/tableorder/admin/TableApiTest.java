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
class TableApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StoreRepository storeRepository;
    @Autowired AdminAccountRepository adminAccountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long storeId;
    String token;
    Long tableId;

    @BeforeAll
    void setUp() throws Exception {
        var store = TestHelper.createStore(storeRepository);
        storeId = store.getId();
        TestHelper.createAdmin(adminAccountRepository, passwordEncoder, storeId);
        token = TestHelper.login(mockMvc, objectMapper, storeId);
    }

    @Test
    @Order(1)
    @DisplayName("테이블 등록")
    void createTable() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tables")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"tableNumber": 1, "pin": "1234"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableNumber").value(1))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andReturn();

        tableId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("테이블 번호 중복 등록 → 실패")
    void createDuplicateTable() throws Exception {
        mockMvc.perform(post("/api/tables")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"tableNumber": 1, "pin": "5678"}
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TABLE_NUMBER_DUPLICATE"));
    }

    @Test
    @Order(3)
    @DisplayName("테이블 목록 조회")
    void getTables() throws Exception {
        mockMvc.perform(get("/api/stores/{storeId}/tables", storeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableNumber").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("테이블 수정")
    void updateTable() throws Exception {
        mockMvc.perform(put("/api/tables/{tableId}", tableId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"tableNumber": 10, "pin": "9999"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(10));
    }

    @Test
    @Order(5)
    @DisplayName("활성 세션 없는 테이블 이용 완료 → 실패")
    void completeNoSession() throws Exception {
        mockMvc.perform(post("/api/tables/{tableId}/complete", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NO_ACTIVE_SESSION"));
    }

    @Test
    @Order(6)
    @DisplayName("테이블 삭제")
    void deleteTable() throws Exception {
        mockMvc.perform(delete("/api/tables/{tableId}", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(7)
    @DisplayName("잘못된 PIN 형식 → 검증 실패")
    void invalidPin() throws Exception {
        mockMvc.perform(post("/api/tables")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"tableNumber": 2, "pin": "ab"}
                            """))
                .andExpect(status().isBadRequest());
    }
}
