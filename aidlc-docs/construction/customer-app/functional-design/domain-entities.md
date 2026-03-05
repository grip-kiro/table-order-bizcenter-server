# Customer App — 도메인 엔티티 설계 (클라이언트 타입)

> Unit 3: Customer App에서 사용하는 TypeScript 타입/인터페이스 정의.
> Customer Backend API 응답과 매핑되는 클라이언트 측 데이터 모델입니다.

---

## 기존 타입 (현재 구현, 수정 필요)

현재 `table-order-client/src/types/index.ts`에 정의된 타입을 Backend API 스키마에 맞게 확장합니다.

---

## 타입 정의

### Category (카테고리)

```typescript
interface Category {
  id: number;
  name: string;
  displayOrder: number;
}
```

**변경점**: 기존에는 카테고리를 `string[]`로 관리 → `Category` 객체 배열로 변경

---

### Menu (메뉴)

```typescript
interface Menu {
  id: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
  isSoldOut: boolean;
  displayOrder: number;
  categories: { id: number; name: string }[];
}
```

**변경점**: 기존 `category: string`, `desc: string`, `img: string` → Backend 스키마에 맞게 필드명/구조 변경. `isSoldOut` 추가.

---

### CartItem (장바구니 항목)

```typescript
interface CartItem {
  menuId: number;
  name: string;
  price: number;
  imageUrl: string | null;
  qty: number;
}
```

**변경점**: Menu 전체를 저장하지 않고 주문에 필요한 최소 필드만 저장. localStorage 용량 절약.

---

### OrderItem (주문 항목)

```typescript
interface OrderItem {
  id: number;
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}
```

**변경점**: Backend 응답 스키마에 맞게 필드 추가 (menuId, subtotal).

---

### OrderStatus (주문 상태)

```typescript
type OrderStatus = "PENDING" | "PREPARING" | "COMPLETED";
```

**변경점**: 한글 → 영문 enum 값 (Backend 스키마 일치). UI 표시 시 한글 매핑.

---

### Order (주문)

```typescript
interface Order {
  id: number;
  storeId: number;
  tableId: number;
  sessionId: string;
  totalAmount: number;
  status: OrderStatus;
  items: OrderItem[];
  createdAt: string; // ISO 8601
  updatedAt: string;
}
```

**변경점**: `id: string` → `number` (AUTO_INCREMENT), 필드명 Backend 스키마 일치.

---

### TableCredential (태블릿 로그인 정보)

```typescript
interface TableCredential {
  storeId: number;
  tableNumber: number;
  pin: string;
}
```

**변경점**: `storeId: string` → `number`, `tableNo` → `tableNumber`, `password` → `pin`.

---

### TableSession (태블릿 세션)

```typescript
interface TableSession {
  storeId: number;
  tableId: number;
  tableNumber: number;
  sessionId: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number; // Access Token 만료 시간 (초)
}
```

**변경점**: 단일 `token` → `accessToken` + `refreshToken` 분리. `tableId` 추가.

---

### TokenPair (토큰 쌍 — API 응답)

```typescript
interface TokenPair {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
```

---

### ApiError (API 에러 응답)

```typescript
interface ApiError {
  status: number;
  code: string;
  message: string;
  timestamp: string;
  details?: Record<string, any>;
}
```

---

### CursorPage (커서 기반 페이지네이션)

```typescript
interface CursorPage<T> {
  items: T[];
  nextCursor: string | null;
}
```

**변경 없음**: 기존 구조 유지.

---

## 상태 표시 매핑

```typescript
const ORDER_STATUS_LABEL: Record<OrderStatus, string> = {
  PENDING: "대기중",
  PREPARING: "준비중",
  COMPLETED: "완료",
};

const ORDER_STATUS_COLOR: Record<OrderStatus, string> = {
  PENDING: "#f59e0b",
  PREPARING: "#3b82f6",
  COMPLETED: "#10b981",
};
```
