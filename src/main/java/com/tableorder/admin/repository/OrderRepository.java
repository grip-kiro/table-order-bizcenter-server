package com.tableorder.admin.repository;

import com.tableorder.admin.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.tableId = :tableId AND o.sessionId = :sessionId " +
           "AND o.deleted = false ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByTableSession(Long tableId, String sessionId);

    Optional<Order> findByIdAndStoreId(Long id, Long storeId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.tableId = :tableId AND o.sessionId = :sessionId AND o.deleted = false")
    int sumTotalAmountByTableSession(Long tableId, String sessionId);
}
