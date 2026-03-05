# 테이블오더 Admin Backend

테이블오더 서비스의 관리자용 백엔드 API 서버입니다.

## 기술 스택

- Java 17
- Spring Boot 3.4.3
- MySQL 8.0 (Docker)
- Flyway (DB 마이그레이션)
- JWT 인증

## 실행 방법

### 1. MySQL 실행

```bash
docker compose up -d
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 시드 데이터

자동으로 생성되는 테스트 데이터:
- 매장: "테스트 매장" (ID: 1, master_pin: 000000)
- 관리자: username `admin`, password `admin1234`
- 카테고리: 메인, 사이드, 음료, 디저트
- 메뉴: 9개 샘플 메뉴
- 테이블: 1~5번

## API 엔드포인트

### 인증
| Method | Path | 설명 |
|--------|------|------|
| POST | /api/auth/admin/login | 관리자 로그인 |
| POST | /api/auth/refresh | 토큰 갱신 |
| POST | /api/auth/logout | 로그아웃 |

### 메뉴 관리
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/stores/{storeId}/menus | 메뉴 목록 조회 |
| POST | /api/menus | 메뉴 등록 |
| PUT | /api/menus/{menuId} | 메뉴 수정 |
| DELETE | /api/menus/{menuId} | 메뉴 삭제 |
| PATCH | /api/menus/{menuId}/sold-out | 품절 설정 |
| PATCH | /api/menus/order | 메뉴 순서 변경 |

### 카테고리 관리
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/stores/{storeId}/categories | 카테고리 목록 |
| POST | /api/categories | 카테고리 등록 |
| PUT | /api/categories/{categoryId} | 카테고리 수정 |
| DELETE | /api/categories/{categoryId} | 카테고리 삭제 |
| PATCH | /api/categories/order | 카테고리 순서 변경 |

### 주문 관리
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/admin/tables/{tableId}/orders | 테이블 주문 조회 |
| PATCH | /api/admin/orders/{orderId}/status | 주문 상태 변경 |
| DELETE | /api/admin/orders/{orderId} | 주문 삭제 |
| GET | /api/admin/tables/{tableId}/history | 과거 주문 내역 |

### 테이블 관리
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/stores/{storeId}/tables | 테이블 목록 |
| POST | /api/tables | 테이블 등록 |
| PUT | /api/tables/{tableId} | 테이블 수정 |
| DELETE | /api/tables/{tableId} | 테이블 삭제 |
| POST | /api/tables/{tableId}/complete | 이용 완료 |

### SSE
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/sse/admin/{storeId} | 실시간 주문 구독 |
