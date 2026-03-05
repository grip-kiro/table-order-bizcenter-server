package com.tableorder.admin.menu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DisplayOrderListRequest {

    @NotEmpty(message = "순서 변경 항목은 1개 이상이어야 합니다")
    @Valid
    private List<DisplayOrderRequest> items;
}
