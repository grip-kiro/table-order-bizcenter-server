package com.tableorder.admin.table.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTableRequest {

    @NotNull(message = "테이블 번호는 필수입니다")
    @Positive(message = "테이블 번호는 양의 정수여야 합니다")
    private Integer tableNumber;

    @NotNull(message = "PIN은 필수입니다")
    @Pattern(regexp = "^\\d{4,6}$", message = "PIN은 숫자 4~6자리여야 합니다")
    private String pin;
}
