# 테이블오더 서비스 — Unit of Work 의존성 (v2)

## 의존성 매트릭스

```
                  Customer    Admin       Customer   Admin
                  Backend     Backend     App        App
Customer Backend    -         공유DB        -          -
Admin Backend     공유DB        -           -          -
Customer App      REST+SSE     -           -          -
Admin App           -        REST+SSE      -          -
```

## 개발 순서

```
Phase 1a: Customer Backend ----+----> Phase 2a: Customer App
                               |
Phase 1b: Admin Backend -------+----> Phase 2b: Admin App
          (병렬)                        (병렬)
```

- Phase 1: 두 백엔드 병렬 개발 (공유 DB 스키마는 Customer Backend에서 Flyway 관리)
- Phase 2: 두 프론트엔드 병렬 개발 (각각의 백엔드 완료 후)

## 통합 포인트

| 통합 포인트 | 관련 유닛 | 계약 |
|-------------|-----------|------|
| 공유 DB | Customer Backend ↔ Admin Backend | 동일 MySQL 스키마 |
| REST API (고객) | Customer Backend ↔ Customer App | 고객용 API 엔드포인트 |
| REST API (관리자) | Admin Backend ↔ Admin App | 관리자용 API 엔드포인트 |
| SSE 주문 업데이트 | Admin Backend → Admin App | /api/sse/admin/{storeId} |
| SSE 세션 종료 | Customer Backend → Customer App | /api/sse/table/{tableId} |
| 이용 완료 이벤트 | Admin Backend → Customer Backend | 공유 DB 상태 변경 기반 |
| JWT 토큰 | 각 Backend ↔ 각 App | Access + Refresh Token |

## 리스크

| 리스크 | 영향 | 완화 방안 |
|--------|------|-----------|
| 공유 DB 스키마 충돌 | 양쪽 백엔드 영향 | Flyway를 한쪽에서만 관리 |
| 이용 완료 이벤트 전달 | 태블릿 세션 종료 지연 | DB 폴링 주기 최소화 (1~2초) |
| 백엔드 간 도메인 모델 불일치 | 데이터 정합성 문제 | 공통 엔티티 정의 문서화 |
