# Customer Backend — 비즈니스 규칙

> Unit 1: Customer Backend에 적용되는 비즈니스 규칙, 검증 로직, 제약 조건을 정의합니다.

---

## 1. 인증 규칙 (Auth Rules)

### BR-AUTH-01: 마스터 PIN 검증
- 마스터 PIN은 Store.master_pin에 저장
- 태블릿 설정 모드 진입 시 마스터 PIN 일치 필수
- 검증 실패 시: 에러 반환 (구체적 실패 사유 노출하지 않음 — "인증 실패")

### BR-AUTH-02: 테이블 PIN 검증
- RestaurantTable.pin과 입력 PIN 비교
- PIN 형식: 숫자 4~6자리
- 검증 실패 시: 에러 반환 ("인증 실패")

### BR-AUTH-03: 기기 제한 (1 테이블 = 1 기기)
- 새 기기 로그인 시 기존 세션의 모든 RefreshToken 무효화 (is_revoked=true)
- 기존 기기는 다음 API 호출 시 401 응답 수신
- SSE 연결이 있으면 SESSION_END 이벤트 발송

### BR-AUTH-04: 세션 유효성
- 모든 API 호출 시 JWT의 sessionId와 RestaurantTable.current_session_id 일치 확인
- 불일치 시: 401 응답 (세션 만료)

### BR-AUTH-05: JWT 토큰 구조
- Access Token claims: storeId, tableId, tableNumber, sessionId, role("TABLE")
- Access Token 만료: 짧은 시간 (예: 30분)
- Refresh Token 만료: 매우 긴 시간 (태블릿용, 예: 365일)
- 토큰 저장: 쿠키 (Secure, HttpOnly, SameSite)

### BR-AUTH-06: Refresh Token 검증
- DB에 존재해야 함
- is_revoked = false
- expires_at > 현재 시각
- 연관된 세션이 유효해야 함 (current_session_id 일치)

---

## 2. 메뉴 규칙 (Menu Rules)

### BR-MENU-01: 메뉴 조회 필터링
- is_deleted = false인 메뉴만 조회 (소프트 삭제된 메뉴 제외)
- 카테고리 순서: Category.display_order ASC
- 메뉴 순서: Menu.display_order ASC

### BR-MENU-02: 품절 메뉴 표시
- is_sold_out = true인 메뉴는 목록에 포함하되 품절 상태 표시
- 품절 메뉴는 주문 불가 (주문 생성 시 검증)

### BR-MENU-03: 캐싱 정책
- 캐시 키: `menu:store:{storeId}:category:{categoryId}`
- 캐시 단위: 매장 + 카테고리별
- 캐시 무효화 조건:
  - 메뉴 등록/수정/삭제 (Admin Backend)
  - 품절 상태 변경 (Admin Backend)
  - 카테고리 변경 (Admin Backend)
- 무효화 방식: TTL 기반 (Admin Backend와 별도 프로세스이므로 직접 eviction 불가) 또는 공유 캐시 사용

### BR-MENU-04: 이미지 처리
- image_url이 NULL이면 클라이언트에서 플레이스홀더 표시 (서버는 null 반환)
- image_url 검증 없음 (관리자 입력 그대로 저장/반환)

---

## 3. 주문 규칙 (Order Rules)

### BR-ORDER-01: 주문 생성 전제 조건
- 유효한 세션 필수 (JWT sessionId = RestaurantTable.current_session_id)
- 주문 항목 1개 이상 필수
- 빈 주문 불가

### BR-ORDER-02: 주문 항목 검증
- 각 항목의 menuId가 유효해야 함 (존재 + is_deleted=false)
- 각 항목의 quantity ≥ 1
- 품절 메뉴(is_sold_out=true) 포함 시 **주문 전체 거부**
  - 에러 응답에 품절 메뉴 ID 목록 포함

### BR-ORDER-03: 가격 스냅샷
- 주문 시점의 메뉴명(menu_name)과 단가(unit_price)를 OrderItem에 저장
- 이후 메뉴 가격이 변경되어도 주문 내역의 가격은 변하지 않음

### BR-ORDER-04: 총 금액 계산
- total_amount = SUM(각 OrderItem의 subtotal)
- subtotal = quantity × unit_price
- 서버에서 계산 (클라이언트 전송 값 무시)

### BR-ORDER-05: 주문 번호
- 자동 증가 숫자 (DB AUTO_INCREMENT)
- 전역 고유 (매장 간 중복 없음)

### BR-ORDER-06: 주문 상태 전이
```
PENDING(대기중) → PREPARING(준비중) → COMPLETED(완료)
```
- 초기 상태: PENDING
- 상태 변경: Admin Backend에서만 수행 (Customer Backend는 읽기만)
- 역방향 전이 불가

### BR-ORDER-07: 세션 기반 주문 조회
- 현재 세션(session_id)의 주문만 조회 가능
- 이전 세션(이용 완료 처리된)의 주문은 조회 불가
- 정렬: created_at ASC

---

## 4. SSE 규칙

### BR-SSE-01: 태블릿 SSE 이벤트
- 수신 이벤트: SESSION_END만 (최소 구현)
- 주문 상태 변경 이벤트는 MVP에서 미포함

### BR-SSE-02: SSE 연결 관리
- 인증된 태블릿만 SSE 구독 가능 (JWT 검증)
- 연결 끊김 시 emitter 자동 제거
- Heartbeat 주기적 전송 (연결 유지)

### BR-SSE-03: 세션 종료 감지
- Admin Backend가 이용 완료 처리 → DB 상태 변경
- Customer Backend가 DB 폴링으로 감지 → SSE 이벤트 발송
- Fallback: 클라이언트 API 호출 시 401 응답으로 감지

---

## 5. 데이터 검증 규칙 (Validation)

### BR-VAL-01: 태블릿 로그인 요청
| 필드 | 규칙 |
|------|------|
| storeId | NOT NULL, 양의 정수 |
| tableNumber | NOT NULL, 양의 정수 |
| pin | NOT NULL, 숫자 4~6자리 |

### BR-VAL-02: 주문 생성 요청
| 필드 | 규칙 |
|------|------|
| items | NOT NULL, 1개 이상 |
| items[].menuId | NOT NULL, 양의 정수 |
| items[].quantity | NOT NULL, 1 이상 |

### BR-VAL-03: Refresh Token 요청
| 필드 | 규칙 |
|------|------|
| refreshToken | NOT NULL, 비어있지 않은 문자열 |

---

## 6. 에러 코드 정의

| 코드 | HTTP | 설명 |
|------|------|------|
| STORE_NOT_FOUND | 404 | 매장을 찾을 수 없음 |
| TABLE_NOT_FOUND | 404 | 테이블을 찾을 수 없음 |
| INVALID_PIN | 401 | PIN 불일치 |
| INVALID_MASTER_PIN | 401 | 마스터 PIN 불일치 |
| INVALID_TOKEN | 401 | 유효하지 않은 토큰 |
| TOKEN_REVOKED | 401 | 무효화된 토큰 |
| TOKEN_EXPIRED | 401 | 만료된 토큰 |
| SESSION_EXPIRED | 401 | 세션 만료 |
| MENU_NOT_FOUND | 404 | 메뉴를 찾을 수 없음 |
| MENU_SOLD_OUT | 400 | 품절 메뉴 포함 |
| INVALID_ORDER | 400 | 잘못된 주문 요청 |
| EMPTY_ORDER | 400 | 빈 주문 |
| INTERNAL_ERROR | 500 | 서버 내부 오류 |

**에러 응답 형식**:
```json
{
  "status": 400,
  "code": "MENU_SOLD_OUT",
  "message": "품절된 메뉴가 포함되어 있습니다",
  "timestamp": "2026-03-05T10:30:00Z",
  "details": { "soldOutMenuIds": [3, 7] }
}
```
