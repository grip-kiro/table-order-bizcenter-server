# 테이블오더 서비스 — 컴포넌트 메서드 정의

> 상세 비즈니스 로직은 Functional Design 단계에서 정의합니다.
> 여기서는 메서드 시그니처와 고수준 목적만 정의합니다.

---

## Backend API — Controller Layer

### AuthController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| adminLogin | POST | /api/auth/admin/login | 관리자 로그인 (매장ID+사용자명+비밀번호) |
| tableLogin | POST | /api/auth/table/login | 태블릿 로그인 (매장ID+테이블번호+PIN) |
| refreshToken | POST | /api/auth/refresh | Access Token 갱신 (Refresh Token 사용) |
| logout | POST | /api/auth/logout | 로그아웃 (토큰 무효화) |

### MenuController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| getMenusByStore | GET | /api/stores/{storeId}/menus | 매장 메뉴 전체 조회 (카테고리 포함) |
| getMenuDetail | GET | /api/menus/{menuId} | 메뉴 상세 조회 |
| createMenu | POST | /api/menus | 메뉴 등록 (관리자) |
| updateMenu | PUT | /api/menus/{menuId} | 메뉴 수정 (관리자) |
| deleteMenu | DELETE | /api/menus/{menuId} | 메뉴 소프트 삭제 (관리자) |
| updateMenuSoldOut | PATCH | /api/menus/{menuId}/sold-out | 품절 상태 변경 (관리자) |
| updateMenuOrder | PATCH | /api/menus/order | 메뉴 노출 순서 변경 (관리자) |

### CategoryController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| getCategories | GET | /api/stores/{storeId}/categories | 매장 카테고리 조회 |
| createCategory | POST | /api/categories | 카테고리 등록 (관리자) |
| updateCategory | PUT | /api/categories/{categoryId} | 카테고리 수정 (관리자) |
| deleteCategory | DELETE | /api/categories/{categoryId} | 카테고리 삭제 (관리자) |
| updateCategoryOrder | PATCH | /api/categories/order | 카테고리 순서 변경 (관리자) |

### OrderController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| createOrder | POST | /api/orders | 주문 생성 (고객) |
| getOrdersBySession | GET | /api/tables/{tableId}/orders | 현재 세션 주문 조회 (고객) |
| getOrdersByTable | GET | /api/admin/tables/{tableId}/orders | 테이블 주문 조회 (관리자) |
| updateOrderStatus | PATCH | /api/orders/{orderId}/status | 주문 상태 변경 (관리자) |
| deleteOrder | DELETE | /api/orders/{orderId} | 주문 삭제 (관리자) |
| getOrderHistory | GET | /api/admin/tables/{tableId}/history | 과거 주문 내역 조회 (관리자) |

### TableController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| getTables | GET | /api/stores/{storeId}/tables | 매장 테이블 목록 조회 (관리자) |
| createTable | POST | /api/tables | 테이블 등록 (관리자) |
| updateTable | PUT | /api/tables/{tableId} | 테이블 정보 수정 (관리자) |
| deleteTable | DELETE | /api/tables/{tableId} | 테이블 삭제 (관리자) |
| completeTableSession | POST | /api/tables/{tableId}/complete | 테이블 이용 완료 (관리자) |

### SseController
| 메서드 | HTTP | 경로 | 목적 |
|--------|------|------|------|
| subscribeAdmin | GET | /api/sse/admin/{storeId} | 관리자 SSE 구독 (주문 업데이트) |
| subscribeTable | GET | /api/sse/table/{tableId} | 태블릿 SSE 구독 (세션 종료 감지) |

---

## Backend API — Service Layer

### AuthService
- authenticateAdmin(storeId, username, password) → TokenPair
- authenticateTable(storeId, tableNumber, pin) → TokenPair
- refreshAccessToken(refreshToken) → TokenPair
- invalidateSession(tableId) → void
- checkLoginAttemptLimit(accountId) → boolean

### MenuService
- getMenusByStore(storeId) → List<MenuDTO> (캐싱 적용)
- getMenuDetail(menuId) → MenuDTO
- createMenu(CreateMenuRequest) → MenuDTO
- updateMenu(menuId, UpdateMenuRequest) → MenuDTO
- softDeleteMenu(menuId) → void
- updateSoldOutStatus(menuId, isSoldOut) → void
- updateMenuDisplayOrder(List<MenuOrderRequest>) → void
- evictMenuCache(storeId) → void

### CategoryService
- getCategoriesByStore(storeId) → List<CategoryDTO>
- createCategory(CreateCategoryRequest) → CategoryDTO
- updateCategory(categoryId, UpdateCategoryRequest) → CategoryDTO
- deleteCategory(categoryId) → void
- updateCategoryDisplayOrder(List<CategoryOrderRequest>) → void

### OrderService
- createOrder(CreateOrderRequest) → OrderDTO
- getOrdersByTableSession(tableId, sessionId) → List<OrderDTO>
- getOrdersByTable(tableId) → List<OrderDTO>
- updateOrderStatus(orderId, OrderStatus) → OrderDTO
- deleteOrder(orderId) → void
- getOrderHistory(tableId, dateFrom, dateTo) → List<OrderHistoryDTO>

### TableService
- getTablesByStore(storeId) → List<TableDTO>
- createTable(CreateTableRequest) → TableDTO
- updateTable(tableId, UpdateTableRequest) → TableDTO
- deleteTable(tableId) → void
- completeTableSession(tableId) → void

### SseService
- addAdminEmitter(storeId, SseEmitter) → void
- addTableEmitter(tableId, SseEmitter) → void
- notifyOrderUpdate(storeId, OrderEvent) → void
- notifySessionEnd(tableId) → void
- removeEmitter(emitterId) → void
