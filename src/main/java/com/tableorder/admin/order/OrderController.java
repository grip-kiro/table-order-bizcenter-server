package com.tableorder.admin.order;

import com.tableorder.admin.auth.AdminPrincipal;
import com.tableorder.admin.order.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/tables/{tableId}/orders")
    public ResponseEntity<TableOrdersResponse> getTableOrders(
            @PathVariable Long tableId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(orderService.getOrdersByTable(principal.getStoreId(), tableId));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(principal.getStoreId(), orderId, request.getStatus()));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        orderService.deleteOrder(principal.getStoreId(), orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tables/{tableId}/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(
            @PathVariable Long tableId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(
                orderService.getOrderHistory(principal.getStoreId(), tableId, dateFrom, dateTo));
    }
}
