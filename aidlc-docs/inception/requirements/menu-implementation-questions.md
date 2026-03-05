# 메뉴 기능 구현 상세 결정 사항

메뉴 조회(FR-C02) 및 메뉴 관리(FR-A04)를 구현하기 위해 추가로 결정이 필요한 사항들입니다.
각 질문의 [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

## Question 1
카테고리는 어떻게 관리됩니까?

A) 고정 카테고리 — 시드 데이터로 미리 정의 (예: 메인, 사이드, 음료, 디저트), 관리자가 변경 불가
B) 관리자가 카테고리 CRUD 가능 — 카테고리 추가/수정/삭제/순서 변경
C) MVP에서는 고정 카테고리, 추후 관리 기능 추가
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
메뉴 노출 순서 조정은 어떤 방식으로 구현하시겠습니까?

A) 드래그 앤 드롭으로 순서 변경
B) 숫자(정렬 순서값) 직접 입력
C) 위/아래 화살표 버튼으로 순서 이동
D) MVP에서는 등록 순서(최신순) 또는 이름순 자동 정렬만 제공
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 3
메뉴 가격의 데이터 타입과 범위는 어떻게 설정하시겠습니까?

A) 정수(원 단위) — 최소 0원, 최대 1,000,000원
B) 정수(원 단위) — 최소 100원, 최대 999,999원
C) 소수점 허용 — 최소 0, 최대 1,000,000 (해외 매장 고려)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 4
메뉴 삭제 시 기존 주문 내역에 있는 메뉴는 어떻게 처리합니까?

A) 소프트 삭제 — 메뉴를 비활성화(숨김)하고, 기존 주문 내역에서는 메뉴명/가격 그대로 표시
B) 하드 삭제 — 메뉴 완전 삭제, 기존 주문 내역에는 주문 시점의 메뉴명/가격이 스냅샷으로 저장되어 있으므로 영향 없음
C) 삭제 불가 — 주문 내역이 있는 메뉴는 삭제 대신 숨김 처리만 가능
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 5
하나의 메뉴가 여러 카테고리에 속할 수 있습니까?

A) 아니오 — 메뉴는 하나의 카테고리에만 속함 (1:N 관계)
B) 예 — 메뉴가 여러 카테고리에 속할 수 있음 (N:M 관계)
C) MVP에서는 1:N, 추후 N:M 확장 가능하도록 설계
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 6
메뉴 이미지 URL 유효성 검증은 어떻게 하시겠습니까?

A) URL 형식만 검증 (정규식으로 URL 패턴 체크)
B) URL 형식 검증 + 실제 이미지 접근 가능 여부 확인 (HEAD 요청)
C) 검증 없음 — 관리자가 입력한 URL을 그대로 저장
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 7
메뉴 관리 화면에서 메뉴 목록은 어떻게 표시하시겠습니까?

A) 테이블(표) 형태 — 메뉴명, 카테고리, 가격, 품절 여부, 순서, 수정/삭제 버튼
B) 카드 형태 — 고객 화면과 유사한 카드 + 관리 버튼
C) 카테고리별 접이식(아코디언) 목록
X) Other (please describe after [Answer]: tag below)

[Answer]: A
