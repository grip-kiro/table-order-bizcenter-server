# 테이블오더 서비스 — Unit of Work 정의 (v2)

## 분해 전략
4개 유닛으로 분해. 고객용과 관리자용을 클라이언트/서버 모두 분리.
공통 DB와 공통 도메인 모델은 백엔드 내 shared 모듈로 관리.

---

## Unit 1: Customer Backend (고객용 서버)

**유형**: Service (독립 배포 가능)
**기술**: Java 17+ / Spring Boot 3.x / MySQL / Flyway
**범위**: 고객 대면 API — 메뉴 조회, 장바구니 검증, 주문 생성, 주문 내역, 태블릿 인증

**모듈 구성**:
- auth — 태블릿 인증 (JWT 발급, 자동 로그인, 기기 제한)
- menu — 메뉴/카테고리 조회 (읽기 전용, 캐싱)
- order — 주문 생성, 현재 세션 주문 조회
- sse — 태블릿 SSE (세션 종료 감지)
- common — 공통 도메인 엔티티, 설정, 에러 처리

**API 엔드포인트**:
- POST /api/auth/table/login
- POST /api/auth/refresh
- GET /api/stores/{storeId}/menus
- GET /api/menus/{menuId}
- POST /api/orders
- GET /api/tables/{tableId}/orders
- GET /api/sse/table/{tableId}

**개발 순서**: 1순위 (Customer App의 의존성)

---

## Unit 2: Admin Backend (관리자용 서버)

**유형**: Service (독립 배포 가능)
**기술**: Java 17+ / Spring Boot 3.x / MySQL / Flyway
**범위**: 관리자 API — 매장 인증, 주문 모니터링/관리, 테이블 관리, 메뉴/카테고리 CRUD

**모듈 구성**:
- auth — 관리자 인증 (JWT, 로그인 시도 제한, 16시간 세션)
- menu — 메뉴/카테고리 CRUD, 품절 설정, 순서 변경
- order — 주문 조회, 상태 변경, 삭제, 과거 내역
- table — 테이블 등록/관리, 세션 종료(이용 완료)
- sse — 관리자 SSE (실시간 주문 업데이트)
- common — 공통 도메인 엔티티, 설정, 에러 처리

**API 엔드포인트**:
- POST /api/auth/admin/login
- POST /api/auth/refresh
- POST /api/auth/logout
- GET /api/stores/{storeId}/menus (관리자용 — 비활성 메뉴 포함)
- POST /api/menus
- PUT /api/menus/{menuId}
- DELETE /api/menus/{menuId}
- PATCH /api/menus/{menuId}/sold-out
- PATCH /api/menus/order
- GET /api/stores/{storeId}/categories
- POST /api/categories
- PUT /api/categories/{categoryId}
- DELETE /api/categories/{categoryId}
- PATCH /api/categories/order
- GET /api/admin/tables/{tableId}/orders
- PATCH /api/orders/{orderId}/status
- DELETE /api/orders/{orderId}
- GET /api/admin/tables/{tableId}/history
- GET /api/stores/{storeId}/tables
- POST /api/tables
- PUT /api/tables/{tableId}
- DELETE /api/tables/{tableId}
- POST /api/tables/{tableId}/complete
- GET /api/sse/admin/{storeId}

**개발 순서**: 1순위 (Admin App의 의존성, Customer Backend와 병렬 가능)

**주의**: 이용 완료(completeTableSession) 시 Customer Backend의 SSE로 세션 종료 이벤트를 전달해야 함 → 공유 DB의 세션 상태 변경 + 이벤트 테이블 또는 직접 SSE 호출

---

## Unit 3: Customer App (고객용 클라이언트)

**유형**: Module (별도 배포, Customer Backend 의존)
**기술**: React 18+ / TypeScript / Vite
**범위**: 고객 대면 UI — 메뉴 탐색, 장바구니, 주문, 주문 내역

**주요 페이지/컴포넌트**:
- SetupPage — 태블릿 초기 설정
- MenuPage — 메뉴 탐색 (사이드바 + 3열 그리드)
- MenuDetailModal — 메뉴 상세 모달
- CartDrawer/CartPage — 장바구니
- OrderConfirmPage — 주문 확인/확정
- OrderSuccessPage — 주문 성공 (5초 표시)
- OrderHistoryPage — 주문 내역 조회

**개발 순서**: 2순위 (Customer Backend 완료 후)

---

## Unit 4: Admin App (관리자용 클라이언트)

**유형**: Module (별도 배포, Admin Backend 의존)
**기술**: React 18+ / TypeScript / Vite
**범위**: 관리자 UI — 로그인, 대시보드, 테이블/메뉴/카테고리 관리

**주요 페이지/컴포넌트**:
- LoginPage — 관리자 로그인
- DashboardPage — 실시간 주문 대시보드
- TableDetailModal — 테이블 주문 상세
- TableManagementPage — 테이블 등록/관리
- MenuManagementPage — 메뉴 CRUD
- CategoryManagementPage — 카테고리 CRUD
- OrderHistoryModal — 과거 주문 내역

**개발 순서**: 2순위 (Admin Backend 완료 후)

---

## 공유 자원

### 공유 DB (MySQL)
- 두 백엔드가 동일한 MySQL 데이터베이스를 공유
- Flyway 마이그레이션은 한쪽(Customer Backend 또는 별도)에서 관리
- 도메인 엔티티(Store, Table, Menu, Order 등)는 양쪽 백엔드에서 동일 스키마 사용

### 백엔드 간 통신 (이용 완료 시)
- Admin Backend에서 이용 완료 처리 → DB 세션 상태 변경
- Customer Backend의 SSE가 DB 상태를 감지하여 태블릿에 세션 종료 푸시
- 방식: 공유 DB 폴링 또는 내부 이벤트 (MVP에서는 DB 상태 기반)

---

## 코드 구조

```
aidlc-workshop/
+-- customer-backend/           # Unit 1: Spring Boot (고객용)
|   +-- src/main/java/com/tableorder/customer/
|   |   +-- auth/
|   |   +-- menu/
|   |   +-- order/
|   |   +-- sse/
|   |   +-- common/
|   +-- src/main/resources/
|   |   +-- db/migration/       # Flyway (공유 스키마)
|   |   +-- application.yml
|   +-- build.gradle
+-- admin-backend/              # Unit 2: Spring Boot (관리자용)
|   +-- src/main/java/com/tableorder/admin/
|   |   +-- auth/
|   |   +-- menu/
|   |   +-- order/
|   |   +-- table/
|   |   +-- sse/
|   |   +-- common/
|   +-- src/main/resources/
|   |   +-- application.yml
|   +-- build.gradle
+-- customer-app/               # Unit 3: React (고객용)
|   +-- src/
|   +-- package.json
+-- admin-app/                  # Unit 4: React (관리자용)
|   +-- src/
|   +-- package.json
+-- aidlc-docs/                 # 문서
```
