package com.tableorder.admin.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminPrincipal {
    private final Long storeId;
    private final Long adminId;
    private final String username;
    private final String role;
}
