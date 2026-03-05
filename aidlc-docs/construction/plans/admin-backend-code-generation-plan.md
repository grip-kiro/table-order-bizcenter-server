# Admin Backend — Code Generation Plan

## Unit Context
- **Unit**: Admin Backend (관리자용 서버)
- **기술**: Java 17+ / Spring Boot 3.x / MySQL / Flyway / Gradle
- **범위**: 관리자 API — 매장 인증, 주문 모니터링/관리, 테이블 관리, 메뉴/카테고리 CRUD, SSE
- **MySQL**: Docker Compose로 실행
- **프론트엔드**: 별도 리포지토리 (이 리포에서는 API + DB만)

## 코드 위치
- **Application Code**: 워크스페이스 루트 (Spring Boot 프로젝트)
- **패키지**: `com.tableorder.admin`

---

## Step 1: 프로젝트 구조 및 빌드 설정
- [x] `build.gradle` (Spring Boot 3.x, JPA, Flyway, MySQL, Security, Validation, Lombok)
- [x] `settings.gradle`
- [x] `gradle/wrapper/` (Gradle Wrapper)
- [x] `docker-compose.yml` (MySQL 8.x)
- [x] `src/main/resources/application.yml` (DB, JPA, Flyway, JWT 설정)
- [x] `src/main/java/com/tableorder/admin/AdminBackendApplication.java`

## Step 2: 공통 모듈 (Common)
- [x] 글로벌 에러 응답 DTO (`ErrorResponse`)
- [x] 비즈니스 예외 클래스 (`BusinessException`, 에러 코드 enum)
- [x] 글로벌 예외 핸들러 (`GlobalExceptionHandler`)
- [x] Base Entity (`BaseEntity` — createdAt, updatedAt)

## Step 3: 도메인 엔티티 (JPA Entities)
- [x] `Store`
- [x] `AdminAccount`
- [x] `RestaurantTable`
- [x] `Category`
- [x] `Menu`
- [x] `MenuCategory`
- [x] `Order` (is_deleted 포함)
- [x] `OrderItem`
- [x] `OrderHistory`
- [x] `OrderHistoryItem`
- [x] `RefreshToken`

## Step 4: Flyway 마이그레이션 스크립트
- [x] `V1__init_schema.sql` — 전체 테이블 생성
- [x] `V2__seed_data.sql` — 개발용 시드 데이터

## Step 5: Repository Layer
- [x] `StoreRepository`
- [x] `AdminAccountRepository`
- [x] `RestaurantTableRepository`
- [x] `CategoryRepository`
- [x] `MenuRepository`
- [x] `MenuCategoryRepository`
- [x] `OrderRepository`
- [x] `OrderItemRepository`
- [x] `OrderHistoryRepository`
- [x] `OrderHistoryItemRepository`
- [x] `RefreshTokenRepository`

## Step 6: 인증 모듈 (Auth)
- [x] `JwtTokenProvider` — JWT 생성/검증
- [x] `JwtAuthenticationFilter` — 요청별 JWT 검증 필터
- [x] `SecurityConfig` — Spring Security 설정 (CORS, 경로별 인가)
- [x] `AuthService` — 관리자 로그인, 토큰 갱신, 로그아웃
- [x] `AuthController` — POST /api/auth/admin/login, /refresh, /logout
- [x] Auth DTO (`AdminLoginRequest`, `TokenResponse`, `RefreshRequest`)

## Step 7: 메뉴/카테고리 관리 모듈 (Menu & Category)
- [x] `CategoryService`
- [x] `CategoryController` — CRUD + 순서 변경
- [x] Category DTO
- [x] `MenuService`
- [x] `MenuController` — CRUD + 품절 + 순서 변경
- [x] Menu DTO

## Step 8: 주문 관리 모듈 (Order)
- [x] `OrderService` — 테이블별 주문 조회, 상태 변경, 삭제, 과거 내역
- [x] `OrderController` — 관리자 주문 API
- [x] Order DTO

## Step 9: 테이블 관리 모듈 (Table)
- [x] `TableService` — CRUD + 이용 완료
- [x] `TableController` — 테이블 관리 API
- [x] Table DTO

## Step 10: SSE 모듈
- [x] `SseService` — emitter 관리, 이벤트 발행
- [x] `SseController` — GET /api/sse/admin/{storeId}
- [x] `OrderEventListener` — 신규 주문 감지

## Step 11: API 문서 및 README
- [x] `README.md` — 프로젝트 설명, 실행 방법, API 개요

---

## 총 11 Steps
