package com.tableorder.admin.repository;

import com.tableorder.admin.domain.OrderHistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryItemRepository extends JpaRepository<OrderHistoryItem, Long> {
}
