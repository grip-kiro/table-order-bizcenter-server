# Customer Backend — 비즈니스 로직 모델

> Unit 1: Customer Backend의 핵심 비즈니스 로직을 기술합니다.
> 기술 구현이 아닌 비즈니스 관점의 로직 흐름에 집중합니다.

---

## 1. 태블릿 인증 모듈 (Auth)

### 1.1 태블릿 로그인 (US-01)

**입력**: storeId, tableNumber, pin

**로직 흐름**:
1. Store 조회 → 존재하지 않으면 에러 (STORE_NOT_FOUND)
2. RestaurantTable 조회 (store_id + table_number) → 존재하지 않으면 에러 (TABLE_NOT_FOUND)
3. PIN 검증 → 불일치 시 에러 (INVALID_PIN)
4. 기존 세션 무효화 (기기 제한 — US-03):
   - 해당 테이블의 기존 RefreshToken 중 role=TABLE, is_revoked=false인 토큰 모두 무효화 (is_revoked=true)
   - 기존 SSE 연결이 있으면 세션 종료 이벤트 발송
5. 새 세션 생성:
   - UUID 생성 → RestaurantTable.current_session_id 업데이트
   - RestaurantTable.status → OCCUPIED
6. JWT 토큰 발급:
   - Access Token 생성 (claims: storeId, tableId, tableNumber, sessionId, role="TABLE")
   - Refresh Token 생성 (매우 긴 만료 — 태블릿용)
   - RefreshToken 엔티티 DB 저장
7. TokenPair(accessToken, refreshToken) 반환

**출력**: TokenPair { accessToken, refreshToken, expiresIn }

---

### 1.2 Access Token 갱신 (US-02)

**입력**: refreshToken

**로직 흐름**:
1. RefreshToken DB 조회 → 존재하지 않으면 에러 (INVALID_TOKEN)
2. 유효성 검증:
   - is_revoked=true → 에러 (TOKEN_REVOKED)
   - expires_at < now → 에러 (TOKEN_EXPIRED)
3. JWT에서 claims 추출 (storeId, tableId, sessionId, role)
4. RestaurantTable.current_session_id와 sessionId 일치 확인 → 불일치 시 에러 (SESSION_INVALIDATED)
5. 새 Access Token 발급 (동일 claims)
6. (선택) Refresh Token 로테이션: 기존 토큰 무효화 + 새 Refresh Token 발급

**출력**: TokenPair { accessToken, refreshToken, expiresIn }

---

### 1.3 기기 제한 처리 (US-03)

**트리거**: 태블릿 로그인 시 자동 실행 (1.1의 Step 4)

**로직**:
- 동일 테이블에 대한 기존 RefreshToken(role=TABLE, is_revoked=false) 모두 무효화
- 기존 기기의 다음 API 호출 시 Access Token 만료 → Refresh Token 갱신 시도 → 무효화된 토큰이므로 401 → 로그인 화면 전환
- SSE 연결이 있으면 세션 종료 이벤트 발송하여 즉시 감지

---

## 2. 메뉴 조회 모듈 (Menu)

### 2.1 매장 메뉴 전체 조회 (US-05, US-08)

**입력**: storeId

**로직 흐름**:
1. 카테고리 목록 조회 (store_id, display_order ASC)
2. 각 카테고리별 메뉴 조회:
   - 조건: menu_category.category_id = category.id AND menu.is_deleted = false
   - 정렬: menu.display_order ASC
3. 응답 구성:
   - 카테고리 목록 (id, name, displayOrder)
   - 각 카테고리 하위에 메뉴 목록 (id, name, price, imageUrl, isSoldOut, displayOrder)

**캐싱**:
- 캐시 키: `menu:store:{storeId}:category:{categoryId}`
- 캐시 단위: 매장+카테고리별
- 캐시 무효화: Admin Backend에서 메뉴/카테고리 변경 시 (공유 DB 기반 캐시 eviction 또는 TTL)

**출력**: List<CategoryWithMenusDTO>

---

### 2.2 메뉴 상세 조회 (US-06)

**입력**: menuId

**로직 흐름**:
1. Menu 조회 (id, is_deleted=false) → 존재하지 않으면 에러 (MENU_NOT_FOUND)
2. 메뉴 상세 정보 구성: id, name, description, price, imageUrl, isSoldOut
3. 소속 카테고리 목록 조회 (menu_category 조인)

**출력**: MenuDetailDTO { id, name, description, price, imageUrl, isSoldOut, categories }

---

## 3. 주문 모듈 (Order)

### 3.1 주문 생성 (US-14, US-15, US-16)

**입력**: CreateOrderRequest { storeId, tableId, items: [{ menuId, quantity }] }

**로직 흐름**:
1. 인증 정보에서 tableId, sessionId 추출 (JWT claims)
2. RestaurantTable 조회 → current_session_id와 sessionId 일치 확인 → 불일치 시 에러 (SESSION_EXPIRED)
3. 주문 항목 검증 (각 item에 대해):
   a. Menu 조회 (id, is_deleted=false) → 존재하지 않으면 에러 (MENU_NOT_FOUND)
   b. 품절 확인 (is_sold_out=true) → 품절이면 에러 (MENU_SOLD_OUT, 품절 메뉴 목록 반환)
   c. quantity 검증 (1 이상)
   d. 메뉴명, 단가 스냅샷 저장 (menu.name, menu.price)
4. **품절 메뉴가 1개라도 있으면 주문 전체 거부** (품절 메뉴 ID 목록 반환)
5. 총 금액 계산: SUM(quantity × unit_price)
6. Order 엔티티 생성:
   - store_id, table_id, session_id, total_amount, status=PENDING
7. OrderItem 엔티티 생성 (각 항목):
   - menu_id, menu_name(스냅샷), quantity, unit_price(스냅샷), subtotal
8. DB 저장 (트랜잭션)
9. SSE 알림: Admin Backend에 주문 생성 이벤트 전달
   - 공유 DB 기반: 주문 테이블에 INSERT → Admin Backend가 폴링 또는 이벤트 감지
10. OrderDTO 반환 (주문 번호 포함)

**출력**: OrderDTO { orderId, items, totalAmount, status, createdAt }

**에러 응답 (품절 시)**:
```json
{
  "status": 400,
  "message": "품절된 메뉴가 포함되어 있습니다",
  "soldOutMenuIds": [3, 7]
}
```

---

### 3.2 현재 세션 주문 내역 조회 (US-17)

**입력**: tableId (JWT claims에서 추출)

**로직 흐름**:
1. JWT claims에서 tableId, sessionId 추출
2. Order 조회: table_id = tableId AND session_id = sessionId
3. 정렬: created_at ASC (시간 순)
4. 각 주문의 OrderItem 목록 함께 조회
5. 응답 구성

**출력**: List<OrderDTO> (각 주문: orderId, createdAt, items, totalAmount, status)

---

## 4. SSE 모듈

### 4.1 태블릿 SSE 구독 (US-01 세션 종료 감지)

**입력**: tableId (JWT claims에서 추출)

**로직 흐름**:
1. JWT 인증 확인
2. SseEmitter 생성 (timeout 설정)
3. tableId 기반으로 emitter 등록 (메모리 내 Map)
4. 연결 유지 (heartbeat 주기적 전송)
5. 이벤트 수신 시 클라이언트에 전달:
   - `SESSION_END`: 세션 종료 이벤트 → 클라이언트가 로그인 화면으로 전환

**이벤트 형식**:
```
event: SESSION_END
data: {"tableId": 5, "reason": "COMPLETED_BY_ADMIN"}
```

### 4.2 세션 종료 이벤트 발송

**트리거**: Admin Backend에서 이용 완료 처리 시

**메커니즘 (공유 DB 기반)**:
1. Admin Backend가 RestaurantTable.current_session_id = null, status = AVAILABLE로 변경
2. Customer Backend의 SSE 모듈이 감지:
   - 방법 A: DB 폴링 (주기적으로 테이블 상태 확인)
   - 방법 B: 이벤트 테이블 (Admin이 이벤트 INSERT → Customer가 폴링)
3. 세션 종료 감지 시 해당 tableId의 SSE emitter로 SESSION_END 이벤트 전송
4. 해당 테이블의 RefreshToken 무효화
