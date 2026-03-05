package com.tableorder.admin.domain;

import com.tableorder.admin.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant_tables",
        uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "table_number"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(nullable = false, length = 10)
    private String pin;

    @Column(name = "current_session_id", length = 36)
    private String currentSessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;

    @Builder
    public RestaurantTable(Long storeId, Integer tableNumber, String pin) {
        this.storeId = storeId;
        this.tableNumber = tableNumber;
        this.pin = pin;
        this.status = TableStatus.AVAILABLE;
    }

    public void update(Integer tableNumber, String pin) {
        if (tableNumber != null) this.tableNumber = tableNumber;
        if (pin != null) this.pin = pin;
    }

    public void clearSession() {
        this.currentSessionId = null;
        this.status = TableStatus.AVAILABLE;
    }

    public boolean hasActiveSession() {
        return this.currentSessionId != null;
    }
}
