package com.tableorder.admin.table.dto;

import com.tableorder.admin.domain.RestaurantTable;
import com.tableorder.admin.domain.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TableResponse {
    private final Long id;
    private final Integer tableNumber;
    private final TableStatus status;
    private final String currentSessionId;
    private final Integer totalOrderAmount;
    private final Integer orderCount;

    public static TableResponse from(RestaurantTable table, int totalOrderAmount, int orderCount) {
        return TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .status(table.getStatus())
                .currentSessionId(table.getCurrentSessionId())
                .totalOrderAmount(totalOrderAmount)
                .orderCount(orderCount)
                .build();
    }
}
