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
class MenuApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StoreRepository storeRepository;
    @Autowired AdminAccountRepository adminAccountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long storeId;
    String token;
    Long categoryId;
    Long menuId;

    @BeforeAll
    void setUp() throws Exception {
        var store = TestHelper.createStore(storeRepository);
        storeId = store.getId();
        TestHelper.createAdmin(adminAccountRepository, passwordEncoder, storeId);
        token = TestHelper.login(mockMvc, objectMapper, storeId);

        // 카테고리 생성
        MvcResult catResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "메인"}
                            """))
                .andReturn();
        categoryId = objectMapper.readTree(catResult.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @Order(1)
    @DisplayName("메뉴 등록")
    void createMenu() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/menus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "불고기 정식",
                              "price": 12000,
                              "description": "소불고기와 밥",
                              "imageUrl": null,
                              "categoryIds": [%d]
                            }
                            """.formatted(categoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("불고기 정식"))
                .andExpect(jsonPath("$.price").value(12000))
                .andExpect(jsonPath("$.soldOut").value(false))
                .andReturn();

        menuId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("메뉴 목록 조회")
    void getMenus() throws Exception {
        mockMvc.perform(get("/api/stores/{storeId}/menus", storeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("불고기 정식"));
    }

    @Test
    @Order(3)
    @DisplayName("메뉴 수정")
    void updateMenu() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}", menuId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "불고기 정식 (특선)",
                              "price": 15000,
                              "description": "프리미엄 소불고기",
                              "imageUrl": null,
                              "categoryIds": [%d]
                            }
                            """.formatted(categoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("불고기 정식 (특선)"))
                .andExpect(jsonPath("$.price").value(15000));
    }

    @Test
    @Order(4)
    @DisplayName("메뉴 품절 설정")
    void setSoldOut() throws Exception {
        mockMvc.perform(patch("/api/menus/{menuId}/sold-out", menuId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"soldOut": true}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soldOut").value(true));
    }

    @Test
    @Order(5)
    @DisplayName("메뉴 품절 해제")
    void unsetSoldOut() throws Exception {
        mockMvc.perform(patch("/api/menus/{menuId}/sold-out", menuId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"soldOut": false}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soldOut").value(false));
    }

    @Test
    @Order(6)
    @DisplayName("메뉴 소프트 삭제")
    void deleteMenu() throws Exception {
        mockMvc.perform(delete("/api/menus/{menuId}", menuId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(7)
    @DisplayName("삭제된 메뉴 수정 시도 → 실패")
    void updateDeletedMenu() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}", menuId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "수정 시도",
                              "price": 10000,
                              "description": "",
                              "imageUrl": null,
                              "categoryIds": [%d]
                            }
                            """.formatted(categoryId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MENU_DELETED"));
    }
}
