package com.tableorder.admin.repository;

import com.tableorder.admin.domain.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    @Query("SELECT oh FROM OrderHistory oh WHERE oh.tableId = :tableId " +
           "AND (:from IS NULL OR oh.completedAt >= :from) " +
           "AND (:to IS NULL OR oh.completedAt <= :to) " +
           "ORDER BY oh.completedAt DESC")
    List<OrderHistory> findByTableIdAndDateRange(Long tableId, LocalDateTime from, LocalDateTime to);
}
