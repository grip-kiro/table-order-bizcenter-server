# 로그인 기능 구현 상세 결정 사항

자동 로그인 및 관리자 로그인을 구현하기 위해 추가로 결정이 필요한 사항들입니다.
각 질문의 [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

## Question 1
태블릿 초기 설정 시 "관리자가 태블릿에서 관리자 인증 후 테이블 번호를 지정"하는 플로우에서, 관리자 인증은 어떻게 수행합니까?

A) 관리자용 앱과 동일한 로그인 (매장ID + 사용자명 + 비밀번호) → 테이블 설정 화면 진입 → 테이블 번호/PIN 설정 → 설정 완료 후 고객용 화면으로 전환
B) 별도의 설정 모드 진입 코드(마스터 PIN 등)를 입력 → 테이블 번호/PIN 설정
C) 고객용 앱에 숨겨진 설정 메뉴(예: 로고 5회 탭) → 관리자 인증 → 테이블 설정
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
JWT 토큰 구조에서 태블릿용 토큰과 관리자용 토큰을 어떻게 구분하시겠습니까?

A) 토큰 내 role 클레임으로 구분 (role: "table" vs role: "admin")
B) 별도의 토큰 발급 엔드포인트 사용 (/api/auth/table-login vs /api/auth/admin-login)
C) 둘 다 (별도 엔드포인트 + role 클레임)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 3
관리자가 "이용 완료" 처리 시 태블릿 세션이 종료된다고 했는데, 태블릿에서 이를 어떻게 감지합니까?

A) SSE를 통해 서버에서 세션 종료 이벤트를 태블릿에 푸시 → 태블릿이 즉시 로그인 화면으로 전환
B) 태블릿이 다음 API 호출 시 401 응답을 받으면 로그인 화면으로 전환
C) 둘 다 (SSE로 즉시 감지 + API 호출 시 401 fallback)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 4
관리자 로그인 시도 제한은 어떻게 구현하시겠습니까?

A) IP 기반 제한 (동일 IP에서 5회 실패 시 15분 잠금)
B) 계정 기반 제한 (동일 계정 5회 실패 시 15분 잠금)
C) 둘 다 (IP + 계정 기반)
D) MVP에서는 간단하게 계정 기반 제한만 적용
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 5
매장(store)과 관리자 계정의 관계는 어떻게 되어야 합니까?

A) 1:1 — 매장당 관리자 계정 1개 (매장ID가 곧 관리자 식별자)
B) 1:N — 매장당 여러 관리자 계정 가능 (owner, staff 등 역할 구분 없이)
C) MVP에서는 1:1로 시작, 추후 1:N 확장 가능하도록 설계
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 6
테이블 등록/관리의 데이터 모델에서, 테이블은 사전에 관리자 화면에서 등록해야 합니까?

A) 예 — 관리자가 먼저 관리자 화면에서 테이블 목록을 등록(번호, PIN 설정) → 태블릿에서 해당 정보로 로그인
B) 아니오 — 태블릿 초기 설정 시 테이블 번호/PIN을 입력하면 자동으로 테이블이 생성됨
C) 둘 다 가능 — 관리자 화면에서 사전 등록도 가능하고, 태블릿에서 새 테이블 생성도 가능
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 7
Refresh Token 전략은 어떻게 하시겠습니까?

A) Access Token만 사용 (만료 없으므로 Refresh Token 불필요)
B) Access Token(짧은 만료) + Refresh Token(긴 만료) 조합 — Access Token 만료 시 Refresh Token으로 자동 갱신
C) Access Token(만료 없음) + Refresh Token(비상용) — 서버 재시작 등으로 토큰 무효화 시 Refresh Token으로 재발급
X) Other (please describe after [Answer]: tag below)

[Answer]: B
