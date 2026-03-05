package com.tableorder.admin.order.dto;

import com.tableorder.admin.domain.Order;
import com.tableorder.admin.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private final Long id;
    private final Long tableId;
    private final Integer totalAmount;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> items;

    public static OrderResponse from(Order order, List<OrderItemResponse> items) {
        return OrderResponse.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
