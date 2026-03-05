# 테이블오더 서비스 — 컴포넌트 정의

## 시스템 구성도

```
+------------------+    +------------------+    +------------------+
|  Customer App    |    |   Admin App      |    |                  |
|  (React/TS)      |    |  (React/TS)      |    |   MySQL DB       |
|                  |    |                  |    |                  |
|  - 메뉴 탐색     |    |  - 대시보드       |    |  - stores        |
|  - 장바구니       |    |  - 주문 모니터링   |    |  - tables        |
|  - 주문 생성     |    |  - 테이블 관리     |    |  - menus         |
|  - 주문 내역     |    |  - 메뉴 관리       |    |  - categories    |
|  - 자동 로그인   |    |  - 카테고리 관리   |    |  - orders        |
+--------+---------+    +--------+---------+    |  - order_items   |
         |                       |               |  - order_history |
         |    REST API + SSE     |               +--------+---------+
         +----------+------------+                        |
                    |                                     |
         +----------v------------+                        |
         |   Backend API         +------------------------+
         |   (Spring Boot)       |       JPA/Flyway
         |                       |
         |  - AuthController     |
         |  - MenuController     |
         |  - OrderController    |
         |  - TableController    |
         |  - CategoryController |
         |  - SSE Emitter        |
         +-----------------------+
```

---

## Component 1: Customer App (고객용 웹 애플리케이션)

**기술**: React + TypeScript
**목적**: 테이블 태블릿에서 고객이 메뉴를 탐색하고 주문하는 웹 애플리케이션

**책임**:
- 태블릿 초기 설정 및 자동 로그인 관리
- 카테고리별 메뉴 탐색 UI (좌측 사이드바 + 3열 그리드)
- 메뉴 상세 모달 표시
- 장바구니 관리 (localStorage 저장)
- 주문 생성 및 결과 표시
- 주문 내역 조회 (현재 세션)
- SSE를 통한 세션 종료 감지
- 네트워크 복구 시 자동 세션 복구

**인터페이스**:
- REST API 호출 (백엔드)
- SSE 수신 (세션 종료 이벤트)
- localStorage (장바구니, 설정 정보)
- 쿠키 (JWT 토큰)

---

## Component 2: Admin App (관리자용 웹 애플리케이션)

**기술**: React + TypeScript
**목적**: 매장 운영자가 주문을 모니터링하고 매장을 관리하는 웹 애플리케이션

**책임**:
- 관리자 로그인/로그아웃
- 실시간 주문 대시보드 (테이블별 그리드)
- 주문 상태 변경 (대기중/준비중/완료)
- 테이블 등록/관리 (사전 등록, 마스터 PIN)
- 테이블 이용 완료 처리
- 과거 주문 내역 조회
- 메뉴 CRUD (테이블 형태 관리 화면)
- 카테고리 CRUD
- 메뉴 품절 설정/해제
- 메뉴 노출 순서 드래그 앤 드롭

**인터페이스**:
- REST API 호출 (백엔드)
- SSE 수신 (실시간 주문 업데이트)
- 쿠키 (JWT 토큰)

---

## Component 3: Backend API (백엔드 API 서버)

**기술**: Java + Spring Boot
**목적**: 비즈니스 로직 처리, 데이터 관리, 인증/인가, 실시간 이벤트 발행

**책임**:
- 인증/인가 (JWT 발급/검증, role 기반 접근 제어)
- 메뉴/카테고리 CRUD 및 캐싱
- 주문 생성/조회/삭제/상태 변경
- 테이블 등록/관리/세션 관리
- SSE 이벤트 발행 (주문 업데이트, 세션 종료)
- 로그인 시도 제한 (계정 기반)
- 데이터 유효성 검증

**인터페이스**:
- REST API 제공 (Customer App, Admin App)
- SSE 발행 (Customer App, Admin App)
- JPA를 통한 DB 접근

---

## Component 4: Database (데이터베이스)

**기술**: MySQL + Flyway
**목적**: 매장, 테이블, 메뉴, 주문 등 모든 영속 데이터 저장

**책임**:
- 데이터 영속성 보장
- Flyway를 통한 스키마 버전 관리
- 관계형 데이터 무결성 유지

**주요 테이블**:
- stores (매장)
- admin_users (관리자 계정)
- tables (테이블)
- categories (카테고리)
- menus (메뉴)
- menu_categories (메뉴-카테고리 N:M 매핑)
- orders (주문)
- order_items (주문 항목)
- order_history (과거 주문 이력)
- table_sessions (테이블 세션)
