package com.tableorder.admin.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TableOrdersResponse {
    private final Long tableId;
    private final Integer tableNumber;
    private final Integer totalAmount;
    private final List<OrderResponse> orders;
}
