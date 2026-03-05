package com.tableorder.admin.table;

import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.domain.*;
import com.tableorder.admin.repository.*;
import com.tableorder.admin.table.dto.CreateTableRequest;
import com.tableorder.admin.table.dto.TableResponse;
import com.tableorder.admin.table.dto.UpdateTableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableService {

    private final RestaurantTableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryItemRepository orderHistoryItemRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public List<TableResponse> getTables(Long storeId) {
        return tableRepository.findByStoreIdOrderByTableNumber(storeId).stream()
                .map(this::toTableResponse)
                .toList();
    }

    @Transactional
    public TableResponse createTable(Long storeId, CreateTableRequest request) {
        if (tableRepository.existsByStoreIdAndTableNumber(storeId, request.getTableNumber())) {
            throw new BusinessException(ErrorCode.TABLE_NUMBER_DUPLICATE);
        }

        RestaurantTable table = RestaurantTable.builder()
                .storeId(storeId)
                .tableNumber(request.getTableNumber())
                .pin(request.getPin())
                .build();

        return toTableResponse(tableRepository.save(table));
    }

    @Transactional
    public TableResponse updateTable(Long storeId, Long tableId, UpdateTableRequest request) {
        RestaurantTable table = findByStore(storeId, tableId);

        if (request.getTableNumber() != null && !request.getTableNumber().equals(table.getTableNumber())) {
            if (tableRepository.existsByStoreIdAndTableNumber(storeId, request.getTableNumber())) {
                throw new BusinessException(ErrorCode.TABLE_NUMBER_DUPLICATE);
            }
        }

        table.update(request.getTableNumber(), request.getPin());
        return toTableResponse(table);
    }

    @Transactional
    public void deleteTable(Long storeId, Long tableId) {
        RestaurantTable table = findByStore(storeId, tableId);
        if (table.hasActiveSession()) {
            throw new BusinessException(ErrorCode.TABLE_IN_USE);
        }
        tableRepository.delete(table);
    }

    @Transactional
    public void completeTableSession(Long storeId, Long tableId) {
        RestaurantTable table = findByStore(storeId, tableId);

        if (!table.hasActiveSession()) {
            throw new BusinessException(ErrorCode.NO_ACTIVE_SESSION);
        }

        String sessionId = table.getCurrentSessionId();
        LocalDateTime completedAt = LocalDateTime.now();

        // 현재 세션의 활성 주문을 OrderHistory로 복사
        List<Order> activeOrders = orderRepository.findActiveOrdersByTableSession(tableId, sessionId);
        for (Order order : activeOrders) {
            OrderHistory history = OrderHistory.builder()
                    .originalOrderId(order.getId())
                    .storeId(order.getStoreId())
                    .tableId(order.getTableId())
                    .sessionId(sessionId)
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .orderedAt(order.getCreatedAt())
                    .completedAt(completedAt)
                    .build();
            orderHistoryRepository.save(history);

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                OrderHistoryItem historyItem = OrderHistoryItem.builder()
                        .orderHistory(history)
                        .menuId(item.getMenuId())
                        .menuName(item.getMenuName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build();
                orderHistoryItemRepository.save(historyItem);
            }
        }

        // 테이블 세션 리셋
        table.clearSession();

        // 해당 테이블의 태블릿 토큰 무효화
        refreshTokenRepository.revokeAllByTableIdAndRole(tableId, TokenRole.TABLE);
    }

    private RestaurantTable findByStore(Long storeId, Long tableId) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TABLE_NOT_FOUND));
        if (!table.getStoreId().equals(storeId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return table;
    }

    private TableResponse toTableResponse(RestaurantTable table) {
        int totalAmount = 0;
        int orderCount = 0;
        if (table.getCurrentSessionId() != null) {
            totalAmount = orderRepository.sumTotalAmountByTableSession(
                    table.getId(), table.getCurrentSessionId());
            orderCount = orderRepository.findActiveOrdersByTableSession(
                    table.getId(), table.getCurrentSessionId()).size();
        }
        return TableResponse.from(table, totalAmount, orderCount);
    }
}
