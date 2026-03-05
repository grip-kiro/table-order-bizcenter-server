# 테이블오더 Admin Backend API 목록

> Base URL: `http://localhost:8080`
> 인증: `Authorization: Bearer {accessToken}` (로그인/토큰갱신/로그아웃 제외)

---

## 1. 인증 (Auth)

### POST /api/auth/admin/login
관리자 로그인. 성공 시 Access Token + Refresh Token 발급.

- 로그인 5회 연속 실패 시 15분 계정 잠금

**Request Body**
```json
{
  "storeId": 1,
  "username": "admin",
  "password": "admin1234"
}
```

**Response 200**
```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi...",
  "expiresIn": 57600
}
```

**Error 401** — 인증 실패
```json
{
  "status": 401,
  "code": "INVALID_CREDENTIALS",
  "message": "인증 정보가 올바르지 않습니다",
  "timestamp": "2026-03-05T10:00:00",
  "details": { "remainingAttempts": 3 }
}
```

**Error 423** — 계정 잠금
```json
{
  "status": 423,
  "code": "ACCOUNT_LOCKED",
  "message": "계정이 잠겼습니다",
  "timestamp": "2026-03-05T10:00:00",
  "details": { "lockUntil": "2026-03-05T10:15:00", "remainingMinutes": 14 }
}
```

---

### POST /api/auth/admin/register
관리자 회원가입. 매장 ID + 사용자명 + 비밀번호로 계정 생성.

- 같은 매장 내 사용자명 중복 불가
- 비밀번호 6자 이상, 사용자명 3~50자

**Request Body**
```json
{
  "storeId": 1,
  "username": "newadmin",
  "password": "pass1234"
}
```

**Response 201**
```json
{
  "id": 2,
  "storeId": 1,
  "username": "newadmin"
}
```

**Error 409** — 사용자명 중복
```json
{
  "status": 409,
  "code": "USERNAME_DUPLICATE",
  "message": "이미 존재하는 사용자명입니다",
  "timestamp": "2026-03-05T10:00:00"
}
```

---

### POST /api/auth/refresh
Access Token 갱신. Refresh Token으로 새 Access Token 발급.

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOi..."
}
```

**Response 200**
```json
{
  "accessToken": "eyJhbGciOi...(새 토큰)",
  "refreshToken": "eyJhbGciOi...(기존과 동일)",
  "expiresIn": 57600
}
```

---

### POST /api/auth/logout
로그아웃. Refresh Token 무효화.

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOi..."
}
```

**Response 200** — 빈 응답

---

## 2. 카테고리 관리

### GET /api/stores/{storeId}/categories
매장의 카테고리 목록 조회. display_order 순 정렬.

**Response 200**
```json
[
  { "id": 1, "name": "메인", "displayOrder": 1 },
  { "id": 2, "name": "사이드", "displayOrder": 2 },
  { "id": 3, "name": "음료", "displayOrder": 3 },
  { "id": 4, "name": "디저트", "displayOrder": 4 }
]
```

---

### POST /api/categories
카테고리 등록. displayOrder는 자동으로 마지막 순서 +1.

**Request Body**
```json
{
  "name": "주류"
}
```

**Response 201**
```json
{ "id": 5, "name": "주류", "displayOrder": 5 }
```

---

### PUT /api/categories/{categoryId}
카테고리명 수정.

**Request Body**
```json
{
  "name": "주류/음료"
}
```

**Response 200**
```json
{ "id": 5, "name": "주류/음료", "displayOrder": 5 }
```

---

### DELETE /api/categories/{categoryId}
카테고리 삭제. 활성 메뉴가 연결되어 있으면 삭제 불가.

**Response 204** — 빈 응답

**Error 409**
```json
{
  "status": 409,
  "code": "CATEGORY_HAS_MENUS",
  "message": "메뉴가 연결된 카테고리는 삭제할 수 없습니다"
}
```

---

### PATCH /api/categories/order
카테고리 노출 순서 일괄 변경.

**Request Body**
```json
{
  "items": [
    { "id": 1, "displayOrder": 2 },
    { "id": 2, "displayOrder": 1 },
    { "id": 3, "displayOrder": 3 }
  ]
}
```

**Response 200** — 변경된 카테고리 목록 (GET과 동일 형태)

---

## 3. 메뉴 관리

### GET /api/stores/{storeId}/menus
매장의 전체 메뉴 조회 (관리자용: 삭제된 메뉴 포함). display_order 순 정렬.

**Response 200**
```json
[
  {
    "id": 1,
    "name": "불고기 정식",
    "description": "소불고기와 밥, 반찬이 함께 제공됩니다",
    "price": 12000,
    "imageUrl": null,
    "soldOut": false,
    "deleted": false,
    "displayOrder": 1,
    "categories": [
      { "id": 1, "name": "메인", "displayOrder": 1 }
    ]
  }
]
```

---

### POST /api/menus
메뉴 등록. categoryIds는 1개 이상 필수.

**Request Body**
```json
{
  "name": "제육볶음",
  "price": 11000,
  "description": "매콤한 제육볶음 정식",
  "imageUrl": "https://example.com/img/jeyuk.jpg",
  "categoryIds": [1]
}
```

**Response 201** — 생성된 메뉴 (GET 응답의 단일 항목과 동일 형태)

---

### PUT /api/menus/{menuId}
메뉴 수정. 삭제된 메뉴는 수정 불가.

**Request Body** — POST와 동일 형태

**Response 200** — 수정된 메뉴

**Error 400** — 삭제된 메뉴
```json
{ "status": 400, "code": "MENU_DELETED", "message": "삭제된 메뉴입니다" }
```

---

### DELETE /api/menus/{menuId}
메뉴 소프트 삭제 (deleted=true). 기존 주문 내역에는 영향 없음.

**Response 204** — 빈 응답

---

### PATCH /api/menus/{menuId}/sold-out
메뉴 품절 상태 변경. 삭제된 메뉴는 품절 설정 불가.

**Request Body**
```json
{
  "soldOut": true
}
```

**Response 200** — 변경된 메뉴

---

### PATCH /api/menus/order
메뉴 노출 순서 일괄 변경.

**Request Body**
```json
{
  "items": [
    { "id": 1, "displayOrder": 2 },
    { "id": 2, "displayOrder": 1 }
  ]
}
```

**Response 200** — 변경된 메뉴 목록

---

## 4. 주문 관리

### GET /api/admin/tables/{tableId}/orders
테이블의 현재 세션 주문 목록 조회. 삭제된 주문 제외, 최신순 정렬.

**Response 200**
```json
{
  "tableId": 1,
  "tableNumber": 1,
  "totalAmount": 23000,
  "orders": [
    {
      "id": 101,
      "tableId": 1,
      "totalAmount": 23000,
      "status": "PENDING",
      "createdAt": "2026-03-05T12:30:00",
      "items": [
        {
          "id": 1,
          "menuId": 1,
          "menuName": "불고기 정식",
          "quantity": 1,
          "unitPrice": 12000,
          "subtotal": 12000
        },
        {
          "id": 2,
          "menuId": 4,
          "menuName": "비빔밥",
          "quantity": 1,
          "unitPrice": 10000,
          "subtotal": 10000
        }
      ]
    }
  ]
}
```

세션이 없는 테이블은 `totalAmount: 0`, `orders: []` 반환.

---

### PATCH /api/admin/orders/{orderId}/status
주문 상태 변경. 허용 전이: PENDING→PREPARING, PREPARING→COMPLETED. 역방향 불가.

**Request Body**
```json
{
  "status": "PREPARING"
}
```

**Response 200** — 변경된 주문 (GET 응답의 orders 배열 내 단일 항목과 동일)

**Error 400**
```json
{ "status": 400, "code": "INVALID_STATUS_TRANSITION", "message": "허용되지 않는 상태 전이입니다" }
```

---

### DELETE /api/admin/orders/{orderId}
주문 소프트 삭제. 대시보드와 총 주문액에서 제외됨.

**Response 204** — 빈 응답

---

### GET /api/admin/tables/{tableId}/history
테이블의 과거 주문 내역 조회 (이용 완료 처리된 주문). 날짜 필터 선택 가능.

**Query Parameters**
- `dateFrom` (선택): `yyyy-MM-dd` — 시작 날짜
- `dateTo` (선택): `yyyy-MM-dd` — 종료 날짜

**예시**: `GET /api/admin/tables/1/history?dateFrom=2026-03-01&dateTo=2026-03-05`

**Response 200**
```json
[
  {
    "id": 1,
    "originalOrderId": 101,
    "totalAmount": 23000,
    "status": "COMPLETED",
    "orderedAt": "2026-03-04T12:30:00",
    "completedAt": "2026-03-04T14:00:00",
    "items": [
      {
        "menuId": 1,
        "menuName": "불고기 정식",
        "quantity": 1,
        "unitPrice": 12000,
        "subtotal": 12000
      }
    ]
  }
]
```

---

## 5. 테이블 관리

### GET /api/stores/{storeId}/tables
매장의 테이블 목록 조회. 각 테이블의 현재 세션 주문 요약 포함.

**Response 200**
```json
[
  {
    "id": 1,
    "tableNumber": 1,
    "status": "OCCUPIED",
    "currentSessionId": "550e8400-e29b-41d4-a716-446655440000",
    "totalOrderAmount": 23000,
    "orderCount": 2
  },
  {
    "id": 2,
    "tableNumber": 2,
    "status": "AVAILABLE",
    "currentSessionId": null,
    "totalOrderAmount": 0,
    "orderCount": 0
  }
]
```

---

### POST /api/tables
테이블 등록. (storeId, tableNumber) 조합은 유니크.

**Request Body**
```json
{
  "tableNumber": 6,
  "pin": "1234"
}
```

**Response 201**
```json
{
  "id": 6,
  "tableNumber": 6,
  "status": "AVAILABLE",
  "currentSessionId": null,
  "totalOrderAmount": 0,
  "orderCount": 0
}
```

**Error 409**
```json
{ "status": 409, "code": "TABLE_NUMBER_DUPLICATE", "message": "이미 존재하는 테이블 번호입니다" }
```

---

### PUT /api/tables/{tableId}
테이블 정보 수정. 변경할 필드만 전송 가능.

**Request Body**
```json
{
  "tableNumber": 7,
  "pin": "5678"
}
```

**Response 200** — 수정된 테이블

---

### DELETE /api/tables/{tableId}
테이블 삭제. 활성 세션이 있으면 삭제 불가.

**Response 204** — 빈 응답

**Error 409**
```json
{ "status": 409, "code": "TABLE_IN_USE", "message": "활성 세션이 있는 테이블입니다" }
```

---

### POST /api/tables/{tableId}/complete
테이블 이용 완료 처리. 현재 세션의 주문을 과거 이력으로 이동하고 테이블을 리셋.

처리 내용:
1. 현재 세션 주문 → OrderHistory로 복사
2. 테이블 세션 초기화 (status=AVAILABLE, sessionId=null)
3. 해당 테이블의 태블릿 토큰 무효화

**Response 200** — 빈 응답

**Error 400**
```json
{ "status": 400, "code": "NO_ACTIVE_SESSION", "message": "활성 세션이 없습니다" }
```

---

## 6. SSE (실시간 이벤트)

### GET /api/sse/admin/{storeId}
관리자 SSE 구독. 실시간 주문 알림 수신.

**Content-Type**: `text/event-stream`

**이벤트 종류**

| 이벤트명 | 설명 | 발생 시점 |
|----------|------|-----------|
| CONNECTED | 연결 확인 | SSE 연결 직후 |
| ORDER_CREATED | 신규 주문 | 고객이 주문 생성 시 |

**ORDER_CREATED 이벤트 데이터**
```json
{
  "id": 102,
  "tableId": 3,
  "totalAmount": 15000,
  "status": "PENDING",
  "createdAt": "2026-03-05T12:35:00",
  "items": [
    { "id": 5, "menuId": 2, "menuName": "김치찌개", "quantity": 1, "unitPrice": 9000, "subtotal": 9000 },
    { "id": 6, "menuId": 5, "menuName": "감자튀김", "quantity": 1, "unitPrice": 5000, "subtotal": 5000 }
  ]
}
```

**프론트엔드 연결 예시**
```javascript
const eventSource = new EventSource('http://localhost:8080/api/sse/admin/1');

eventSource.addEventListener('CONNECTED', (e) => {
  console.log('SSE 연결됨');
});

eventSource.addEventListener('ORDER_CREATED', (e) => {
  const order = JSON.parse(e.data);
  console.log('새 주문:', order);
});
```

---

## 공통 에러 응답 형식

모든 에러는 아래 형태로 반환됩니다:

```json
{
  "status": 400,
  "code": "ERROR_CODE",
  "message": "사람이 읽을 수 있는 메시지",
  "timestamp": "2026-03-05T10:00:00",
  "details": {}
}
```

### 주요 에러 코드

| code | HTTP | 설명 |
|------|------|------|
| INVALID_CREDENTIALS | 401 | 로그인 실패 |
| ACCOUNT_LOCKED | 423 | 계정 잠금 |
| INVALID_TOKEN | 401 | 유효하지 않은 토큰 |
| TOKEN_EXPIRED | 401 | 만료된 토큰 |
| FORBIDDEN | 403 | 접근 권한 없음 |
| TABLE_NOT_FOUND | 404 | 테이블 없음 |
| TABLE_NUMBER_DUPLICATE | 409 | 테이블 번호 중복 |
| TABLE_IN_USE | 409 | 활성 세션 있는 테이블 |
| NO_ACTIVE_SESSION | 400 | 활성 세션 없음 |
| ORDER_NOT_FOUND | 404 | 주문 없음 |
| INVALID_STATUS_TRANSITION | 400 | 잘못된 상태 전이 |
| MENU_NOT_FOUND | 404 | 메뉴 없음 |
| MENU_DELETED | 400 | 삭제된 메뉴 |
| CATEGORY_NOT_FOUND | 404 | 카테고리 없음 |
| CATEGORY_HAS_MENUS | 409 | 메뉴 있는 카테고리 |
| INVALID_INPUT | 400 | 입력값 검증 실패 |

---

## 시드 데이터 (개발용)

| 항목 | 값 |
|------|-----|
| 매장 ID | 1 |
| 매장명 | 테스트 매장 |
| master_pin | 000000 |
| 관리자 username | admin |
| 관리자 password | admin1234 |
| 테이블 | 1~5번 (PIN: 1234) |
| 카테고리 | 메인, 사이드, 음료, 디저트 |
| 메뉴 | 불고기 정식, 김치찌개, 된장찌개, 비빔밥, 감자튀김, 샐러드, 콜라, 사이다, 아이스크림 |
