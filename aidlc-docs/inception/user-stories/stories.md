# 테이블오더 서비스 — User Stories

**분류 방식**: User Journey 기반
**세분화 수준**: Feature 수준 (중간 단위)
**수용 기준 형식**: Given-When-Then (BDD)
**우선순위 체계**: MoSCoW (Must/Should/Could/Won't)

---

## Journey 1: 태블릿 설정 및 인증

### US-01: 태블릿 설정 모드 진입
**As a** 매장 관리자,
**I want to** 태블릿에서 마스터 PIN을 입력하여 설정 모드에 진입할 수 있다,
**So that** 태블릿을 특정 테이블에 연결할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 태블릿이 미설정 상태일 때, When 마스터 PIN을 올바르게 입력하면, Then 설정 모드 화면이 표시된다
- Given 설정 모드 진입 시, When 잘못된 마스터 PIN을 입력하면, Then 오류 메시지가 표시된다
- Given 설정 모드에서, When 테이블 번호와 PIN을 입력하고 확인하면, Then 태블릿 전용 토큰이 발급되고 고객용 메뉴 화면으로 전환된다

### US-02: 태블릿 자동 로그인
**As a** 고객,
**I want to** 태블릿이 자동으로 로그인되어 있어서 별도 인증 없이 바로 메뉴를 볼 수 있다,
**So that** 대기 시간 없이 즉시 주문할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 태블릿이 설정 완료 상태일 때, When 브라우저를 열면, Then 저장된 토큰으로 자동 로그인되어 메뉴 화면이 표시된다
- Given Access Token이 만료되었을 때, When 페이지를 요청하면, Then Refresh Token으로 자동 갱신되어 끊김 없이 사용할 수 있다
- Given 토큰이 완전히 무효화되었을 때, When 페이지를 요청하면, Then localStorage의 정보로 자동 재로그인을 시도한다
- Given 자동 재로그인이 실패했을 때, When 재시도가 실패하면, Then 설정(로그인) 화면이 표시된다

### US-03: 태블릿 기기 제한
**As a** 매장 관리자,
**I want to** 하나의 테이블에 하나의 기기만 로그인할 수 있도록 제한한다,
**So that** 중복 주문이나 혼란을 방지할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 테이블 1에 기기A가 로그인된 상태에서, When 기기B가 같은 테이블로 로그인하면, Then 기기A의 세션이 무효화된다
- Given 기존 세션이 무효화된 기기에서, When API를 호출하면, Then 401 응답을 받고 로그인 화면으로 전환된다

### US-04: 네트워크 복구 시 세션 자동 복구
**As a** 고객,
**I want to** 네트워크가 끊겼다가 복구되면 자동으로 세션이 복구된다,
**So that** 네트워크 문제로 주문 과정이 중단되지 않는다.

**Priority**: Should

**Acceptance Criteria**:
- Given 네트워크가 끊긴 상태에서, When 네트워크가 복구되면, Then 토큰이 유효한 경우 자동으로 세션이 복구된다
- Given 네트워크 복구 후 토큰이 무효한 경우, When 세션 복구를 시도하면, Then 자동 재로그인 플로우가 실행된다

---

## Journey 2: 메뉴 탐색

### US-05: 카테고리별 메뉴 탐색
**As a** 고객,
**I want to** 좌측 사이드바에서 카테고리를 선택하여 메뉴를 탐색할 수 있다,
**So that** 원하는 종류의 메뉴를 빠르게 찾을 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 화면에 진입했을 때, When 화면이 로드되면, Then 좌측에 카테고리 사이드바가 표시되고 오른쪽에 메뉴 카드가 3열 그리드로 표시된다
- Given 카테고리 사이드바에서, When 특정 카테고리를 터치하면, Then 해당 카테고리의 메뉴만 오른쪽 영역에 표시된다
- Given 메뉴 카드에, When 카드가 표시되면, Then 이미지, 메뉴명, 가격이 표시된다

### US-06: 메뉴 상세 정보 확인
**As a** 고객,
**I want to** 메뉴 카드를 터치하여 상세 정보를 모달로 확인할 수 있다,
**So that** 메뉴 설명과 큰 이미지를 보고 주문 여부를 결정할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 카드를 터치했을 때, When 모달이 열리면, Then 큰 이미지, 메뉴명, 설명, 가격, 수량 선택, 장바구니 추가 버튼이 표시된다
- Given 모달에서, When 수량을 선택하고 장바구니 추가 버튼을 터치하면, Then 해당 수량으로 장바구니에 추가되고 모달이 닫힌다

### US-07: 메뉴 카드에서 빠른 장바구니 추가
**As a** 고객,
**I want to** 메뉴 카드의 "+" 버튼을 터치하여 수량 1로 바로 장바구니에 추가할 수 있다,
**So that** 빠르게 여러 메뉴를 장바구니에 담을 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 카드에 "+" 버튼이 표시될 때, When "+" 버튼을 터치하면, Then 해당 메뉴가 수량 1로 장바구니에 추가된다
- Given 이미 장바구니에 있는 메뉴의 "+" 버튼을 터치하면, When 추가되면, Then 해당 메뉴의 수량이 1 증가한다
- Given 품절 상태인 메뉴에서, When "+" 버튼이 표시되면, Then 버튼이 비활성화되고 품절 표시가 나타난다

### US-08: 품절 메뉴 표시
**As a** 고객,
**I want to** 품절된 메뉴가 명확하게 표시되어 주문할 수 없음을 알 수 있다,
**So that** 주문 불가능한 메뉴를 선택하는 실수를 방지할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴가 품절 상태일 때, When 메뉴 카드가 표시되면, Then 품절 표시(오버레이/뱃지)가 나타나고 "+" 버튼이 비활성화된다
- Given 품절 메뉴 카드를 터치했을 때, When 모달이 열리면, Then 품절 상태가 표시되고 장바구니 추가 버튼이 비활성화된다

### US-09: 이미지 없는 메뉴 표시
**As a** 고객,
**I want to** 이미지가 없는 메뉴도 플레이스홀더 이미지로 일관되게 표시된다,
**So that** 메뉴 목록이 깔끔하게 보인다.

**Priority**: Should

**Acceptance Criteria**:
- Given 메뉴에 이미지 URL이 없을 때, When 메뉴 카드가 표시되면, Then 기본 플레이스홀더 이미지(음식 아이콘)가 표시된다

---

## Journey 3: 장바구니 관리

### US-10: 장바구니에 메뉴 추가
**As a** 고객,
**I want to** 선택한 메뉴를 장바구니에 추가할 수 있다,
**So that** 주문 전에 여러 메뉴를 모아둘 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴를 장바구니에 추가했을 때, When 장바구니를 확인하면, Then 추가된 메뉴명, 수량, 단가, 소계가 표시된다
- Given 장바구니에 메뉴가 있을 때, When 총 금액을 확인하면, Then 모든 메뉴의 소계 합산이 실시간으로 표시된다

### US-11: 장바구니 수량 조절 및 삭제
**As a** 고객,
**I want to** 장바구니에서 메뉴 수량을 변경하거나 삭제할 수 있다,
**So that** 주문 전에 원하는 대로 조정할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 장바구니에 메뉴가 있을 때, When 수량 증가 버튼을 터치하면, Then 수량이 1 증가하고 소계와 총 금액이 재계산된다
- Given 장바구니에 메뉴가 있을 때, When 수량 감소 버튼을 터치하면, Then 수량이 1 감소하고 소계와 총 금액이 재계산된다
- Given 수량이 1인 메뉴에서, When 수량 감소 버튼을 터치하면, Then 해당 메뉴가 장바구니에서 삭제된다
- Given 장바구니에 메뉴가 있을 때, When 삭제 버튼을 터치하면, Then 해당 메뉴가 장바구니에서 즉시 삭제된다

### US-12: 장바구니 비우기
**As a** 고객,
**I want to** 장바구니를 한 번에 비울 수 있다,
**So that** 처음부터 다시 메뉴를 선택할 수 있다.

**Priority**: Should

**Acceptance Criteria**:
- Given 장바구니에 메뉴가 있을 때, When 장바구니 비우기 버튼을 터치하면, Then 모든 메뉴가 삭제되고 총 금액이 0원이 된다

### US-13: 장바구니 데이터 유지
**As a** 고객,
**I want to** 페이지를 새로고침해도 장바구니 내용이 유지된다,
**So that** 실수로 새로고침해도 담아둔 메뉴를 잃지 않는다.

**Priority**: Must

**Acceptance Criteria**:
- Given 장바구니에 메뉴가 담긴 상태에서, When 브라우저를 새로고침하면, Then 장바구니 내용(메뉴, 수량)이 그대로 유지된다

---

## Journey 4: 주문 생성

### US-14: 주문 내역 확인 및 주문 확정
**As a** 고객,
**I want to** 장바구니의 주문 내역을 최종 확인하고 주문을 확정할 수 있다,
**So that** 실수 없이 정확한 주문을 할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 장바구니에 메뉴가 있을 때, When 주문하기 버튼을 터치하면, Then 주문 내역(메뉴명, 수량, 단가, 총 금액)이 최종 확인 화면에 표시된다
- Given 최종 확인 화면에서, When 주문 확정 버튼을 터치하면, Then 서버에 주문이 전송된다

### US-15: 주문 성공 처리
**As a** 고객,
**I want to** 주문이 성공하면 주문 번호를 확인하고 자동으로 메뉴 화면으로 돌아간다,
**So that** 추가 주문을 바로 할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 주문이 성공했을 때, When 성공 화면이 표시되면, Then 주문 번호가 5초간 표시된다
- Given 주문 성공 후, When 5초가 경과하면, Then 장바구니가 자동으로 비워지고 메뉴 화면으로 자동 리다이렉트된다

### US-16: 주문 실패 처리
**As a** 고객,
**I want to** 주문이 실패하면 에러 메시지를 확인하고 장바구니가 유지된다,
**So that** 다시 주문을 시도할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 주문이 실패했을 때, When 에러가 발생하면, Then 에러 메시지가 표시되고 장바구니 내용은 그대로 유지된다

---

## Journey 5: 주문 내역 확인

### US-17: 현재 세션 주문 내역 조회
**As a** 고객,
**I want to** 현재 테이블 세션의 주문 내역을 확인할 수 있다,
**So that** 지금까지 주문한 메뉴와 총 금액을 파악할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 주문 내역 화면에 진입했을 때, When 주문 목록이 로드되면, Then 현재 세션의 주문만 시간 순으로 표시된다
- Given 주문 내역에서, When 각 주문을 확인하면, Then 주문 번호, 시각, 메뉴/수량, 금액, 상태(대기중/준비중/완료)가 표시된다
- Given 이전 세션(이용 완료 처리된)의 주문은, When 주문 내역을 조회하면, Then 표시되지 않는다

---

## Journey 6: 관리자 인증

### US-18: 관리자 로그인
**As a** 매장 관리자,
**I want to** 매장 식별자, 사용자명, 비밀번호로 로그인할 수 있다,
**So that** 매장 관리 시스템에 접근할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 관리자 로그인 화면에서, When 올바른 매장ID/사용자명/비밀번호를 입력하면, Then JWT 토큰이 발급되고 대시보드로 이동한다
- Given 로그인 성공 후, When 브라우저를 새로고침하면, Then 세션이 유지되어 재로그인 없이 사용할 수 있다
- Given 로그인 후 16시간이 경과하면, When 다음 요청을 보내면, Then 자동 로그아웃되고 로그인 화면으로 이동한다

### US-19: 관리자 로그인 시도 제한
**As a** 매장 관리자,
**I want to** 로그인 시도가 제한되어 무차별 대입 공격으로부터 보호된다,
**So that** 계정 보안이 유지된다.

**Priority**: Must

**Acceptance Criteria**:
- Given 동일 계정으로 5회 로그인 실패 시, When 6번째 시도를 하면, Then 15분간 해당 계정이 잠기고 잠금 메시지가 표시된다
- Given 계정이 잠긴 상태에서, When 15분이 경과하면, Then 다시 로그인을 시도할 수 있다

---

## Journey 7: 실시간 주문 모니터링

### US-20: 테이블별 주문 대시보드
**As a** 매장 관리자,
**I want to** 테이블별 주문 현황을 그리드 대시보드로 한눈에 볼 수 있다,
**So that** 매장 전체 상황을 빠르게 파악할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 대시보드에 진입했을 때, When 화면이 로드되면, Then 테이블별 카드가 그리드 형태로 표시된다
- Given 각 테이블 카드에, When 카드가 표시되면, Then 테이블 번호, 총 주문액, 최신 주문 미리보기가 표시된다
- Given 테이블 카드를 클릭했을 때, When 상세 보기가 열리면, Then 해당 테이블의 전체 주문 메뉴 목록이 표시된다

### US-21: 실시간 주문 업데이트
**As a** 매장 관리자,
**I want to** 새로운 주문이 들어오면 2초 이내에 대시보드에 반영된다,
**So that** 주문을 놓치지 않고 즉시 확인할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 대시보드가 열린 상태에서, When 고객이 새 주문을 생성하면, Then SSE를 통해 2초 이내에 해당 테이블 카드에 주문이 반영된다
- Given 신규 주문이 도착했을 때, When 카드가 업데이트되면, Then 시각적 강조(색상 변경, 애니메이션)가 적용된다

### US-22: 주문 상태 변경
**As a** 매장 관리자,
**I want to** 주문 상태를 대기중 → 준비중 → 완료로 변경할 수 있다,
**So that** 주문 처리 진행 상황을 관리할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 대기중 상태의 주문에서, When 상태 변경 버튼을 클릭하면, Then 준비중으로 변경된다
- Given 준비중 상태의 주문에서, When 상태 변경 버튼을 클릭하면, Then 완료로 변경된다
- Given 주문 상태가 변경되면, When 고객 화면에서 주문 내역을 조회하면, Then 변경된 상태가 반영되어 표시된다

### US-23: 테이블별 필터링
**As a** 매장 관리자,
**I want to** 특정 테이블의 주문만 필터링하여 볼 수 있다,
**So that** 특정 테이블의 상황을 집중적으로 확인할 수 있다.

**Priority**: Should

**Acceptance Criteria**:
- Given 대시보드에서, When 테이블 필터를 선택하면, Then 선택한 테이블의 카드만 표시된다

---

## Journey 8: 테이블 관리

### US-24: 테이블 사전 등록
**As a** 매장 관리자,
**I want to** 관리자 화면에서 테이블을 사전 등록(번호, PIN 설정)할 수 있다,
**So that** 태블릿을 해당 테이블에 연결할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 테이블 관리 화면에서, When 테이블 번호와 PIN(4~6자리 숫자)을 입력하고 등록하면, Then 새 테이블이 생성된다
- Given 이미 존재하는 테이블 번호를 입력했을 때, When 등록을 시도하면, Then 중복 오류 메시지가 표시된다

### US-25: 주문 삭제 (직권 수정)
**As a** 매장 관리자,
**I want to** 특정 주문을 삭제할 수 있다,
**So that** 잘못된 주문을 정정할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 테이블 상세에서 주문 삭제 버튼을 클릭했을 때, When 확인 팝업에서 확인을 누르면, Then 주문이 즉시 삭제되고 테이블 총 주문액이 재계산된다
- Given 주문 삭제 후, When 대시보드를 확인하면, Then 해당 테이블 카드의 총 주문액이 업데이트된다

### US-26: 테이블 이용 완료 처리
**As a** 매장 관리자,
**I want to** 테이블 이용 완료를 처리하여 세션을 종료할 수 있다,
**So that** 새 고객이 이전 주문 내역 없이 시작할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 테이블 카드에서 이용 완료 버튼을 클릭했을 때, When 확인 팝업에서 확인을 누르면, Then 해당 세션의 주문 내역이 과거 이력으로 이동된다
- Given 이용 완료 처리 후, When 대시보드를 확인하면, Then 해당 테이블의 현재 주문 목록과 총 주문액이 0으로 리셋된다
- Given 이용 완료 처리 후, When 태블릿에서 SSE 이벤트를 수신하면, Then 태블릿 세션이 종료되고 로그인 화면으로 전환된다

### US-27: 과거 주문 내역 조회
**As a** 매장 관리자,
**I want to** 테이블별 과거 주문 내역을 조회할 수 있다,
**So that** 이전 고객의 주문 이력을 확인할 수 있다.

**Priority**: Should

**Acceptance Criteria**:
- Given 테이블 상세에서 과거 내역 버튼을 클릭했을 때, When 과거 주문 목록이 로드되면, Then 시간 역순으로 과거 주문이 표시된다
- Given 과거 주문 목록에서, When 각 주문을 확인하면, Then 주문 번호, 시각, 메뉴 목록, 총 금액, 이용 완료 시각이 표시된다
- Given 과거 주문 목록에서, When 날짜 필터를 적용하면, Then 해당 날짜 범위의 주문만 표시된다

---

## Journey 9: 메뉴 및 카테고리 관리

### US-28: 카테고리 관리
**As a** 매장 관리자,
**I want to** 메뉴 카테고리를 추가/수정/삭제/순서 변경할 수 있다,
**So that** 매장 메뉴 구조를 자유롭게 구성할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 카테고리 관리 화면에서, When 새 카테고리명을 입력하고 추가하면, Then 카테고리가 생성된다
- Given 기존 카테고리에서, When 이름을 수정하면, Then 고객 화면의 사이드바에도 변경이 반영된다
- Given 메뉴가 없는 카테고리에서, When 삭제하면, Then 카테고리가 삭제된다
- Given 메뉴가 있는 카테고리에서, When 삭제를 시도하면, Then 삭제 불가 메시지가 표시된다 (또는 메뉴 재배치 안내)

### US-29: 메뉴 등록
**As a** 매장 관리자,
**I want to** 새 메뉴를 등록할 수 있다,
**So that** 고객이 주문할 수 있는 메뉴를 추가할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 등록 화면에서, When 메뉴명, 가격, 설명, 카테고리(복수 선택), 이미지 URL을 입력하고 저장하면, Then 메뉴가 등록되고 고객 화면에 표시된다
- Given 필수 필드(메뉴명, 가격, 카테고리)가 비어있을 때, When 저장을 시도하면, Then 유효성 검증 오류 메시지가 표시된다
- Given 가격이 0~1,000,000 범위를 벗어날 때, When 저장을 시도하면, Then 가격 범위 오류 메시지가 표시된다

### US-30: 메뉴 수정
**As a** 매장 관리자,
**I want to** 기존 메뉴 정보를 수정할 수 있다,
**So that** 가격 변경, 설명 업데이트 등을 반영할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 목록에서 수정 버튼을 클릭했을 때, When 수정 화면이 열리면, Then 기존 메뉴 정보가 채워진 상태로 표시된다
- Given 수정 화면에서, When 정보를 변경하고 저장하면, Then 변경 사항이 반영되고 서버 캐시가 무효화된다

### US-31: 메뉴 삭제 (소프트 삭제)
**As a** 매장 관리자,
**I want to** 메뉴를 삭제(비활성화)할 수 있다,
**So that** 더 이상 판매하지 않는 메뉴를 고객 화면에서 숨길 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 목록에서 삭제 버튼을 클릭했을 때, When 확인 팝업에서 확인을 누르면, Then 메뉴가 비활성화(숨김)되어 고객 화면에서 사라진다
- Given 삭제된 메뉴가 포함된 기존 주문 내역에서, When 주문을 조회하면, Then 주문 시점의 메뉴명과 가격이 그대로 표시된다

### US-32: 메뉴 품절 설정
**As a** 매장 관리자,
**I want to** 메뉴의 품절 상태를 설정/해제할 수 있다,
**So that** 재료 소진 시 고객이 해당 메뉴를 주문하지 못하도록 할 수 있다.

**Priority**: Must

**Acceptance Criteria**:
- Given 메뉴 목록에서, When 품절 토글을 활성화하면, Then 해당 메뉴가 고객 화면에서 품절로 표시되고 주문 불가 처리된다
- Given 품절 상태의 메뉴에서, When 품절 토글을 해제하면, Then 고객 화면에서 정상 주문 가능 상태로 복구된다

### US-33: 메뉴 노출 순서 변경
**As a** 매장 관리자,
**I want to** 드래그 앤 드롭으로 메뉴 노출 순서를 변경할 수 있다,
**So that** 인기 메뉴나 추천 메뉴를 상단에 배치할 수 있다.

**Priority**: Should

**Acceptance Criteria**:
- Given 메뉴 관리 화면에서, When 메뉴 항목을 드래그하여 다른 위치에 드롭하면, Then 순서가 변경되고 고객 화면에 반영된다

---

## 스토리 의존성 맵

```
US-24 (테이블 등록) → US-01 (태블릿 설정) → US-02 (자동 로그인)
US-28 (카테고리 관리) → US-29 (메뉴 등록) → US-05 (메뉴 탐색)
US-05 (메뉴 탐색) → US-07 (빠른 추가) / US-06 (상세 모달) → US-10 (장바구니 추가)
US-10 (장바구니) → US-14 (주문 확인) → US-15 (주문 성공) → US-17 (주문 내역)
US-18 (관리자 로그인) → US-20 (대시보드) → US-21 (실시간 업데이트)
US-20 (대시보드) → US-22 (상태 변경) / US-25 (주문 삭제) / US-26 (이용 완료)
```

## 우선순위 요약

| 우선순위 | 스토리 |
|----------|--------|
| Must | US-01~03, US-05~08, US-10~11, US-13~17, US-18~22, US-24~26, US-28~32 |
| Should | US-04, US-09, US-12, US-23, US-27, US-33 |
| Could | (없음) |
| Won't | (없음) |
