# 메뉴 조회 기능 구체화 질문

메뉴 조회 및 탐색 기능을 구현하기 위해 결정이 필요한 사항들입니다.
각 질문의 [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

## Question 1
메뉴 카테고리 탐색 UI는 어떤 형태를 원하십니까?

A) 상단 탭 바 (가로 스크롤) — 카테고리 탭을 터치하면 해당 섹션으로 스크롤 이동
B) 좌측 사이드바 — 카테고리 목록이 왼쪽에 고정, 오른쪽에 메뉴 목록 표시
C) 상단 탭 바 + 페이지 전환 — 카테고리 선택 시 해당 카테고리 메뉴만 표시 (다른 카테고리 메뉴는 숨김)
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
메뉴 카드 레이아웃은 어떤 형태를 원하십니까?

A) 그리드 레이아웃 (2열) — 이미지 상단, 메뉴명/가격 하단
B) 그리드 레이아웃 (3열) — 작은 카드, 이미지 + 메뉴명 + 가격
C) 리스트 레이아웃 — 왼쪽 이미지 썸네일 + 오른쪽 메뉴명/설명/가격
D) 그리드 + 리스트 전환 가능
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 3
메뉴 상세 정보는 어떻게 표시하시겠습니까?

A) 메뉴 카드 터치 시 모달/팝업으로 상세 정보 표시 (이미지 크게, 설명, 가격, 수량 선택, 장바구니 추가 버튼)
B) 메뉴 카드 터치 시 별도 상세 페이지로 이동
C) 카드에 모든 정보 표시 (별도 상세 화면 없이 카드에서 바로 장바구니 추가)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 4
메뉴에 "품절" 상태 표시가 필요합니까?

A) 예 — 관리자가 메뉴별 품절 설정 가능, 고객 화면에서 품절 표시 + 주문 불가
B) 예 — 품절 표시만 하되, 주문은 가능 (매장에서 별도 안내)
C) 아니오 — MVP에서는 품절 기능 제외
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 5
메뉴 이미지가 없는 경우 어떻게 표시하시겠습니까?

A) 기본 플레이스홀더 이미지 표시 (음식 아이콘 등)
B) 이미지 영역 없이 텍스트만 표시
C) 카테고리별 기본 이미지 표시
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 6
메뉴 검색 기능이 필요합니까?

A) 예 — 메뉴명 키워드 검색
B) 예 — 메뉴명 + 설명 키워드 검색
C) 아니오 — MVP에서는 카테고리 탐색만 제공
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 7
메뉴 데이터 캐싱 전략은 어떻게 하시겠습니까?

A) 매번 서버에서 최신 데이터 조회 (캐싱 없음)
B) 클라이언트 측 캐싱 — 일정 시간(예: 5분) 동안 캐시 유지, 이후 재조회
C) 서버 측 캐싱 — Spring Cache 등으로 서버에서 캐싱, 메뉴 변경 시 캐시 무효화
D) 클라이언트 + 서버 양쪽 캐싱
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 8
메뉴 카드에서 장바구니 추가는 어떤 방식으로 하시겠습니까?

A) 카드에 "+" 버튼 → 터치 시 수량 1로 바로 장바구니 추가 (수량 변경은 장바구니에서)
B) 카드 터치 → 상세 모달에서 수량 선택 후 장바구니 추가
C) 카드에 "+" 버튼 + 카드 터치 시 상세 모달 둘 다 지원
X) Other (please describe after [Answer]: tag below)

[Answer]: A
