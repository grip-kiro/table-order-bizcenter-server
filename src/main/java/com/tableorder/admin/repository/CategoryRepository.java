package com.tableorder.admin.repository;

import com.tableorder.admin.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStoreIdOrderByDisplayOrder(Long storeId);

    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM Category c WHERE c.storeId = :storeId")
    int findMaxDisplayOrderByStoreId(Long storeId);

    Optional<Category> findByIdAndStoreId(Long id, Long storeId);
}
