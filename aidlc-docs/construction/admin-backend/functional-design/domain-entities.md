# Admin Backend — 도메인 엔티티 설계

> Unit 2: Admin Backend가 사용하는 도메인 엔티티 정의.
> Unit 1 (Customer Backend)과 공유 DB를 사용하며, 공통 엔티티는 Unit 1의 domain-entities.md를 참조합니다.
> 여기서는 Admin Backend 추가/확장 엔티티만 정의합니다.

---

## 공유 엔티티 (Unit 1 참조)

다음 엔티티는 Unit 1에서 정의된 스키마를 그대로 사용합니다:
- Store, RestaurantTable, Category, Menu, MenuCategory, Order, OrderItem, RefreshToken, AdminAccount

---

## 추가 엔티티

### OrderHistory (주문 이력)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 이력 ID |
| original_order_id | BIGINT | NOT NULL | 원본 Order ID |
| store_id | BIGINT (FK → Store) | NOT NULL | 소속 매장 |
| table_id | BIGINT (FK → RestaurantTable) | NOT NULL | 주문 테이블 |
| session_id | VARCHAR(36) | NOT NULL | 세션 ID |
| total_amount | INT | NOT NULL | 총 주문 금액 |
| status | ENUM('PENDING','PREPARING','COMPLETED') | NOT NULL | 주문 상태 |
| ordered_at | DATETIME | NOT NULL | 원본 주문 시각 |
| completed_at | DATETIME | NOT NULL | 이용 완료 처리 시각 |
| created_at | DATETIME | NOT NULL | 이력 생성 시각 |

**인덱스**: (table_id, completed_at DESC), (store_id, completed_at DESC)

---

### OrderHistoryItem (주문 이력 항목)

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT (PK) | AUTO_INCREMENT | 이력 항목 ID |
| order_history_id | BIGINT (FK → OrderHistory) | NOT NULL | 소속 이력 |
| menu_id | BIGINT | NOT NULL | 메뉴 ID (참조용, FK 아님) |
| menu_name | VARCHAR(100) | NOT NULL | 주문 시점 메뉴명 |
| quantity | INT | NOT NULL | 수량 |
| unit_price | INT | NOT NULL | 주문 시점 단가 |
| subtotal | INT | NOT NULL | 소계 |

---

## 기존 엔티티 확장 (Admin Backend 관점)

### Order — 추가 필드

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| is_deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | 소프트 삭제 여부 (관리자 주문 삭제) |

> Unit 1의 Order 엔티티에 is_deleted 필드를 추가합니다.
> Customer Backend의 주문 조회 시 is_deleted=false 조건을 추가해야 합니다.
