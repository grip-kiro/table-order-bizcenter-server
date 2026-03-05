package com.tableorder.admin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminRegisterResponse {
    private final Long id;
    private final Long storeId;
    private final String username;
}
