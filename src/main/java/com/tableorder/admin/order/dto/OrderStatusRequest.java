package com.tableorder.admin.order.dto;

import com.tableorder.admin.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusRequest {

    @NotNull(message = "주문 상태는 필수입니다")
    private OrderStatus status;
}
