package com.tableorder.admin.order.dto;

import com.tableorder.admin.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private final Long id;
    private final Long menuId;
    private final String menuName;
    private final Integer quantity;
    private final Integer unitPrice;
    private final Integer subtotal;

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(), item.getMenuId(), item.getMenuName(),
                item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
    }
}
