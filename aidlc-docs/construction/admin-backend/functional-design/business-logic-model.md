# Admin Backend — 비즈니스 로직 모델

> Unit 2: Admin Backend의 핵심 비즈니스 로직을 기술합니다.

---

## 1. 관리자 인증 모듈 (Auth)

### 1.1 관리자 로그인 (US-18, US-19)

**입력**: storeId, username, password

**로직 흐름**:
1. AdminAccount 조회 (store_id + username) → 존재하지 않으면 에러 (INVALID_CREDENTIALS)
2. 계정 잠금 확인:
   - is_locked=true AND lock_until > now → 에러 (ACCOUNT_LOCKED, 남은 잠금 시간 반환)
   - is_locked=true AND lock_until ≤ now → 잠금 해제 (is_locked=false, failed_attempts 유지)
3. 비밀번호 검증 (bcrypt):
   - 실패 시:
     a. failed_attempts += 1
     b. failed_attempts ≥ 5 → is_locked=true, lock_until=now+15분
     c. 에러 반환 (INVALID_CREDENTIALS, 남은 시도 횟수 포함)
   - 성공 시:
     a. failed_attempts = 0 (초기화)
     b. 다음 단계 진행
4. JWT 토큰 발급:
   - Access Token (claims: storeId, adminId, username, role="ADMIN", 만료: 30분)
   - Refresh Token (만료: 16시간)
   - RefreshToken 엔티티 DB 저장
5. TokenPair 반환

**출력**: TokenPair { accessToken, refreshToken, expiresIn }

---

### 1.2 Access Token 갱신

**입력**: refreshToken

**로직 흐름**: Unit 1의 1.2와 동일 구조
1. RefreshToken DB 조회 → 유효성 검증
2. role="ADMIN" 확인
3. 새 Access Token 발급

---

### 1.3 로그아웃

**입력**: refreshToken (쿠키에서 추출)

**로직 흐름**:
1. RefreshToken 조회
2. is_revoked = true로 변경
3. 클라이언트 쿠키 삭제 지시

---

## 2. 주문 모니터링 모듈 (Order)

### 2.1 테이블별 주문 조회 (US-20)

**입력**: tableId

**로직 흐름**:
1. JWT에서 storeId 추출
2. RestaurantTable 조회 → 해당 매장 소속 확인
3. 현재 세션 주문 조회:
   - Order WHERE table_id=tableId AND session_id=current_session_id AND is_deleted=false
   - 정렬: created_at DESC
4. 각 주문의 OrderItem 함께 조회
5. 테이블 총 주문액 계산: SUM(order.total_amount)

**출력**: TableOrdersDTO { tableId, tableNumber, totalAmount, orders: List<OrderDTO> }

---

### 2.2 주문 상태 변경 (US-22)

**입력**: orderId, newStatus

**로직 흐름**:
1. Order 조회 → 존재 확인 + 매장 소속 확인
2. 상태 전이 검증:
   - PENDING → PREPARING ✅
   - PREPARING → COMPLETED ✅
   - 그 외 → 에러 (INVALID_STATUS_TRANSITION)
3. Order.status 업데이트
4. OrderDTO 반환

**출력**: OrderDTO

---

### 2.3 주문 삭제 (US-25)

**입력**: orderId

**로직 흐름**:
1. Order 조회 → 존재 확인 + 매장 소속 확인
2. Order.is_deleted = true (소프트 삭제)
3. 테이블 총 주문액 재계산 필요 (클라이언트에서 처리 또는 응답에 포함)

**출력**: void (204 No Content)

---

### 2.4 과거 주문 내역 조회 (US-27)

**입력**: tableId, dateFrom (optional), dateTo (optional)

**로직 흐름**:
1. JWT에서 storeId 추출
2. RestaurantTable 조회 → 매장 소속 확인
3. OrderHistory 조회:
   - WHERE table_id=tableId
   - 날짜 필터 적용 (dateFrom, dateTo가 있으면)
   - 정렬: completed_at DESC
4. 각 이력의 OrderHistoryItem 함께 조회

**출력**: List<OrderHistoryDTO>

---

## 3. 테이블 관리 모듈 (Table)

### 3.1 테이블 목록 조회

**입력**: storeId (JWT에서 추출)

**로직 흐름**:
1. RestaurantTable 조회 (store_id=storeId)
2. 각 테이블의 현재 세션 주문 요약 포함 (총 주문액, 주문 수)

**출력**: List<TableDTO>

---

### 3.2 테이블 등록 (US-24)

**입력**: CreateTableRequest { tableNumber, pin }

**로직 흐름**:
1. JWT에서 storeId 추출
2. 중복 확인: (store_id, table_number) UNIQUE → 중복 시 에러 (TABLE_NUMBER_DUPLICATE)
3. PIN 형식 검증: 숫자 4~6자리
4. RestaurantTable 생성 (status=AVAILABLE, current_session_id=null)

**출력**: TableDTO

---

### 3.3 테이블 수정

**입력**: tableId, UpdateTableRequest { tableNumber, pin }

**로직 흐름**:
1. RestaurantTable 조회 → 매장 소속 확인
2. tableNumber 변경 시 중복 확인
3. 필드 업데이트

**출력**: TableDTO

---

### 3.4 테이블 삭제

**입력**: tableId

**로직 흐름**:
1. RestaurantTable 조회 → 매장 소속 확인
2. 활성 세션 확인 (current_session_id != null) → 활성 세션 있으면 에러 (TABLE_IN_USE)
3. 물리 삭제

**출력**: void (204 No Content)

---

### 3.5 테이블 이용 완료 (US-26)

**입력**: tableId

**로직 흐름**:
1. RestaurantTable 조회 → 매장 소속 확인
2. 활성 세션 확인 (current_session_id != null) → 없으면 에러 (NO_ACTIVE_SESSION)
3. 현재 세션 주문 → OrderHistory로 복사:
   a. Order 조회 (table_id=tableId, session_id=current_session_id, is_deleted=false)
   b. 각 Order에 대해 OrderHistory + OrderHistoryItem 생성 (completed_at=now)
4. 테이블 세션 리셋:
   - current_session_id = null
   - status = AVAILABLE
5. 토큰 무효화:
   - 해당 테이블의 RefreshToken (role=TABLE, is_revoked=false) 모두 무효화
6. SSE 세션 종료 알림:
   - Customer Backend의 SSE로 SESSION_END 이벤트 전달
   - 방식: DB 이벤트 테이블에 INSERT 또는 테이블 상태 변경으로 Customer Backend가 감지

**출력**: void (200 OK)

---

## 4. 메뉴/카테고리 관리 모듈 (Menu)

### 4.1 카테고리 조회

**입력**: storeId (JWT에서 추출)

**로직 흐름**:
1. Category 조회 (store_id=storeId, display_order ASC)

**출력**: List<CategoryDTO>

---

### 4.2 카테고리 등록 (US-28)

**입력**: CreateCategoryRequest { name }

**로직 흐름**:
1. JWT에서 storeId 추출
2. display_order 결정: 현재 최대값 + 1
3. Category 생성

**출력**: CategoryDTO

---

### 4.3 카테고리 수정 (US-28)

**입력**: categoryId, UpdateCategoryRequest { name }

**로직 흐름**:
1. Category 조회 → 매장 소속 확인
2. name 업데이트

**출력**: CategoryDTO

---

### 4.4 카테고리 삭제 (US-28)

**입력**: categoryId

**로직 흐름**:
1. Category 조회 → 매장 소속 확인
2. 메뉴 존재 확인: MenuCategory에 해당 category_id가 있는지 (is_deleted=false인 메뉴만)
   - 메뉴 있으면 → 에러 (CATEGORY_HAS_MENUS)
3. Category 물리 삭제

**출력**: void (204 No Content)

---

### 4.5 카테고리 순서 변경 (US-28)

**입력**: List<CategoryOrderRequest> [{ categoryId, displayOrder }]

**로직 흐름**:
1. 각 categoryId → 매장 소속 확인
2. display_order 일괄 업데이트

**출력**: List<CategoryDTO>

---

### 4.6 메뉴 조회 (관리자용)

**입력**: storeId (JWT에서 추출)

**로직 흐름**:
1. Menu 조회 (store_id=storeId) — **is_deleted 포함** (관리자는 삭제된 메뉴도 볼 수 있음)
2. 각 메뉴의 카테고리 목록 포함
3. 정렬: display_order ASC

**출력**: List<MenuDTO> (is_deleted 필드 포함)

---

### 4.7 메뉴 등록 (US-29)

**입력**: CreateMenuRequest { name, price, description, imageUrl, categoryIds }

**로직 흐름**:
1. JWT에서 storeId 추출
2. 입력 검증:
   - name: NOT NULL, 비어있지 않음
   - price: 0 ~ 1,000,000
   - categoryIds: 1개 이상 필수, 각 ID가 해당 매장 소속인지 확인
3. display_order 결정: 현재 최대값 + 1
4. Menu 생성
5. MenuCategory 레코드 생성 (각 categoryId에 대해)
6. 캐시 무효화: 관련 카테고리의 메뉴 캐시 evict

**출력**: MenuDTO

---

### 4.8 메뉴 수정 (US-30)

**입력**: menuId, UpdateMenuRequest { name, price, description, imageUrl, categoryIds }

**로직 흐름**:
1. Menu 조회 → 매장 소속 확인, is_deleted=false 확인
2. 입력 검증 (4.7과 동일)
3. Menu 필드 업데이트
4. MenuCategory 갱신: 기존 연결 삭제 → 새 연결 생성
5. 캐시 무효화

**출력**: MenuDTO

---

### 4.9 메뉴 소프트 삭제 (US-31)

**입력**: menuId

**로직 흐름**:
1. Menu 조회 → 매장 소속 확인
2. is_deleted = true (카테고리 연결은 유지)
3. 캐시 무효화

**출력**: void (204 No Content)

---

### 4.10 메뉴 품절 설정/해제 (US-32)

**입력**: menuId, isSoldOut (boolean)

**로직 흐름**:
1. Menu 조회 → 매장 소속 확인, is_deleted=false 확인
2. is_sold_out 업데이트
3. 캐시 무효화

**출력**: MenuDTO

---

### 4.11 메뉴 노출 순서 변경 (US-33)

**입력**: List<MenuOrderRequest> [{ menuId, displayOrder }]

**로직 흐름**:
1. 각 menuId → 매장 소속 확인
2. display_order 일괄 업데이트
3. 캐시 무효화

**출력**: List<MenuDTO>

---

## 5. SSE 모듈

### 5.1 관리자 SSE 구독 (US-21)

**입력**: storeId (JWT에서 추출)

**로직 흐름**:
1. JWT 인증 확인 (role=ADMIN)
2. SseEmitter 생성 (timeout 설정)
3. storeId 기반으로 emitter 등록
4. Heartbeat 주기적 전송

**이벤트 종류**:
- `ORDER_CREATED`: 신규 주문 생성 시

**이벤트 형식**:
```
event: ORDER_CREATED
data: {"orderId":1,"tableId":5,"tableNumber":3,"totalAmount":25000,"items":[...],"createdAt":"..."}
```

### 5.2 주문 생성 이벤트 감지

**메커니즘 (공유 DB 기반)**:
1. Customer Backend에서 주문 INSERT
2. Admin Backend가 감지:
   - 방법 A: DB 폴링 (주기적으로 새 주문 확인)
   - 방법 B: 이벤트 테이블 (Customer Backend가 이벤트 INSERT → Admin Backend가 폴링)
3. 감지 시 해당 storeId의 SSE emitter로 ORDER_CREATED 이벤트 전송
