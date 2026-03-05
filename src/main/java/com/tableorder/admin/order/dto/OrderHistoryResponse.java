package com.tableorder.admin.order.dto;

import com.tableorder.admin.domain.OrderHistory;
import com.tableorder.admin.domain.OrderHistoryItem;
import com.tableorder.admin.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderHistoryResponse {
    private final Long id;
    private final Long originalOrderId;
    private final Integer totalAmount;
    private final OrderStatus status;
    private final LocalDateTime orderedAt;
    private final LocalDateTime completedAt;
    private final List<OrderHistoryItemResponse> items;

    public static OrderHistoryResponse from(OrderHistory history) {
        List<OrderHistoryItemResponse> items = history.getItems().stream()
                .map(OrderHistoryItemResponse::from)
                .toList();
        return OrderHistoryResponse.builder()
                .id(history.getId())
                .originalOrderId(history.getOriginalOrderId())
                .totalAmount(history.getTotalAmount())
                .status(history.getStatus())
                .orderedAt(history.getOrderedAt())
                .completedAt(history.getCompletedAt())
                .items(items)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class OrderHistoryItemResponse {
        private final Long menuId;
        private final String menuName;
        private final Integer quantity;
        private final Integer unitPrice;
        private final Integer subtotal;

        public static OrderHistoryItemResponse from(OrderHistoryItem item) {
            return new OrderHistoryItemResponse(
                    item.getMenuId(), item.getMenuName(),
                    item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
        }
    }
}
