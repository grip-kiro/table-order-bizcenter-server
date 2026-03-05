# Admin App — 비즈니스 로직 모델

> Unit 4: Admin App의 페이지/컴포넌트 흐름과 상태 관리 로직을 기술합니다.

---

## 1. 앱 구조 및 라우팅

### 1.1 페이지 구성

| 경로 | 컴포넌트 | 설명 | 인증 필요 |
|------|----------|------|-----------|
| — | LoginPage | 관리자 로그인 | No |
| `/` | DashboardPage | 실시간 주문 대시보드 (기본 화면) | Yes |
| `/tables` | TableManagementPage | 테이블 등록/관리 | Yes |
| `/menus` | MenuManagementPage | 메뉴 CRUD | Yes |
| `/categories` | CategoryManagementPage | 카테고리 CRUD | Yes |

### 1.2 인증 분기

```
App 로드
  → 세션 존재? (sessionStorage/쿠키)
    → Yes → 대시보드 표시
    → No → 로그인 화면
```

---

## 2. 인증 모듈 (useAdminSession)

### 2.1 관리자 로그인 (US-18, US-19)

**입력**: storeId, username, password

**로직 흐름**:
1. `POST /api/auth/admin/login` 호출
2. 성공 시:
   - TokenPair에서 accessToken, refreshToken 추출
   - AdminSession 구성 → sessionStorage에 저장
   - 대시보드로 이동
3. 실패 시:
   - INVALID_CREDENTIALS → "아이디 또는 비밀번호가 올바르지 않습니다" + 남은 시도 횟수
   - ACCOUNT_LOCKED → "계정이 잠겼습니다. N분 후 다시 시도해주세요"

### 2.2 토큰 갱신

**트리거**: API 401 응답

**로직 흐름**:
1. `POST /api/auth/refresh` 호출
2. 성공 → 새 accessToken으로 원래 요청 재시도
3. 실패 → 로그아웃 처리, 로그인 화면 전환

### 2.3 로그아웃

**로직**:
1. `POST /api/auth/logout` 호출 (서버 측 토큰 무효화)
2. sessionStorage 세션 삭제
3. 로그인 화면 전환

---

## 3. 대시보드 모듈 (DashboardPage) — US-20, US-21, US-22, US-23, US-25, US-26

### 3.1 대시보드 초기 로드

**트리거**: DashboardPage 마운트 시

**로직 흐름**:
1. `GET /api/stores/{storeId}/tables` 호출 → 테이블 목록 + 주문 요약
2. 테이블별 카드 그리드 렌더링
3. SSE 연결 시작 (`GET /api/sse/admin/{storeId}`)

### 3.2 테이블 카드 표시 (US-20)

**각 카드 내용**:
- 테이블 번호
- 상태 뱃지 (비어있음/이용중)
- 총 주문액 (이용중인 경우)
- 최신 주문 미리보기 (최대 3개, 메뉴명 축약)

**카드 클릭 → 테이블 상세 모달 (TableDetailModal)**

### 3.3 테이블 상세 모달

**내용**:
- 테이블 번호, 상태
- 현재 세션 전체 주문 목록 (`GET /api/admin/tables/{tableId}/orders`)
- 각 주문: 주문 번호, 시각, 메뉴/수량, 금액, 상태 뱃지
- 주문별 액션 버튼:
  - 상태 변경 버튼 (US-22)
  - 삭제 버튼 (US-25)
- 테이블 액션:
  - 이용 완료 버튼 (US-26)
  - 과거 내역 버튼 (US-27)

### 3.4 실시간 주문 업데이트 (US-21)

**SSE 이벤트 처리**:
1. `ORDER_CREATED` 이벤트 수신
2. 해당 테이블 카드의 주문 정보 업데이트
3. 시각적 강조: 카드 테두리 색상 변경 + 펄스 애니메이션 (3초간)
4. 테이블 상세 모달이 열려있으면 주문 목록에도 추가

### 3.5 주문 상태 변경 (US-22)

**트리거**: 테이블 상세 모달에서 상태 변경 버튼 클릭

**로직 흐름**:
1. 현재 상태에 따라 다음 상태 결정:
   - PENDING → PREPARING
   - PREPARING → COMPLETED
2. `PATCH /api/orders/{orderId}/status` 호출
3. 성공 → 주문 상태 UI 업데이트
4. 실패 → 에러 메시지 표시

### 3.6 주문 삭제 (US-25)

**트리거**: 테이블 상세 모달에서 삭제 버튼 클릭

**로직 흐름**:
1. 확인 팝업 표시 ("이 주문을 삭제하시겠습니까?")
2. 확인 → `DELETE /api/orders/{orderId}` 호출
3. 성공 → 주문 목록에서 제거, 총 주문액 재계산
4. 실패 → 에러 메시지 표시

### 3.7 테이블 이용 완료 (US-26)

**트리거**: 테이블 상세 모달에서 이용 완료 버튼 클릭

**로직 흐름**:
1. 확인 팝업 표시 ("테이블 이용을 완료하시겠습니까? 주문 내역이 과거 이력으로 이동됩니다.")
2. 확인 → `POST /api/tables/{tableId}/complete` 호출
3. 성공 → 테이블 카드 리셋 (주문 0, 총액 0, 상태 AVAILABLE), 모달 닫기
4. 실패 → 에러 메시지 표시

### 3.8 과거 주문 내역 조회 (US-27)

**트리거**: 테이블 상세 모달에서 과거 내역 버튼 클릭

**로직 흐름**:
1. OrderHistoryModal 표시
2. `GET /api/admin/tables/{tableId}/history` 호출 (날짜 필터 옵션)
3. 시간 역순으로 과거 주문 목록 표시
4. 각 주문: 주문 번호, 시각, 메뉴 목록, 총 금액, 이용 완료 시각
5. 날짜 필터: dateFrom, dateTo 입력 → 재조회

### 3.9 테이블 필터링 (US-23)

**로직**:
- 검색/필터 입력으로 특정 테이블 번호 필터링
- 클라이언트 측 필터링 (전체 테이블 목록에서)

---

## 4. 테이블 관리 모듈 (TableManagementPage) — US-24

### 4.1 테이블 목록

**트리거**: 페이지 마운트 시

**로직 흐름**:
1. `GET /api/stores/{storeId}/tables` 호출
2. 테이블 형태로 목록 표시 (번호, 상태, 액션)

### 4.2 테이블 등록

**트리거**: 등록 버튼 클릭

**로직 흐름**:
1. 등록 폼 표시 (테이블 번호, PIN)
2. 클라이언트 검증 (번호: 양의 정수, PIN: 4~6자리 숫자)
3. `POST /api/tables` 호출
4. 성공 → 목록에 추가
5. 실패 (TABLE_NUMBER_DUPLICATE) → "이미 존재하는 테이블 번호입니다"

### 4.3 테이블 수정

**트리거**: 수정 버튼 클릭

**로직 흐름**:
1. 기존 정보가 채워진 수정 폼 표시
2. `PUT /api/tables/{tableId}` 호출
3. 성공 → 목록 업데이트

### 4.4 테이블 삭제

**트리거**: 삭제 버튼 클릭

**로직 흐름**:
1. 확인 팝업
2. `DELETE /api/tables/{tableId}` 호출
3. 성공 → 목록에서 제거
4. 실패 (TABLE_IN_USE) → "이용 중인 테이블은 삭제할 수 없습니다"

---

## 5. 메뉴 관리 모듈 (MenuManagementPage) — US-29~33

### 5.1 메뉴 목록

**로직 흐름**:
1. `GET /api/stores/{storeId}/menus` 호출 (관리자용 — is_deleted 포함)
2. 테이블 형태로 표시 (메뉴명, 가격, 카테고리, 품절, 삭제 상태, 순서, 액션)
3. 삭제된 메뉴는 회색 처리 + "삭제됨" 뱃지

### 5.2 메뉴 등록 (US-29)

**로직 흐름**:
1. 등록 폼: 메뉴명, 가격, 설명, 이미지 URL, 카테고리 (복수 선택 체크박스)
2. 클라이언트 검증:
   - 메뉴명: 필수, 1~100자
   - 가격: 필수, 0~1,000,000
   - 카테고리: 1개 이상 선택
3. `POST /api/menus` 호출
4. 성공 → 목록에 추가

### 5.3 메뉴 수정 (US-30)

**로직 흐름**:
1. 기존 정보가 채워진 수정 폼
2. `PUT /api/menus/{menuId}` 호출
3. 성공 → 목록 업데이트

### 5.4 메뉴 삭제 (US-31)

**로직 흐름**:
1. 확인 팝업 ("메뉴를 삭제하시겠습니까? 고객 화면에서 숨겨집니다.")
2. `DELETE /api/menus/{menuId}` 호출
3. 성공 → 목록에서 isDeleted=true로 표시 변경

### 5.5 품절 토글 (US-32)

**로직 흐름**:
1. 토글 스위치 클릭
2. `PATCH /api/menus/{menuId}/sold-out` 호출 (isSoldOut: true/false)
3. 성공 → UI 즉시 반영

### 5.6 노출 순서 변경 (US-33)

**로직 흐름**:
1. 위/아래 화살표 버튼으로 순서 변경
2. 변경된 순서 목록 구성
3. `PATCH /api/menus/order` 호출
4. 성공 → 목록 재정렬

---

## 6. 카테고리 관리 모듈 (CategoryManagementPage) — US-28

### 6.1 카테고리 목록

**로직**: `GET /api/stores/{storeId}/categories` → 테이블 형태 표시

### 6.2 카테고리 등록

**로직**: 이름 입력 → `POST /api/categories` → 목록 추가

### 6.3 카테고리 수정

**로직**: 이름 수정 → `PUT /api/categories/{categoryId}` → 목록 업데이트

### 6.4 카테고리 삭제

**로직**: 확인 팝업 → `DELETE /api/categories/{categoryId}`
- 실패 (CATEGORY_HAS_MENUS) → "메뉴가 있는 카테고리는 삭제할 수 없습니다"

### 6.5 카테고리 순서 변경

**로직**: 위/아래 버튼 → `PATCH /api/categories/order` → 목록 재정렬

---

## 7. 컴포넌트 트리

```
App
├── LoginPage (세션 없을 때)
│   └── 로그인 폼 (storeId, username, password)
│
└── AuthenticatedApp (세션 있을 때)
    ├── Sidebar 네비게이션
    │   ├── 대시보드
    │   ├── 테이블 관리
    │   ├── 메뉴 관리
    │   ├── 카테고리 관리
    │   └── 로그아웃
    │
    └── Routes
        ├── DashboardPage (/)
        │   ├── 테이블 필터 바
        │   ├── 테이블 카드 그리드
        │   │   ├── 테이블 번호, 상태 뱃지
        │   │   ├── 총 주문액
        │   │   ├── 최신 주문 미리보기
        │   │   └── 신규 주문 강조 애니메이션
        │   ├── TableDetailModal
        │   │   ├── 주문 목록 (상태 변경/삭제 버튼)
        │   │   ├── 이용 완료 버튼
        │   │   └── 과거 내역 버튼
        │   └── OrderHistoryModal
        │       ├── 날짜 필터
        │       └── 과거 주문 목록
        │
        ├── TableManagementPage (/tables)
        │   ├── 테이블 목록 테이블
        │   └── 등록/수정 폼
        │
        ├── MenuManagementPage (/menus)
        │   ├── 메뉴 목록 테이블 (품절 토글, 순서 버튼)
        │   └── 등록/수정 폼
        │
        └── CategoryManagementPage (/categories)
            ├── 카테고리 목록 테이블 (순서 버튼)
            └── 등록/수정 폼
```
