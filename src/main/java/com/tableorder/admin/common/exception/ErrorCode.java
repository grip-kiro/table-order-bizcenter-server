package com.tableorder.admin.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다"),
    USERNAME_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다"),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "계정이 잠겼습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "무효화된 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),

    // Table
    TABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "테이블을 찾을 수 없습니다"),
    TABLE_NUMBER_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 테이블 번호입니다"),
    TABLE_IN_USE(HttpStatus.CONFLICT, "활성 세션이 있는 테이블입니다"),
    NO_ACTIVE_SESSION(HttpStatus.BAD_REQUEST, "활성 세션이 없습니다"),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "허용되지 않는 상태 전이입니다"),

    // Menu
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"),
    MENU_DELETED(HttpStatus.BAD_REQUEST, "삭제된 메뉴입니다"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"),
    CATEGORY_HAS_MENUS(HttpStatus.CONFLICT, "메뉴가 연결된 카테고리는 삭제할 수 없습니다"),

    // Common
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    INVALID_PIN_FORMAT(HttpStatus.BAD_REQUEST, "PIN 형식이 올바르지 않습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
