# Admin App — Functional Design Plan

## 대상 유닛
- **Unit 4: Admin App (관리자용 클라이언트)**
- **기술**: React 18+ / TypeScript / Vite
- **스토리**: US-18, US-19, US-20, US-21, US-22, US-23, US-24, US-25, US-26, US-27, US-28, US-29, US-30, US-31, US-32, US-33

## 기존 코드 현황
- Admin App 코드 없음 — 신규 프로젝트 생성 필요
- Admin Backend API 설계 완료 (Unit 2 Functional Design 참조)

## 산출물 체크리스트

- [x] Step 1: 유닛 컨텍스트 분석
- [x] Step 2: 명확화 질문 생성 및 답변 수집
- [x] Step 3: 도메인 엔티티 설계 (`domain-entities.md`)
- [x] Step 4: 비즈니스 로직 모델 설계 (`business-logic-model.md`)
- [x] Step 5: 비즈니스 규칙 정의 (`business-rules.md`)
- [ ] Step 6: 사용자 승인 ✅ (2026-03-05)

## 질문 (Clarification Questions)

---

### Question 1
Admin App의 UI 프레임워크/컴포넌트 라이브러리를 사용할까요?

A) 순수 CSS (Customer App과 동일 방식)
B) 경량 UI 라이브러리 (예: CSS Modules + 자체 컴포넌트)

[Answer]: A — 순수 CSS. MVP에서 외부 라이브러리 의존성 최소화.

### Question 2
메뉴 노출 순서 변경(US-33)의 드래그 앤 드롭 구현 방식은?

A) HTML5 Drag and Drop API (라이브러리 없이)
B) 순서 변경 버튼 (위/아래 화살표) — 드래그 앤 드롭 대신 단순 구현

[Answer]: B — MVP에서는 위/아래 버튼으로 단순 구현. 드래그 앤 드롭은 추후.

### Question 3
대시보드의 테이블 카드 레이아웃은?

A) 고정 열 그리드 (예: 4열)
B) 반응형 그리드 (화면 크기에 따라 열 수 변경)

[Answer]: B — 반응형 그리드. 다양한 화면 크기 대응.

### Question 4
과거 주문 내역(US-27) 표시 방식은?

A) 별도 페이지
B) 모달/드로어

[Answer]: B — 모달. 대시보드에서 벗어나지 않고 확인 가능.
