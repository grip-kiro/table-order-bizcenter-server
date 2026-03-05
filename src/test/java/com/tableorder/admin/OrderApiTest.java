package com.tableorder.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.domain.*;
import com.tableorder.admin.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StoreRepository storeRepository;
    @Autowired AdminAccountRepository adminAccountRepository;
    @Autowired RestaurantTableRepository tableRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemRepository orderItemRepository;
    @Autowired PasswordEncoder passwordEncoder;

    Long storeId;
    String token;
    Long tableId;
    Long orderId;
    String sessionId;

    @BeforeAll
    void setUp() throws Exception {
        var store = TestHelper.createStore(storeRepository);
        storeId = store.getId();
        TestHelper.createAdmin(adminAccountRepository, passwordEncoder, storeId);
        token = TestHelper.login(mockMvc, objectMapper, storeId);

        // 테이블 생성 (세션 있는 상태)
        sessionId = UUID.randomUUID().toString();
        RestaurantTable table = RestaurantTable.builder()
                .storeId(storeId)
                .tableNumber(1)
                .pin("1234")
                .build();
        table = tableRepository.save(table);
        tableId = table.getId();

        // 세션 설정 (리플렉션)
        setField(table, "currentSessionId", sessionId);
        setField(table, "status", TableStatus.OCCUPIED);
        tableRepository.save(table);

        // 주문 생성
        com.tableorder.admin.domain.Order order = createOrder(storeId, tableId, sessionId, 23000);
        order = orderRepository.save(order);
        orderId = order.getId();

        // 주문 항목 생성
        OrderItem item1 = createOrderItem(order, 1L, "불고기 정식", 1, 12000);
        orderItemRepository.save(item1);
        OrderItem item2 = createOrderItem(order, 2L, "비빔밥", 1, 10000);
        orderItemRepository.save(item2);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("테이블 주문 조회")
    void getTableOrders() throws Exception {
        mockMvc.perform(get("/api/admin/tables/{tableId}/orders", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId").value(tableId))
                .andExpect(jsonPath("$.totalAmount").value(23000))
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders[0].items").isArray());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("주문 상태 변경: PENDING → PREPARING")
    void updateStatusToPreparing() throws Exception {
        mockMvc.perform(patch("/api/admin/orders/{orderId}/status", orderId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"status": "PREPARING"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("주문 상태 변경: PREPARING → COMPLETED")
    void updateStatusToCompleted() throws Exception {
        mockMvc.perform(patch("/api/admin/orders/{orderId}/status", orderId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"status": "COMPLETED"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("잘못된 상태 전이 → 실패")
    void invalidStatusTransition() throws Exception {
        mockMvc.perform(patch("/api/admin/orders/{orderId}/status", orderId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"status": "PENDING"}
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("테이블 이용 완료")
    void completeTableSession() throws Exception {
        mockMvc.perform(post("/api/tables/{tableId}/complete", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 이용 완료 후 주문 조회 → 빈 목록
        mockMvc.perform(get("/api/admin/tables/{tableId}/orders", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.orders").isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("과거 주문 내역 조회")
    void getOrderHistory() throws Exception {
        mockMvc.perform(get("/api/admin/tables/{tableId}/history", tableId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].originalOrderId").value(orderId));
    }

    private com.tableorder.admin.domain.Order createOrder(Long storeId, Long tableId, String sessionId, int totalAmount) {
        try {
            var constructor = com.tableorder.admin.domain.Order.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            com.tableorder.admin.domain.Order order = constructor.newInstance();
            setField(order, "storeId", storeId);
            setField(order, "tableId", tableId);
            setField(order, "sessionId", sessionId);
            setField(order, "totalAmount", totalAmount);
            setField(order, "status", OrderStatus.PENDING);
            return order;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OrderItem createOrderItem(com.tableorder.admin.domain.Order order, Long menuId, String menuName,
                                       int quantity, int unitPrice) {
        try {
            var constructor = OrderItem.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            OrderItem item = constructor.newInstance();
            setField(item, "order", order);
            setField(item, "menuId", menuId);
            setField(item, "menuName", menuName);
            setField(item, "quantity", quantity);
            setField(item, "unitPrice", unitPrice);
            setField(item, "subtotal", quantity * unitPrice);
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
