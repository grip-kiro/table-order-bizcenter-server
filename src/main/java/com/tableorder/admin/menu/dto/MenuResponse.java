package com.tableorder.admin.menu.dto;

import com.tableorder.admin.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MenuResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final Integer price;
    private final String imageUrl;
    private final boolean soldOut;
    private final boolean deleted;
    private final Integer displayOrder;
    private final List<CategoryResponse> categories;

    public static MenuResponse from(Menu menu, List<CategoryResponse> categories) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .imageUrl(menu.getImageUrl())
                .soldOut(menu.isSoldOut())
                .deleted(menu.isDeleted())
                .displayOrder(menu.getDisplayOrder())
                .categories(categories)
                .build();
    }
}
