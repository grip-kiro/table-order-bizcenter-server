# Customer Backend — Functional Design Plan

## 대상 유닛
- **Unit 1: Customer Backend (고객용 서버)**
- **기술**: Java 17+ / Spring Boot 3.x / MySQL / Flyway
- **스토리**: US-01, US-02, US-03, US-05, US-06, US-08, US-14, US-15, US-16, US-17

## 산출물 체크리스트

- [x] Step 1: 유닛 컨텍스트 분석
- [x] Step 2: 명확화 질문 생성 및 답변 수집
- [x] Step 3: 도메인 엔티티 설계 (`domain-entities.md`)
- [x] Step 4: 비즈니스 로직 모델 설계 (`business-logic-model.md`)
- [x] Step 5: 비즈니스 규칙 정의 (`business-rules.md`)
- [x] Step 6: 사용자 승인 ✅ (2026-03-05)

## 질문 (Clarification Questions)

요구사항이 상세하게 정의되어 있으나, Customer Backend 비즈니스 로직 설계에 필요한 추가 결정 사항이 있습니다.

---

### Question 1
태블릿 로그인 시 마스터 PIN 검증은 어디서 관리합니까? (마스터 PIN은 매장 단위 설정)

A) Store 엔티티에 master_pin 필드로 저장 (매장별 1개)
B) 별도 설정 테이블(StoreConfig)에 저장
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 2
주문 생성 시 주문 번호 형식은 어떻게 할까요?

A) 자동 증가 숫자 (1, 2, 3...)
B) 날짜+순번 형식 (20260305-001)
C) 매장+날짜+순번 형식 (S1-20260305-001)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 3
주문 생성 시 품절 메뉴가 포함된 경우 처리 방식은?

A) 주문 전체 거부 (품절 메뉴 목록 반환)
B) 품절 메뉴만 제외하고 나머지 주문 처리
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 4
테이블 세션(session) 관리 방식은 어떻게 할까요? (세션 = 태블릿 로그인 ~ 이용 완료까지의 기간)

A) 별도 Session 테이블로 관리 (session_id, table_id, start_time, end_time, status)
B) Table 엔티티에 current_session_id 필드 + Order에 session_id 필드로 관리
C) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 5
메뉴 조회 시 캐싱 키(key) 전략은?

A) 매장 ID 단위 (storeId → 전체 메뉴+카테고리)
B) 매장 ID + 카테고리 ID 단위 (storeId:categoryId → 카테고리별 메뉴)
C) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 6
주문 상태(OrderStatus) 값은 어떤 것들이 필요합니까?

A) 3단계: PENDING(대기중) → PREPARING(준비중) → COMPLETED(완료)
B) 4단계: PENDING → CONFIRMED(접수) → PREPARING → COMPLETED
C) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 7
SSE 연결 시 태블릿이 세션 종료 이벤트 외에 다른 이벤트도 수신해야 합니까?

A) 세션 종료 이벤트만 (최소 구현)
B) 세션 종료 + 주문 상태 변경 이벤트 (고객이 주문 상태를 실시간으로 확인)
C) Other (please describe after [Answer]: tag below)

[Answer]: A

