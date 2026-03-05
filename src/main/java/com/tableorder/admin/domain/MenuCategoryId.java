package com.tableorder.admin.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MenuCategoryId implements Serializable {
    private Long menuId;
    private Long categoryId;
}
