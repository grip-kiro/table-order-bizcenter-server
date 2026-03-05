# 테이블오더 서비스 — User Story 생성 계획

## 스토리 생성 질문

아래 질문에 답변해 주세요. [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

### Question 1
스토리 분류(breakdown) 방식은 어떤 것을 선호하십니까?

A) User Journey 기반 — 사용자 워크플로우 흐름에 따라 스토리 구성 (예: 태블릿 설정 → 메뉴 탐색 → 장바구니 → 주문 → 주문 확인)
B) Feature 기반 — 시스템 기능 단위로 스토리 구성 (예: 인증, 메뉴 관리, 주문 관리, 모니터링)
C) Persona 기반 — 사용자 유형별로 스토리 그룹화 (예: 고객 스토리 묶음, 관리자 스토리 묶음)
D) Epic 기반 — 대분류 Epic 아래 세부 스토리 계층 구조
X) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 2
스토리의 세분화 수준은 어느 정도를 원하십니까?

A) 큰 단위 — Epic 수준 (예: "고객으로서 메뉴를 조회하고 주문할 수 있다") — 10개 내외
B) 중간 단위 — Feature 수준 (예: "고객으로서 카테고리별 메뉴를 탐색할 수 있다") — 20~30개
C) 작은 단위 — Task 수준 (예: "고객으로서 좌측 사이드바에서 카테고리를 선택할 수 있다") — 40개 이상
X) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 3
수용 기준(Acceptance Criteria) 형식은 어떤 것을 선호하십니까?

A) Given-When-Then 형식 (BDD 스타일)
B) 체크리스트 형식 (간결한 조건 나열)
C) 혼합 — 핵심 시나리오는 Given-When-Then, 단순 조건은 체크리스트
X) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 4
스토리 우선순위 체계는 어떻게 하시겠습니까?

A) MoSCoW (Must/Should/Could/Won't)
B) 숫자 우선순위 (P1/P2/P3)
C) 우선순위 없이 의존성 순서만 표시
X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 스토리 생성 실행 계획

아래 단계에 따라 스토리를 생성합니다.

### Phase 1: 페르소나 생성
- [x] 고객(Customer) 페르소나 정의
- [x] 관리자(Admin) 페르소나 정의
- [x] 페르소나별 목표, 동기, 불편사항 정리
- [x] `aidlc-docs/inception/user-stories/personas.md` 생성

### Phase 2: 스토리 생성
- [x] 고객용 스토리 작성 (FR-C01 ~ FR-C05 기반)
  - [x] 자동 로그인 및 세션 관리 스토리
  - [x] 메뉴 조회 및 탐색 스토리
  - [x] 장바구니 관리 스토리
  - [x] 주문 생성 스토리
  - [x] 주문 내역 조회 스토리
- [x] 관리자용 스토리 작성 (FR-A01 ~ FR-A04 기반)
  - [x] 매장 인증 스토리
  - [x] 실시간 주문 모니터링 스토리
  - [x] 테이블 관리 스토리
  - [x] 메뉴 관리 스토리
  - [x] 카테고리 관리 스토리

### Phase 3: 수용 기준 작성
- [x] 각 스토리별 수용 기준(Acceptance Criteria) 작성
- [x] INVEST 기준 검증

### Phase 4: 스토리 맵 정리
- [x] 스토리 간 의존성 정리
- [x] 우선순위 부여
- [x] `aidlc-docs/inception/user-stories/stories.md` 생성
