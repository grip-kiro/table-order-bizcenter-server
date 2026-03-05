# Admin App — 도메인 엔티티 설계 (클라이언트 타입)

> Unit 4: Admin App에서 사용하는 TypeScript 타입/인터페이스 정의.
> Admin Backend API 응답과 매핑되는 클라이언트 측 데이터 모델입니다.

---

## 인증 관련

### AdminCredential (관리자 로그인 요청)

```typescript
interface AdminCredential {
  storeId: number;
  username: string;
  password: string;
}
```

### AdminSession (관리자 세션)

```typescript
interface AdminSession {
  storeId: number;
  adminId: number;
  username: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
```

### TokenPair (토큰 쌍 — API 응답)

```typescript
interface TokenPair {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
```

---

## 테이블 관련

### RestaurantTable (테이블)

```typescript
interface RestaurantTable {
  id: number;
  tableNumber: number;
  status: "AVAILABLE" | "OCCUPIED";
  currentSessionId: string | null;
  totalAmount: number;      // 현재 세션 총 주문액
  orderCount: number;       // 현재 세션 주문 수
  createdAt: string;
}
```

### CreateTableRequest / UpdateTableRequest

```typescript
interface CreateTableRequest {
  tableNumber: number;
  pin: string;
}

interface UpdateTableRequest {
  tableNumber: number;
  pin: string;
}
```

---

## 주문 관련

### OrderStatus

```typescript
type OrderStatus = "PENDING" | "PREPARING" | "COMPLETED";
```

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
  createdAt: string;
  updatedAt: string;
}
```

### TableOrders (테이블별 주문 조회 응답)

```typescript
interface TableOrders {
  tableId: number;
  tableNumber: number;
  totalAmount: number;
  orders: Order[];
}
```

---

## 주문 이력 관련

### OrderHistory (과거 주문 이력)

```typescript
interface OrderHistory {
  id: number;
  originalOrderId: number;
  tableId: number;
  sessionId: string;
  totalAmount: number;
  status: OrderStatus;
  orderedAt: string;
  completedAt: string;
  items: OrderHistoryItem[];
}
```

### OrderHistoryItem

```typescript
interface OrderHistoryItem {
  id: number;
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}
```

---

## 카테고리 관련

### Category

```typescript
interface Category {
  id: number;
  name: string;
  displayOrder: number;
}
```

### CreateCategoryRequest / UpdateCategoryRequest

```typescript
interface CreateCategoryRequest {
  name: string;
}

interface UpdateCategoryRequest {
  name: string;
}
```

---

## 메뉴 관련

### Menu (관리자용 — is_deleted 포함)

```typescript
interface Menu {
  id: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
  isSoldOut: boolean;
  isDeleted: boolean;
  displayOrder: number;
  categories: { id: number; name: string }[];
}
```

### CreateMenuRequest

```typescript
interface CreateMenuRequest {
  name: string;
  price: number;
  description?: string;
  imageUrl?: string;
  categoryIds: number[];
}
```

### UpdateMenuRequest

```typescript
interface UpdateMenuRequest {
  name: string;
  price: number;
  description?: string;
  imageUrl?: string;
  categoryIds: number[];
}
```

---

## 공통

### ApiError

```typescript
interface ApiError {
  status: number;
  code: string;
  message: string;
  timestamp: string;
  details?: Record<string, any>;
}
```

### SSE 이벤트

```typescript
interface OrderCreatedEvent {
  orderId: number;
  tableId: number;
  tableNumber: number;
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;
}
```

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

const TABLE_STATUS_LABEL: Record<string, string> = {
  AVAILABLE: "비어있음",
  OCCUPIED: "이용중",
};
```
