package com.tableorder.admin.domain;

public enum OrderStatus {
    PENDING,
    PREPARING,
    COMPLETED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PREPARING;
            case PREPARING -> next == COMPLETED;
            case COMPLETED -> false;
        };
    }
}
