package com.tableorder.admin.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(MenuCategoryId.class)
public class MenuCategory {

    @Id
    @Column(name = "menu_id")
    private Long menuId;

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public MenuCategory(Long menuId, Long categoryId) {
        this.menuId = menuId;
        this.categoryId = categoryId;
    }
}
