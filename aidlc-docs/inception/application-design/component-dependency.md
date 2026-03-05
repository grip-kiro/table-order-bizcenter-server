# 테이블오더 서비스 — 컴포넌트 의존성

## 컴포넌트 의존성 매트릭스

```
                Customer App   Admin App   Backend API   Database
Customer App        -             -         REST+SSE        -
Admin App           -             -         REST+SSE        -
Backend API         -             -            -          JPA/Flyway
Database            -             -            -             -
```

## 데이터 흐름

### 고객 → 주문 생성
```
Customer App --POST /api/orders--> Backend API --INSERT--> Database
                                       |
                                       +--SSE notifyOrderUpdate--> Admin App
```

### 관리자 → 주문 상태 변경
```
Admin App --PATCH /api/orders/{id}/status--> Backend API --UPDATE--> Database
```

### 관리자 → 테이블 이용 완료
```
Admin App --POST /api/tables/{id}/complete--> Backend API --UPDATE--> Database
                                                  |
                                                  +--SSE notifySessionEnd--> Customer App
                                                  +--invalidateSession--> (토큰 무효화)
```

### 관리자 → 메뉴 변경
```
Admin App --POST/PUT/DELETE /api/menus--> Backend API --UPDATE--> Database
                                              |
                                              +--evictMenuCache (캐시 무효화)
```

## 통신 패턴

| 통신 | 프로토콜 | 방향 | 용도 |
|------|----------|------|------|
| Customer App ↔ Backend | REST (HTTP) | 양방향 | 메뉴 조회, 주문 생성, 주문 내역 |
| Customer App ← Backend | SSE | 단방향 (서버→클라이언트) | 세션 종료 감지 |
| Admin App ↔ Backend | REST (HTTP) | 양방향 | 모든 관리 기능 |
| Admin App ← Backend | SSE | 단방향 (서버→클라이언트) | 실시간 주문 업데이트 |
| Backend ↔ Database | JPA (JDBC) | 양방향 | 데이터 CRUD |

## 프로젝트 구조 (예상)

```
aidlc-workshop/
+-- customer-app/          # React 고객용 앱
|   +-- src/
|   +-- package.json
+-- admin-app/             # React 관리자용 앱
|   +-- src/
|   +-- package.json
+-- backend/               # Spring Boot API
|   +-- src/main/java/
|   +-- src/main/resources/
|   +-- build.gradle (or pom.xml)
+-- aidlc-docs/            # AI-DLC 문서 (코드 아님)
```
