# 자동 로그인 기능 구체화 질문

테이블 태블릿 자동 로그인 기능을 구체화하기 위한 질문입니다.
각 질문의 [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

## Question 1
테이블 태블릿의 초기 설정은 누가 수행합니까?

A) 매장 관리자가 관리자 화면에서 테이블을 등록하면, 태블릿에서 매장ID + 테이블번호 + 비밀번호를 입력하여 연결
B) 매장 관리자가 태블릿에서 직접 관리자 인증 후 테이블 번호를 지정
C) 관리자 화면에서 테이블 등록 시 QR코드/URL을 생성하고, 태블릿에서 해당 URL로 접속하면 자동 설정
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
테이블 태블릿의 세션(토큰) 만료 시간은 어떻게 설정하시겠습니까?

A) 관리자와 동일하게 16시간
B) 24시간 (하루 영업 시간 커버)
C) 만료 없음 (태블릿은 항상 로그인 상태 유지, 관리자가 수동으로 세션 종료)
D) 영업 시간 기반 (예: 매일 새벽 자동 만료 후 자동 재로그인)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 3
자동 로그인 토큰이 만료되었을 때 어떻게 처리하시겠습니까?

A) 저장된 매장ID/테이블번호/비밀번호로 자동 재로그인 시도
B) 로그인 화면을 표시하고 관리자가 다시 설정
C) 자동 재로그인 시도 → 실패 시 로그인 화면 표시
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 4
하나의 테이블에 여러 태블릿(기기)이 동시에 로그인할 수 있어야 합니까?

A) 아니오 — 하나의 테이블에 하나의 기기만 허용 (새 기기 로그인 시 기존 세션 무효화)
B) 예 — 여러 기기가 동일 테이블로 동시 로그인 가능
C) MVP에서는 고려하지 않음 (단일 기기 가정)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 5
테이블 비밀번호의 용도와 복잡도는 어떻게 설정하시겠습니까?

A) 단순 숫자 PIN (4~6자리) — 태블릿 초기 설정 시 빠른 입력 목적
B) 일반 비밀번호 (영문+숫자, 8자 이상) — 보안 강화 목적
C) 관리자가 자유롭게 설정 (최소 길이만 제한)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 6
테이블 세션(고객 식사 세션)과 태블릿 로그인 세션의 관계는 어떻게 되어야 합니까?

A) 독립적 — 태블릿 로그인 세션은 유지되고, 고객 식사 세션만 관리자가 시작/종료
B) 연동 — 관리자가 테이블 이용 완료 처리 시 태블릿 로그인 세션도 함께 종료
C) 독립적이되, 이용 완료 시 태블릿 화면을 초기 메뉴 화면으로 리셋
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 7
태블릿이 네트워크 연결이 끊겼다가 복구되었을 때 어떻게 처리하시겠습니까?

A) 자동으로 세션 복구 시도 (토큰이 유효하면 바로 복구)
B) 네트워크 오류 화면 표시 후 수동 새로고침
C) MVP에서는 별도 처리 없이 브라우저 기본 동작에 맡김
X) Other (please describe after [Answer]: tag below)

[Answer]: A
