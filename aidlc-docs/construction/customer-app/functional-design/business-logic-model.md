# Customer App — 비즈니스 로직 모델

> Unit 3: Customer App의 페이지/컴포넌트 흐름과 상태 관리 로직을 기술합니다.

---

## 1. 앱 구조 및 라우팅

### 1.1 페이지 구성

| 경로 | 컴포넌트 | 설명 | 인증 필요 |
|------|----------|------|-----------|
| — | LoginPage | 태블릿 초기 설정/로그인 | No |
| `/` | MenuPage | 메뉴 탐색 (기본 화면) | Yes |
| `/cart` | CartPage | 장바구니 | Yes |
| `/orders` | OrdersPage | 주문 내역 | Yes |

### 1.2 인증 분기

```
App 로드
  → 세션 존재? (sessionStorage)
    → Yes → 메뉴 화면 표시
    → No → 저장된 credential 존재? (localStorage)
      → Yes → 자동 로그인 시도
        → 성공 → 메뉴 화면
        → 실패 → 로그인 화면
      → No → 로그인 화면
```

---

## 2. 인증 모듈 (useSession)

### 2.1 로그인 (US-01)

**입력**: storeId, tableNumber, pin

**로직 흐름**:
1. `POST /api/auth/table/login` 호출
2. 성공 시:
   - credential을 localStorage에 저장 (자동 로그인용)
   - TokenPair에서 accessToken, refreshToken 추출
   - TableSession 구성 → sessionStorage에 저장
   - 세션 상태 업데이트 → 메뉴 화면 전환
3. 실패 시:
   - 에러 메시지 표시 ("인증에 실패했습니다")

### 2.2 자동 로그인 (US-02)

**트리거**: 앱 로드 시 세션 없고 credential 있을 때

**로직 흐름**:
1. localStorage에서 credential 로드
2. `POST /api/auth/table/login` 호출
3. 성공 → 세션 설정
4. 실패 → localStorage credential 삭제, 로그인 화면 표시

### 2.3 토큰 갱신 (US-02)

**트리거**: API 호출 시 401 응답

**로직 흐름**:
1. `POST /api/auth/refresh` 호출 (refreshToken 전송)
2. 성공 → 새 accessToken으로 원래 요청 재시도
3. 실패 → 자동 재로그인 시도 (2.2)
4. 재로그인도 실패 → 로그아웃 처리, 로그인 화면 전환

### 2.4 로그아웃

**로직**:
1. localStorage credential 삭제
2. sessionStorage 세션 삭제
3. localStorage 장바구니 삭제
4. 세션 상태 null → 로그인 화면 전환

---

## 3. 메뉴 탐색 모듈 (MenuPage)

### 3.1 카테고리 로드 (US-05)

**트리거**: MenuPage 마운트 시

**로직 흐름**:
1. `GET /api/stores/{storeId}/menus` 호출
2. 응답에서 카테고리 목록 추출 (CategoryWithMenusDTO)
3. "전체" 카테고리를 맨 앞에 추가
4. 카테고리 탭 렌더링

### 3.2 메뉴 목록 표시 (US-05, US-08, US-09)

**로직 흐름**:
1. 선택된 카테고리에 해당하는 메뉴 필터링
2. 각 메뉴를 MenuCard로 렌더링:
   - 이미지: imageUrl 있으면 표시, 없으면 플레이스홀더 아이콘 (US-09)
   - 품절: isSoldOut=true면 오버레이 + "품절" 뱃지 (US-08)
   - 가격: 원화 포맷
3. 무한 스크롤 또는 전체 로드 (API 응답 구조에 따라)

### 3.3 메뉴 상세 모달 (US-06)

**트리거**: 메뉴 카드 클릭

**로직 흐름**:
1. 선택된 메뉴 정보로 모달 표시
2. 큰 이미지, 메뉴명, 설명, 가격, 수량 선택, 장바구니 추가 버튼
3. 품절 메뉴: 장바구니 추가 버튼 비활성화 + 품절 표시
4. 수량 선택 후 "장바구니 추가" → useCart.add() 호출 → 모달 닫기

### 3.4 빠른 장바구니 추가 (US-07)

**트리거**: 메뉴 카드의 "담기"/"+"/"-" 버튼

**로직 흐름**:
1. 품절 메뉴: 버튼 비활성화 (클릭 불가)
2. 장바구니에 없는 메뉴: "담기" 버튼 → useCart.add(menu)
3. 장바구니에 있는 메뉴: 수량 조절 UI (+/-) → useCart.updateQty(id, delta)

---

## 4. 장바구니 모듈 (useCart, CartPage)

### 4.1 장바구니 상태 관리 (US-10, US-13)

**저장소**: localStorage (`table-order-cart` 키)

**상태**:
- `cart: CartItem[]` — 장바구니 항목 배열
- `total: number` — 총 금액 (실시간 계산)
- `count: number` — 총 수량

**동기화**: cart 변경 시 자동으로 localStorage에 저장 (useEffect)

### 4.2 수량 조절 (US-11)

- 증가: qty + 1
- 감소: qty - 1, qty가 0이 되면 항목 삭제
- 삭제: 항목 즉시 제거

### 4.3 장바구니 비우기 (US-12)

- 전체 항목 삭제 → cart = []

---

## 5. 주문 모듈 (useOrders, CartPage)

### 5.1 주문 생성 (US-14, US-15, US-16)

**트리거**: CartPage에서 "주문하기" 버튼

**로직 흐름**:
1. 장바구니가 비어있으면 무시
2. `POST /api/orders` 호출:
   ```json
   {
     "items": [{ "menuId": 1, "quantity": 2 }, ...]
   }
   ```
   - storeId, tableId, sessionId는 JWT에서 서버가 추출
3. 성공 시:
   - 장바구니 비우기 (useCart.clear)
   - 주문 성공 오버레이 표시 (주문 번호, 5초)
   - 5초 후 메뉴 화면으로 자동 리다이렉트
4. 실패 시:
   - 에러 메시지 표시 (alert 또는 토스트)
   - 장바구니 유지
   - 품절 에러(MENU_SOLD_OUT): 품절 메뉴 ID 목록 표시

### 5.2 주문 내역 조회 (US-17)

**트리거**: OrdersPage 마운트 시

**로직 흐름**:
1. `GET /api/tables/{tableId}/orders` 호출 (JWT에서 tableId 추출)
2. 현재 세션 주문만 반환됨 (서버에서 필터링)
3. 시간 순 정렬 (created_at ASC)
4. 각 주문: 주문 번호, 시각, 메뉴/수량, 금액, 상태 표시
5. 상태 표시: PENDING→"대기중"(노란), PREPARING→"준비중"(파란), COMPLETED→"완료"(초록)

---

## 6. API 클라이언트 모듈 (api/client.ts)

### 6.1 HTTP 클라이언트 구조

**인터셉터 패턴**:
1. 요청 시: Authorization 헤더에 accessToken 추가
2. 401 응답 시:
   a. refreshToken으로 토큰 갱신 시도
   b. 성공 → 새 accessToken으로 원래 요청 재시도
   c. 실패 → 자동 재로그인 시도
   d. 재로그인 실패 → 로그아웃

### 6.2 Mock/실제 API 전환

- `REACT_APP_API_BASE` 환경변수로 전환
- 값 없으면 mock 데이터 사용 (개발용)
- 값 있으면 실제 API 호출

---

## 7. 컴포넌트 트리

```
App
├── LoginPage (세션 없을 때)
│   └── 로그인 폼 (storeId, tableNumber, pin)
│
└── AuthenticatedApp (세션 있을 때)
    ├── Header
    │   ├── 테이블 번호 표시
    │   ├── 장바구니 아이콘 + 뱃지 (수량)
    │   └── 네비게이션 (메뉴/장바구니/주문내역)
    ├── OrderSuccessOverlay (주문 성공 시 5초 표시)
    └── Routes
        ├── MenuPage (/)
        │   ├── 카테고리 탭 바
        │   ├── MenuCard 그리드
        │   │   ├── 이미지 (또는 플레이스홀더)
        │   │   ├── 메뉴명, 설명, 가격
        │   │   ├── 품절 오버레이 (isSoldOut)
        │   │   └── 담기 버튼 / 수량 조절
        │   └── MenuDetailModal
        │       ├── 큰 이미지, 상세 설명
        │       ├── 수량 선택
        │       └── 장바구니 추가 버튼
        ├── CartPage (/cart)
        │   ├── 장바구니 항목 목록
        │   │   ├── 메뉴명, 단가, 수량 조절, 소계
        │   │   └── 삭제 버튼
        │   ├── 총 금액
        │   ├── 장바구니 비우기 버튼
        │   └── 주문하기 버튼
        └── OrdersPage (/orders)
            └── 주문 목록
                ├── 주문 번호, 시각
                ├── 메뉴/수량 목록
                ├── 총 금액
                └── 상태 뱃지
```
