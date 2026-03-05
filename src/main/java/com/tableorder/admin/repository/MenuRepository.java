package com.tableorder.admin.repository;

import com.tableorder.admin.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStoreIdOrderByDisplayOrder(Long storeId);

    Optional<Menu> findByIdAndStoreId(Long id, Long storeId);

    @Query("SELECT COALESCE(MAX(m.displayOrder), 0) FROM Menu m WHERE m.storeId = :storeId")
    int findMaxDisplayOrderByStoreId(Long storeId);
}
