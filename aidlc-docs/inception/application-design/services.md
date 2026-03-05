# 테이블오더 서비스 — 서비스 레이어 설계

## 서비스 오케스트레이션 패턴

### 주문 생성 플로우
```
Customer App → OrderController.createOrder()
  → OrderService.createOrder()
    → 주문 유효성 검증 (메뉴 존재, 품절 아님, 세션 유효)
    → Order + OrderItems DB 저장
    → SseService.notifyOrderUpdate() → Admin App SSE 푸시
    → OrderDTO 반환
```

### 테이블 이용 완료 플로우
```
Admin App → TableController.completeTableSession()
  → TableService.completeTableSession()
    → 현재 세션 주문 → OrderHistory로 이동
    → 테이블 현재 주문/총액 리셋
    → 테이블 세션 종료
    → AuthService.invalidateSession() → 태블릿 토큰 무효화
    → SseService.notifySessionEnd() → Customer App SSE 푸시
```

### 관리자 로그인 플로우
```
Admin App → AuthController.adminLogin()
  → AuthService.checkLoginAttemptLimit()
    → 5회 초과 시 15분 잠금 → 에러 반환
  → AuthService.authenticateAdmin()
    → 매장ID + 사용자명 + 비밀번호 검증 (bcrypt)
    → JWT Access Token + Refresh Token 발급 (role: "admin")
    → TokenPair 반환
```

### 태블릿 설정 플로우
```
Customer App → AuthController.tableLogin()
  → AuthService.authenticateTable()
    → 매장ID + 테이블번호 + PIN 검증
    → 기존 세션 무효화 (기기 제한)
    → JWT Access Token + Refresh Token 발급 (role: "table")
    → TokenPair 반환
```

### 메뉴 변경 시 캐시 무효화 플로우
```
Admin App → MenuController.createMenu/updateMenu/deleteMenu/updateSoldOut()
  → MenuService.create/update/delete/updateSoldOut()
    → DB 변경
    → MenuService.evictMenuCache(storeId)
```

---

## 서비스 간 의존성

| 서비스 | 의존하는 서비스 |
|--------|----------------|
| AuthService | — (독립) |
| MenuService | — (독립) |
| CategoryService | MenuService (카테고리 삭제 시 메뉴 존재 확인) |
| OrderService | MenuService (주문 시 메뉴 검증), SseService (주문 알림) |
| TableService | OrderService (이용 완료 시 주문 이력 이동), AuthService (세션 무효화), SseService (세션 종료 알림) |
| SseService | — (독립, 이벤트 발행만) |

---

## 횡단 관심사 (Cross-Cutting Concerns)

### 인증/인가
- Spring Security + JWT Filter
- role 기반 접근 제어: "table" (고객 API), "admin" (관리자 API)
- Access Token 검증 → 만료 시 401 → 클라이언트에서 Refresh Token으로 갱신

### 에러 처리
- Global Exception Handler (@ControllerAdvice)
- 표준 에러 응답 형식 (status, message, timestamp)

### 캐싱
- Spring Cache (메뉴 조회)
- 메뉴 변경 시 @CacheEvict
