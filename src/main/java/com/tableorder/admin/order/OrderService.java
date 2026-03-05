package com.tableorder.admin.order;

import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.domain.*;
import com.tableorder.admin.order.dto.*;
import com.tableorder.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final RestaurantTableRepository tableRepository;

    public TableOrdersResponse getOrdersByTable(Long storeId, Long tableId) {
        RestaurantTable table = findTableByStore(storeId, tableId);

        if (table.getCurrentSessionId() == null) {
            return new TableOrdersResponse(tableId, table.getTableNumber(), 0, List.of());
        }

        List<Order> orders = orderRepository.findActiveOrdersByTableSession(
                tableId, table.getCurrentSessionId());
        int totalAmount = orderRepository.sumTotalAmountByTableSession(
                tableId, table.getCurrentSessionId());

        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toOrderResponse)
                .toList();

        return new TableOrdersResponse(tableId, table.getTableNumber(), totalAmount, orderResponses);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long storeId, Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }

        order.updateStatus(newStatus);
        return toOrderResponse(order);
    }

    @Transactional
    public void deleteOrder(Long storeId, Long orderId) {
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.softDelete();
    }

    public List<OrderHistoryResponse> getOrderHistory(Long storeId, Long tableId,
                                                       LocalDate dateFrom, LocalDate dateTo) {
        findTableByStore(storeId, tableId);

        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;

        return orderHistoryRepository.findByTableIdAndDateRange(tableId, from, to).stream()
                .map(OrderHistoryResponse::from)
                .toList();
    }

    private RestaurantTable findTableByStore(Long storeId, Long tableId) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TABLE_NOT_FOUND));
        if (!table.getStoreId().equals(storeId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return table;
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(OrderItemResponse::from)
                .toList();
        return OrderResponse.from(order, items);
    }
}
