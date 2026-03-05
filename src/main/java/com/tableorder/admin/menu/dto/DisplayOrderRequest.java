package com.tableorder.admin.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisplayOrderRequest {

    @NotNull
    private Long id;

    @NotNull
    @Min(0)
    private Integer displayOrder;
}
