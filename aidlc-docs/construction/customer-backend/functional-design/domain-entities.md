# Customer Backend — 도메인 엔티티 설계

> Unit 1: Customer Backend가 사용하는 도메인 엔티티 정의.
> Admin Backend와 공유 DB를 사용하므로 동일 스키마를 참조합니다.

---

## 엔티티 관계 다이어그램 (ERD 개요)

```
Store (1) ──── (N) RestaurantTable
Store (1) ──── (N) Category
Store (1) ──── (N) Menu
Store (1) ──── (N) AdminAccount

Menu (N) ──── (N) Category        [menu_category 조인 테이블]
RestaurantTable (1) ──── (N) Order
Order (1) ──── (N) OrderItem
OrderItem (N) ──── (1) Menu
```

---

## 엔티티 상세

### Store (매장)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 매장 고유 ID |
| name | VARCHAR(100) | NOT NULL | 매장명 |
| master_pin | VARCHAR(10) | NOT NULL | 태블릿 설정 모드 진입용 마스터 PIN |
| created_at | DATETIME | NOT NULL, DEFAULT NOW | 생성 시각 |
| updated_at | DATETIME | NOT NULL, DEFAULT NOW ON UPDATE | 수정 시각 |

**Customer Backend 사용**: 태블릿 로그인 시 master_pin 검증, storeId 기반 메뉴/카테고리 조회

---

### RestaurantTable (테이블)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 테이블 고유 ID |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| table_number | INT | NOT NULL | 테이블 번호 |
| pin | VARCHAR(10) | NOT NULL | 테이블 PIN (4~6자리 숫자) |
| current_session_id | VARCHAR(36) | NULLABLE | 현재 활성 세션 ID (UUID) |
| status | ENUM('AVAILABLE','OCCUPIED') | NOT NULL, DEFAULT 'AVAILABLE' | 테이블 상태 |
| created_at | DATETIME | NOT NULL | 생성 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

**유니크 제약**: (store_id, table_number) UNIQUE

**Customer Backend 사용**: 태블릿 로그인 시 PIN 검증, 세션 ID 발급/검증, 주문 시 세션 유효성 확인

---

### Category (카테고리)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 카테고리 고유 ID |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| name | VARCHAR(50) | NOT NULL | 카테고리명 |
| display_order | INT | NOT NULL, DEFAULT 0 | 노출 순서 |
| created_at | DATETIME | NOT NULL | 생성 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

**Customer Backend 사용**: 읽기 전용 — 카테고리 목록 조회

---

### Menu (메뉴)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 메뉴 고유 ID |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| name | VARCHAR(100) | NOT NULL | 메뉴명 |
| description | TEXT | NULLABLE | 메뉴 설명 |
| price | INT | NOT NULL | 가격 (원 단위, 0~1,000,000) |
| image_url | VARCHAR(500) | NULLABLE | 이미지 URL |
| is_sold_out | BOOLEAN | NOT NULL, DEFAULT FALSE | 품절 여부 |
| is_deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | 소프트 삭제 여부 |
| display_order | INT | NOT NULL, DEFAULT 0 | 노출 순서 |
| created_at | DATETIME | NOT NULL | 생성 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

**Customer Backend 사용**: 읽기 전용 — 메뉴 목록/상세 조회 (is_deleted=false만), 주문 시 메뉴 존재/품절 검증

---

### MenuCategory (메뉴-카테고리 조인 테이블)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| menu_id | BIGINT (FK → Menu) | NOT NULL | 메뉴 ID |
| category_id | BIGINT (FK → Category) | NOT NULL | 카테고리 ID |

**복합 PK**: (menu_id, category_id)

**Customer Backend 사용**: 읽기 전용 — 카테고리별 메뉴 필터링

---

### Order (주문)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 주문 번호 (자동 증가) |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| table_id | BIGINT (FK → RestaurantTable) | NOT NULL | 주문 테이블 |
| session_id | VARCHAR(36) | NOT NULL | 세션 ID (테이블의 current_session_id) |
| total_amount | INT | NOT NULL | 총 주문 금액 |
| status | ENUM('PENDING','PREPARING','COMPLETED') | NOT NULL, DEFAULT 'PENDING' | 주문 상태 |
| created_at | DATETIME | NOT NULL | 주문 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

**인덱스**: (table_id, session_id), (store_id, created_at)

**Customer Backend 사용**: 주문 생성 (INSERT), 현재 세션 주문 조회 (SELECT by table_id + session_id)

---

### OrderItem (주문 항목)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 주문 항목 ID |
| order_id | BIGINT (FK → Order) | NOT NULL | 소속 주문 |
| menu_id | BIGINT (FK → Menu) | NOT NULL | 메뉴 ID |
| menu_name | VARCHAR(100) | NOT NULL | 주문 시점 메뉴명 (스냅샷) |
| quantity | INT | NOT NULL | 수량 (1 이상) |
| unit_price | INT | NOT NULL | 주문 시점 단가 (스냅샷) |
| subtotal | INT | NOT NULL | 소계 (quantity × unit_price) |

**Customer Backend 사용**: 주문 생성 시 항목 저장, 주문 내역 조회 시 항목 표시

---

### RefreshToken (리프레시 토큰)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 토큰 ID |
| token | VARCHAR(500) | NOT NULL, UNIQUE | Refresh Token 값 |
| table_id | BIGINT (FK → RestaurantTable) | NULLABLE | 태블릿용 토큰인 경우 |
| admin_id | BIGINT (FK → AdminAccount) | NULLABLE | 관리자용 토큰인 경우 |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| role | ENUM('TABLE','ADMIN') | NOT NULL | 토큰 역할 |
| expires_at | DATETIME | NOT NULL | 만료 시각 |
| is_revoked | BOOLEAN | NOT NULL, DEFAULT FALSE | 무효화 여부 |
| created_at | DATETIME | NOT NULL | 생성 시각 |

**Customer Backend 사용**: 태블릿 로그인 시 토큰 발급, Access Token 갱신 시 검증, 기기 제한 시 기존 토큰 무효화

---

### AdminAccount (관리자 계정) — 참조용

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 관리자 ID |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| username | VARCHAR(50) | NOT NULL | 사용자명 |
| password_hash | VARCHAR(255) | NOT NULL | bcrypt 해시 비밀번호 |
| is_locked | BOOLEAN | NOT NULL, DEFAULT FALSE | 계정 잠금 여부 |
| lock_until | DATETIME | NULLABLE | 잠금 해제 시각 |
| failed_attempts | INT | NOT NULL, DEFAULT 0 | 연속 실패 횟수 |
| created_at | DATETIME | NOT NULL | 생성 시각 |
| updated_at | DATETIME | NOT NULL | 수정 시각 |

**유니크 제약**: (store_id, username) UNIQUE

**Customer Backend 사용**: 직접 사용하지 않음 (Admin Backend 전용). 공유 DB에 존재.
