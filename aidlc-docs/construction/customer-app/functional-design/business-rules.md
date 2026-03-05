# Customer App — 비즈니스 규칙

> Unit 3: Customer App에 적용되는 UI 규칙, 클라이언트 검증, 상태 관리 규칙을 정의합니다.

---

## 1. 인증 규칙 (Auth Rules)

### BR-APP-AUTH-01: 로그인 정보 저장
- credential (storeId, tableNumber, pin)은 localStorage에 저장 (자동 로그인용)
- 세션 (accessToken, refreshToken 등)은 sessionStorage에 저장
- 브라우저 탭 닫으면 세션 소멸 → 다음 접속 시 자동 로그인 시도

### BR-APP-AUTH-02: 토큰 갱신 흐름
- API 401 응답 시 자동 토큰 갱신 시도
- 갱신 실패 시 localStorage credential로 자동 재로그인
- 재로그인 실패 시 로그아웃 + 로그인 화면 전환
- 토큰 갱신 중 중복 요청 방지 (갱신 Promise 공유)

### BR-APP-AUTH-03: 로그아웃 시 데이터 정리
- localStorage: credential 삭제, 장바구니 삭제
- sessionStorage: 세션 삭제
- 메모리 상태 초기화

### BR-APP-AUTH-04: 로그인 폼 검증
| 필드 | 규칙 | 에러 메시지 |
|------|------|-------------|
| storeId | 필수, 양의 정수 | "매장 ID를 입력해주세요" |
| tableNumber | 필수, 양의 정수 | "테이블 번호를 입력해주세요" |
| pin | 필수, 숫자 4~6자리 | "PIN은 4~6자리 숫자입니다" |

---

## 2. 메뉴 표시 규칙 (Menu Rules)

### BR-APP-MENU-01: 카테고리 탭
- "전체" 탭을 항상 첫 번째에 표시
- 카테고리 순서: displayOrder ASC
- 기본 선택: "전체"
- 카테고리 변경 시 메뉴 목록 리셋 + 재로드

### BR-APP-MENU-02: 메뉴 카드 표시
- 이미지: imageUrl 있으면 `<img>` 표시, 없으면 플레이스홀더 아이콘 (🍽️)
- 가격: 원화 포맷 (예: "9,000원")
- 정렬: displayOrder ASC

### BR-APP-MENU-03: 품절 메뉴 처리
- 카드에 반투명 오버레이 (opacity: 0.5) + "품절" 뱃지
- "담기" 버튼 비활성화 (disabled)
- 수량 조절 UI 미표시
- 모달에서도 "장바구니 추가" 버튼 비활성화

### BR-APP-MENU-04: 메뉴 상세 모달
- 메뉴 카드 클릭 시 모달 오픈 (품절 메뉴도 상세 확인 가능)
- 모달 외부 클릭 또는 닫기 버튼으로 닫기
- 수량 선택: 기본값 1, 최소 1
- 장바구니 추가 시 모달 자동 닫기

---

## 3. 장바구니 규칙 (Cart Rules)

### BR-APP-CART-01: 데이터 영속성
- localStorage 키: `table-order-cart`
- cart 상태 변경 시 자동 동기화 (useEffect)
- 브라우저 새로고침 시 복원 (US-13)

### BR-APP-CART-02: 수량 관리
- 최소 수량: 1 (수량 0이 되면 항목 삭제)
- 동일 메뉴 추가 시 수량 +1 (중복 항목 생성 안 함)
- 수량 증가/감소: ±1 단위

### BR-APP-CART-03: 금액 계산
- 소계: price × qty (각 항목)
- 총 금액: SUM(소계) — 실시간 계산
- 원화 포맷 표시

### BR-APP-CART-04: 장바구니 비우기
- 전체 항목 삭제
- 확인 없이 즉시 실행 (또는 간단한 확인)

---

## 4. 주문 규칙 (Order Rules)

### BR-APP-ORDER-01: 주문 생성 조건
- 장바구니에 1개 이상 항목 필수
- 빈 장바구니에서 주문 버튼 비활성화

### BR-APP-ORDER-02: 주문 요청 데이터
- 서버에 전송: items[{ menuId, quantity }]만 전송
- 가격, 메뉴명 등은 서버에서 검증/계산 (클라이언트 값 무시)
- storeId, tableId, sessionId는 JWT에서 서버가 추출

### BR-APP-ORDER-03: 주문 성공 처리
- 장바구니 자동 비우기
- 주문 성공 오버레이 표시 (주문 번호)
- 5초 후 자동으로 메뉴 화면 리다이렉트
- 주문 내역 목록에 새 주문 추가

### BR-APP-ORDER-04: 주문 실패 처리
- 에러 메시지 표시 (alert 또는 토스트)
- 장바구니 내용 유지 (삭제하지 않음)
- 품절 에러(MENU_SOLD_OUT): "품절된 메뉴가 포함되어 있습니다" 메시지

### BR-APP-ORDER-05: 주문 내역 표시
- 현재 세션 주문만 표시 (서버에서 필터링)
- 시간 순 정렬 (최신 주문 상단)
- 상태 표시: 색상 뱃지 (대기중=노란, 준비중=파란, 완료=초록)
- 주문 번호: 숫자 그대로 표시

---

## 5. UI/UX 규칙

### BR-APP-UX-01: 터치 친화적
- 버튼 최소 크기: 44×44px
- 충분한 터치 영역 간격

### BR-APP-UX-02: 로딩 상태
- API 호출 중 로딩 인디케이터 표시
- 버튼 중복 클릭 방지 (로딩 중 비활성화)

### BR-APP-UX-03: 에러 처리
- 네트워크 에러: "네트워크 연결을 확인해주세요"
- 서버 에러: "일시적인 오류가 발생했습니다"
- 인증 에러: 자동 토큰 갱신 → 실패 시 로그인 화면

### BR-APP-UX-04: 헤더 네비게이션
- 현재 테이블 번호 표시
- 장바구니 아이콘에 수량 뱃지
- 메뉴/장바구니/주문내역 탭 네비게이션

---

## 6. API 연동 규칙

### BR-APP-API-01: 엔드포인트 매핑

| 기능 | 메서드 | 엔드포인트 |
|------|--------|------------|
| 로그인 | POST | `/api/auth/table/login` |
| 토큰 갱신 | POST | `/api/auth/refresh` |
| 메뉴 조회 | GET | `/api/stores/{storeId}/menus` |
| 메뉴 상세 | GET | `/api/menus/{menuId}` |
| 주문 생성 | POST | `/api/orders` |
| 주문 내역 | GET | `/api/tables/{tableId}/orders` |

### BR-APP-API-02: Mock 모드
- `REACT_APP_API_BASE` 환경변수 미설정 시 mock 데이터 사용
- 개발/테스트 시 백엔드 없이 동작 가능

### BR-APP-API-03: 인증 헤더
- 모든 인증 필요 API에 `Authorization: Bearer {accessToken}` 헤더 추가
- 로그인/토큰 갱신 API는 인증 헤더 불필요
