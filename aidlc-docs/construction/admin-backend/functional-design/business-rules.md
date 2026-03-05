# Admin Backend — 비즈니스 규칙

> Unit 2: Admin Backend에 적용되는 비즈니스 규칙, 검증 로직, 제약 조건을 정의합니다.
> Unit 1 (Customer Backend)의 공통 규칙은 해당 문서를 참조하며, 여기서는 Admin 고유 규칙만 정의합니다.

---

## 1. 인증 규칙 (Auth Rules)

### BR-ADMIN-AUTH-01: 관리자 로그인 자격 증명
- AdminAccount 조회 시 (store_id + username) 복합 조건 사용
- 계정 미존재 시에도 "인증 실패" 일반 메시지 반환 (계정 존재 여부 노출 금지)
- 비밀번호 검증: bcrypt 해시 비교

### BR-ADMIN-AUTH-02: 로그인 시도 제한
- 동일 계정 기준 연속 실패 5회 시 계정 잠금
- 잠금 기간: 15분 (lock_until = now + 15분)
- 잠금 중 로그인 시도 시: ACCOUNT_LOCKED 에러 + 남은 잠금 시간 반환
- 잠금 해제 조건: lock_until ≤ 현재 시각 (자동 해제)
- 실패 횟수 초기화: 로그인 성공 시에만 (failed_attempts = 0)
- 잠금 해제 후에도 failed_attempts는 유지 (성공 시에만 초기화)

### BR-ADMIN-AUTH-03: JWT 토큰 구조
- Access Token claims: storeId, adminId, username, role("ADMIN")
- Access Token 만료: 30분
- Refresh Token 만료: 16시간 (관리자 세션 유지 시간)
- 토큰 저장: 쿠키 (Secure, HttpOnly, SameSite)

### BR-ADMIN-AUTH-04: 세션 관리
- 16시간 세션 유지 (Refresh Token 만료 기준)
- 브라우저 새로고침 시 세션 유지 (쿠키 기반)
- 16시간 경과 후 자동 로그아웃 (Refresh Token 만료)
- 명시적 로그아웃 시 RefreshToken.is_revoked = true

### BR-ADMIN-AUTH-05: Refresh Token 검증
- DB에 존재해야 함
- is_revoked = false
- expires_at > 현재 시각
- role = "ADMIN" 확인

---

## 2. 주문 관리 규칙 (Order Rules)

### BR-ADMIN-ORDER-01: 주문 조회 범위
- 현재 세션(current_session_id) 주문만 대시보드에 표시
- is_deleted = false인 주문만 표시
- 정렬: created_at DESC (최신 주문 우선)

### BR-ADMIN-ORDER-02: 주문 상태 전이
```
PENDING(대기중) → PREPARING(준비중) → COMPLETED(완료)
```
- 허용 전이: PENDING → PREPARING, PREPARING → COMPLETED
- 역방향 전이 불가 (COMPLETED → PREPARING ✗, PREPARING → PENDING ✗)
- 동일 상태 전이 불가 (PENDING → PENDING ✗)
- is_deleted = true인 주문의 상태 변경 불가

### BR-ADMIN-ORDER-03: 주문 삭제 (소프트 삭제)
- Order.is_deleted = true로 변경 (물리 삭제 아님)
- 삭제된 주문은 대시보드에서 제외
- 삭제된 주문은 테이블 총 주문액 계산에서 제외
- 삭제된 주문도 이용 완료 시 OrderHistory로 복사하지 않음

### BR-ADMIN-ORDER-04: 테이블 총 주문액 계산
- SUM(Order.total_amount) WHERE session_id = current_session_id AND is_deleted = false
- 주문 삭제/이용 완료 시 재계산 필요

### BR-ADMIN-ORDER-05: 과거 주문 내역
- OrderHistory 테이블에서 조회
- 날짜 필터링: completed_at 기준 (dateFrom ~ dateTo)
- 정렬: completed_at DESC (최근 이용 완료 우선)

---

## 3. 테이블 관리 규칙 (Table Rules)

### BR-ADMIN-TABLE-01: 테이블 등록
- (store_id, table_number) 유니크 제약 — 중복 불가
- PIN 형식: 숫자 4~6자리 (정규식: `^\d{4,6}$`)
- 초기 상태: status = AVAILABLE, current_session_id = null

### BR-ADMIN-TABLE-02: 테이블 수정
- 매장 소속 확인 필수 (JWT storeId와 테이블의 store_id 일치)
- tableNumber 변경 시 중복 확인 필수
- PIN 변경 시 형식 검증 필수

### BR-ADMIN-TABLE-03: 테이블 삭제
- 활성 세션(current_session_id != null)이 있는 테이블은 삭제 불가
- 물리 삭제 (DB에서 완전 제거)

### BR-ADMIN-TABLE-04: 테이블 이용 완료 처리
- 활성 세션이 없으면 에러 (NO_ACTIVE_SESSION)
- 처리 순서 (트랜잭션):
  1. 현재 세션의 is_deleted=false 주문 → OrderHistory + OrderHistoryItem으로 복사
  2. RestaurantTable.current_session_id = null, status = AVAILABLE
  3. 해당 테이블의 RefreshToken (role=TABLE, is_revoked=false) 모두 무효화
- 이용 완료 후 해당 테이블의 현재 주문 목록/총 주문액은 0으로 리셋
- Customer Backend SSE로 SESSION_END 이벤트 전달 (DB 상태 변경 기반)

### BR-ADMIN-TABLE-05: 매장 소속 검증
- 모든 테이블 관련 API에서 JWT의 storeId와 대상 테이블의 store_id 일치 확인
- 불일치 시: 403 Forbidden 또는 404 Not Found

---

## 4. 메뉴 관리 규칙 (Menu Rules)

### BR-ADMIN-MENU-01: 메뉴 조회 (관리자용)
- is_deleted 포함 전체 메뉴 조회 (관리자는 삭제된 메뉴도 확인 가능)
- 각 메뉴의 카테고리 목록 포함
- 정렬: display_order ASC

### BR-ADMIN-MENU-02: 메뉴 등록 검증
| 필드 | 규칙 |
|------|------|
| name | NOT NULL, 비어있지 않음, VARCHAR(100) |
| price | NOT NULL, 0 ~ 1,000,000 (원 단위) |
| description | NULLABLE, TEXT |
| imageUrl | NULLABLE, VARCHAR(500) |
| categoryIds | NOT NULL, 1개 이상, 각 ID가 해당 매장 소속 |

### BR-ADMIN-MENU-03: 메뉴 수정
- is_deleted = true인 메뉴는 수정 불가
- 매장 소속 확인 필수
- 카테고리 변경 시: 기존 MenuCategory 삭제 → 새 연결 생성

### BR-ADMIN-MENU-04: 메뉴 소프트 삭제
- is_deleted = true로 변경 (물리 삭제 아님)
- MenuCategory 연결은 유지 (is_deleted=true인 메뉴도 카테고리 관계 보존)
- 삭제된 메뉴가 포함된 기존 주문 내역은 스냅샷(menu_name, unit_price)으로 보존

### BR-ADMIN-MENU-05: 품절 설정/해제
- is_deleted = true인 메뉴는 품절 설정 불가
- 품절 설정 시: 고객 화면에서 주문 불가 처리
- 품절 해제 시: 고객 화면에서 정상 주문 가능

### BR-ADMIN-MENU-06: 노출 순서 변경
- display_order 일괄 업데이트
- 매장 소속 확인 필수 (각 menuId)

### BR-ADMIN-MENU-07: 캐시 무효화
- 메뉴 등록/수정/삭제 시 관련 카테고리의 캐시 무효화
- 품절 상태 변경 시 캐시 무효화
- 순서 변경 시 캐시 무효화
- 무효화 대상: `menu:store:{storeId}:category:{categoryId}`

---

## 5. 카테고리 관리 규칙 (Category Rules)

### BR-ADMIN-CAT-01: 카테고리 등록
- name: NOT NULL, 비어있지 않음, VARCHAR(50)
- display_order: 자동 결정 (현재 최대값 + 1)

### BR-ADMIN-CAT-02: 카테고리 삭제 제약
- 활성 메뉴(is_deleted=false)가 연결된 카테고리는 삭제 불가 (CATEGORY_HAS_MENUS)
- 활성 메뉴가 없으면 물리 삭제

### BR-ADMIN-CAT-03: 카테고리 순서 변경
- display_order 일괄 업데이트
- 매장 소속 확인 필수 (각 categoryId)

---

## 6. SSE 규칙

### BR-ADMIN-SSE-01: 관리자 SSE 이벤트
- 수신 이벤트: ORDER_CREATED만 (신규 주문 생성)
- 주문 상태 변경/삭제 이벤트는 MVP에서 미포함

### BR-ADMIN-SSE-02: SSE 연결 관리
- 인증된 관리자만 SSE 구독 가능 (JWT role=ADMIN 검증)
- storeId 기반 emitter 등록
- 연결 끊김 시 emitter 자동 제거
- Heartbeat 주기적 전송 (연결 유지)

### BR-ADMIN-SSE-03: 주문 생성 이벤트 감지
- 공유 DB 기반 폴링으로 신규 주문 감지
- 감지 시 해당 storeId의 SSE emitter로 ORDER_CREATED 이벤트 전송
- 목표: 2초 이내 대시보드 반영

---

## 7. 데이터 검증 규칙 (Validation)

### BR-ADMIN-VAL-01: 관리자 로그인 요청
| 필드 | 규칙 |
|------|------|
| storeId | NOT NULL, 양의 정수 |
| username | NOT NULL, 비어있지 않은 문자열 |
| password | NOT NULL, 비어있지 않은 문자열 |

### BR-ADMIN-VAL-02: 테이블 등록/수정 요청
| 필드 | 규칙 |
|------|------|
| tableNumber | NOT NULL, 양의 정수 |
| pin | NOT NULL, 숫자 4~6자리 (`^\d{4,6}$`) |

### BR-ADMIN-VAL-03: 메뉴 등록/수정 요청
| 필드 | 규칙 |
|------|------|
| name | NOT NULL, 1~100자 |
| price | NOT NULL, 0 ~ 1,000,000 |
| description | NULLABLE |
| imageUrl | NULLABLE, 최대 500자 |
| categoryIds | NOT NULL, 1개 이상 |

### BR-ADMIN-VAL-04: 카테고리 등록/수정 요청
| 필드 | 규칙 |
|------|------|
| name | NOT NULL, 1~50자 |

### BR-ADMIN-VAL-05: 주문 상태 변경 요청
| 필드 | 규칙 |
|------|------|
| status | NOT NULL, ENUM('PREPARING', 'COMPLETED') |

### BR-ADMIN-VAL-06: 과거 내역 조회 요청
| 필드 | 규칙 |
|------|------|
| dateFrom | NULLABLE, 날짜 형식 (yyyy-MM-dd) |
| dateTo | NULLABLE, 날짜 형식 (yyyy-MM-dd) |

### BR-ADMIN-VAL-07: 순서 변경 요청
| 필드 | 규칙 |
|------|------|
| items | NOT NULL, 1개 이상 |
| items[].id | NOT NULL, 양의 정수 |
| items[].displayOrder | NOT NULL, 0 이상 정수 |

---

## 8. 에러 코드 정의 (Admin 고유)

| 코드 | HTTP | 설명 |
|------|------|------|
| INVALID_CREDENTIALS | 401 | 잘못된 인증 정보 |
| ACCOUNT_LOCKED | 423 | 계정 잠금 (로그인 시도 초과) |
| INVALID_TOKEN | 401 | 유효하지 않은 토큰 |
| TOKEN_REVOKED | 401 | 무효화된 토큰 |
| TOKEN_EXPIRED | 401 | 만료된 토큰 |
| TABLE_NOT_FOUND | 404 | 테이블을 찾을 수 없음 |
| TABLE_NUMBER_DUPLICATE | 409 | 테이블 번호 중복 |
| TABLE_IN_USE | 409 | 활성 세션이 있는 테이블 (삭제 불가) |
| NO_ACTIVE_SESSION | 400 | 활성 세션 없음 (이용 완료 불가) |
| ORDER_NOT_FOUND | 404 | 주문을 찾을 수 없음 |
| INVALID_STATUS_TRANSITION | 400 | 허용되지 않는 상태 전이 |
| MENU_NOT_FOUND | 404 | 메뉴를 찾을 수 없음 |
| MENU_DELETED | 400 | 삭제된 메뉴 (수정/품절 설정 불가) |
| CATEGORY_NOT_FOUND | 404 | 카테고리를 찾을 수 없음 |
| CATEGORY_HAS_MENUS | 409 | 메뉴가 있는 카테고리 (삭제 불가) |
| INVALID_PIN_FORMAT | 400 | PIN 형식 오류 |
| FORBIDDEN | 403 | 접근 권한 없음 (매장 소속 불일치) |
| INTERNAL_ERROR | 500 | 서버 내부 오류 |

**에러 응답 형식**:
```json
{
  "status": 423,
  "code": "ACCOUNT_LOCKED",
  "message": "계정이 잠겼습니다. 15분 후 다시 시도해주세요.",
  "timestamp": "2026-03-05T11:00:00Z",
  "details": { "lockUntil": "2026-03-05T11:15:00Z", "remainingMinutes": 15 }
}
```