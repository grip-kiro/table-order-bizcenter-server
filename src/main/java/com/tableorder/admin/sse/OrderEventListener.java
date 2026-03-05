package com.tableorder.admin.sse;

import com.tableorder.admin.domain.Order;
import com.tableorder.admin.order.dto.OrderItemResponse;
import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.repository.OrderItemRepository;
import com.tableorder.admin.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SseService sseService;

    private final ConcurrentHashMap<Long, LocalDateTime> lastChecked = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 2000)
    public void pollNewOrders() {
        // 간단한 폴링 방식: 최근 2초 이내 생성된 주문 감지
        LocalDateTime since = LocalDateTime.now().minusSeconds(3);

        // 모든 매장의 새 주문을 확인하는 대신,
        // 실제 구현에서는 매장별로 마지막 확인 시점을 추적
        // MVP에서는 간단하게 처리
    }

    /**
     * Customer Backend에서 주문 생성 시 호출할 수 있는 메서드.
     * 공유 DB 환경에서는 이 메서드를 직접 호출하거나,
     * 이벤트 테이블을 폴링하여 호출합니다.
     */
    public void onOrderCreated(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(OrderItemResponse::from)
                .toList();
        OrderResponse response = OrderResponse.from(order, items);
        sseService.notifyOrderCreated(order.getStoreId(), response);
    }
}
