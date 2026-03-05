package com.tableorder.admin.menu.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateMenuRequest {

    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 100, message = "메뉴명은 100자 이내여야 합니다")
    private String name;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    @Max(value = 1000000, message = "가격은 1,000,000 이하여야 합니다")
    private Integer price;

    private String description;

    @Size(max = 500, message = "이미지 URL은 500자 이내여야 합니다")
    private String imageUrl;

    @NotEmpty(message = "카테고리는 1개 이상 선택해야 합니다")
    private List<Long> categoryIds;
}
