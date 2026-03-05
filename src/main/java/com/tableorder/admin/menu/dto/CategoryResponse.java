package com.tableorder.admin.menu.dto;

import com.tableorder.admin.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
    private final Long id;
    private final String name;
    private final Integer displayOrder;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDisplayOrder());
    }
}
