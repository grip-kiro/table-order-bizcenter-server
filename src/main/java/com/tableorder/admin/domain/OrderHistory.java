package com.tableorder.admin.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_history", indexes = {
        @Index(name = "idx_oh_table_completed", columnList = "table_id, completed_at"),
        @Index(name = "idx_oh_store_completed", columnList = "store_id, completed_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_order_id", nullable = false)
    private Long originalOrderId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orderHistory", cascade = CascadeType.ALL)
    private List<OrderHistoryItem> items = new ArrayList<>();

    @Builder
    public OrderHistory(Long originalOrderId, Long storeId, Long tableId, String sessionId,
                        Integer totalAmount, OrderStatus status, LocalDateTime orderedAt,
                        LocalDateTime completedAt) {
        this.originalOrderId = originalOrderId;
        this.storeId = storeId;
        this.tableId = tableId;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderedAt = orderedAt;
        this.completedAt = completedAt;
        this.createdAt = LocalDateTime.now();
    }
}
