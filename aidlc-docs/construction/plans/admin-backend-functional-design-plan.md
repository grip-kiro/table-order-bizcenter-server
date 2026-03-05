# Admin Backend — Functional Design Plan

## 대상 유닛
- **Unit 2: Admin Backend (관리자용 서버)**
- **기술**: Java 17+ / Spring Boot 3.x / MySQL / Flyway
- **스토리**: US-18, US-19, US-20, US-21, US-22, US-24, US-25, US-26, US-27, US-28, US-29, US-30, US-31, US-32, US-33

## 산출물 체크리스트

- [x] Step 1: 유닛 컨텍스트 분석
- [x] Step 2: 명확화 질문 생성 및 답변 수집
- [x] Step 3: 도메인 엔티티 설계 (`domain-entities.md`) — Unit 1과 공유, 추가 엔티티만
- [x] Step 4: 비즈니스 로직 모델 설계 (`business-logic-model.md`)
- [x] Step 5: 비즈니스 규칙 정의 (`business-rules.md`)
- [x] Step 6: 사용자 승인 ✅ (2026-03-05)

## 질문 (Clarification Questions)

Unit 1에서 공유 도메인 엔티티와 기본 규칙이 정의되었습니다. Admin Backend 고유 비즈니스 로직에 대한 추가 결정 사항입니다.

---

### Question 1
테이블 이용 완료 시 주문 이력 보관 방식은?

A) 별도 OrderHistory 테이블로 복사 후 원본 Order 유지 (status 변경만)
B) 별도 OrderHistory 테이블로 이동 (원본 Order 삭제)
C) Order 테이블에 그대로 유지하고 session_id로 구분 (별도 이력 테이블 없음)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 2
관리자 로그인 시도 제한의 실패 횟수 카운트 초기화 시점은?

A) 로그인 성공 시 초기화
B) 로그인 성공 시 + 잠금 해제(15분 경과) 시 초기화
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 3
카테고리 삭제 시 해당 카테고리에 메뉴가 있는 경우 처리 방식은?

A) 삭제 불가 (메뉴가 있으면 에러 반환)
B) 메뉴-카테고리 연결만 해제하고 카테고리 삭제 (메뉴는 유지, 다른 카테고리에 속하지 않으면 미분류)
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 4
메뉴 소프트 삭제 시 해당 메뉴의 카테고리 연결(menu_category)은?

A) 연결 유지 (is_deleted=true인 메뉴도 카테고리 관계 보존)
B) 연결 해제 (menu_category 레코드 삭제)
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 5
주문 삭제(US-25) 시 실제 DB 처리 방식은?

A) 하드 삭제 (Order + OrderItem 물리 삭제)
B) 소프트 삭제 (Order에 is_deleted 플래그)
C) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 6
관리자 SSE로 전달되는 주문 이벤트의 범위는?

A) 신규 주문 생성만 (ORDER_CREATED)
B) 신규 주문 + 주문 상태 변경 + 주문 삭제 (ORDER_CREATED, ORDER_STATUS_CHANGED, ORDER_DELETED)
C) Other (please describe after [Answer]: tag below)

[Answer]: A

