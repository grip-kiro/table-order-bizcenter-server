# Customer App — Functional Design Plan

## 대상 유닛
- **Unit 3: Customer App (고객용 클라이언트)**
- **기술**: React 18+ / TypeScript / CRA (기존 프로젝트)
- **스토리**: US-01, US-02, US-04, US-05, US-06, US-07, US-08, US-09, US-10, US-11, US-12, US-13, US-14, US-15, US-16, US-17

## 기존 코드 현황
- `table-order-client/` 디렉토리에 React + TypeScript 프로젝트 존재
- Mock 데이터 기반으로 기본 기능 구현 완료 (로그인, 메뉴 탐색, 장바구니, 주문, 주문 내역)
- Customer Backend API 연동 시 mock → 실제 API 전환 구조 (`API_BASE` 환경변수 기반)

## 산출물 체크리스트

- [x] Step 1: 유닛 컨텍스트 분석
- [x] Step 2: 명확화 질문 생성 및 답변 수집
- [x] Step 3: 도메인 엔티티 설계 (`domain-entities.md`) — 클라이언트 타입/인터페이스
- [x] Step 4: 비즈니스 로직 모델 설계 (`business-logic-model.md`) — 페이지/컴포넌트 흐름
- [x] Step 5: 비즈니스 규칙 정의 (`business-rules.md`) — UI 규칙/검증
- [x] Step 6: 사용자 승인 ✅ (2026-03-05)

## 질문 (Clarification Questions)

기존 코드가 있으므로 기존 구현을 최대한 활용하되, 백엔드 API 연동 및 누락 기능에 대한 결정이 필요합니다.

---

### Question 1
기존 코드는 CRA(Create React App) 기반입니다. Unit 정의에서는 Vite를 명시했는데, 기존 CRA를 유지할까요?

A) CRA 유지 (기존 코드 활용, 마이그레이션 비용 절감)
B) Vite로 마이그레이션 (성능 향상, 최신 도구)

[Answer]: A — 기존 CRA 유지. MVP에서 빌드 도구 마이그레이션은 불필요.

### Question 2
현재 카테고리 사이드바가 상단 가로 탭 형태로 구현되어 있습니다. 요구사항의 "좌측 사이드바" 레이아웃으로 변경할까요?

A) 현재 상단 탭 유지 (태블릿에서도 사용성 양호)
B) 좌측 사이드바로 변경 (요구사항 준수)

[Answer]: A — 현재 상단 탭 유지. 태블릿 가로 모드에서 상단 탭이 더 효율적.

### Question 3
메뉴 카드에 품절 표시 기능이 현재 미구현입니다. 품절 UI 처리 방식은?

A) 카드에 반투명 오버레이 + "품절" 뱃지, "담기" 버튼 비활성화
B) 카드 자체를 회색 처리 + "품절" 텍스트
C) 품절 메뉴는 목록에서 숨김

[Answer]: A — 오버레이 + 뱃지 방식. 메뉴 존재는 보여주되 주문 불가 표시.

### Question 4
SSE 연결을 통한 세션 종료 감지를 구현할까요, 아니면 API 호출 시 401 응답으로 감지하는 방식만 사용할까요?

A) SSE 구현 (실시간 세션 종료 감지)
B) API 호출 시 401 감지만 (단순 구현)
C) 둘 다 구현 (SSE + 401 fallback)

[Answer]: B — MVP에서는 API 401 감지만. SSE는 추후 구현.
