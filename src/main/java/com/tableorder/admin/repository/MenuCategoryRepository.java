package com.tableorder.admin.repository;

import com.tableorder.admin.domain.MenuCategory;
import com.tableorder.admin.domain.MenuCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, MenuCategoryId> {
    void deleteByMenuId(Long menuId);
    List<MenuCategory> findByMenuId(Long menuId);

    @Query("SELECT COUNT(mc) > 0 FROM MenuCategory mc JOIN Menu m ON mc.menuId = m.id " +
           "WHERE mc.categoryId = :categoryId AND m.deleted = false")
    boolean existsActiveMenuByCategoryId(Long categoryId);
}
