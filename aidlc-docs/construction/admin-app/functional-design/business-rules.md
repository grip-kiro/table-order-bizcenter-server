# Admin App — 비즈니스 규칙

> Unit 4: Admin App에 적용되는 UI 규칙, 클라이언트 검증, 상태 관리 규칙을 정의합니다.

---

## 1. 인증 규칙 (Auth Rules)

### BR-ADMIN-APP-AUTH-01: 로그인 폼 검증
| 필드 | 규칙 | 에러 메시지 |
|------|------|-------------|
| storeId | 필수, 양의 정수 | "매장 ID를 입력해주세요" |
| username | 필수, 비어있지 않음 | "사용자명을 입력해주세요" |
| password | 필수, 비어있지 않음 | "비밀번호를 입력해주세요" |

### BR-ADMIN-APP-AUTH-02: 세션 관리
- 세션은 sessionStorage에 저장 (탭 닫으면 소멸)
- 16시간 세션 유지 (Refresh Token 만료 기준)
- 브라우저 새로고침 시 세션 유지

### BR-ADMIN-APP-AUTH-03: 토큰 갱신
- API 401 응답 시 자동 토큰 갱신 시도
- 갱신 실패 시 로그아웃 + 로그인 화면 전환
- 갱신 중 중복 요청 방지

### BR-ADMIN-APP-AUTH-04: 로그인 실패 표시
- INVALID_CREDENTIALS: "아이디 또는 비밀번호가 올바르지 않습니다" + 남은 시도 횟수 (서버 응답에 포함 시)
- ACCOUNT_LOCKED: "계정이 잠겼습니다. N분 후 다시 시도해주세요" (남은 시간 표시)

### BR-ADMIN-APP-AUTH-05: 로그아웃
- 서버 측 토큰 무효화 API 호출
- sessionStorage 세션 삭제
- 로그인 화면 전환

---

## 2. 대시보드 규칙 (Dashboard Rules)

### BR-ADMIN-APP-DASH-01: 테이블 카드 표시
- 반응형 그리드 레이아웃
- 이용중(OCCUPIED) 테이블: 컬러 카드 (주문 정보 표시)
- 비어있음(AVAILABLE) 테이블: 회색 카드 (주문 정보 없음)
- 카드 내 최신 주문 미리보기: 최대 3개, 메뉴명 축약 (20자 초과 시 "...")

### BR-ADMIN-APP-DASH-02: 실시간 업데이트 (SSE)
- SSE 연결: 페이지 마운트 시 시작, 언마운트 시 종료
- ORDER_CREATED 이벤트 수신 시:
  - 해당 테이블 카드 주문 정보 업데이트
  - 카드 강조 애니메이션 (3초간 테두리 색상 변경 + 펄스)
- SSE 연결 끊김 시 자동 재연결 (3초 후)

### BR-ADMIN-APP-DASH-03: 테이블 필터링
- 테이블 번호 검색 입력
- 클라이언트 측 필터링 (서버 재호출 없음)
- 빈 검색어: 전체 표시

### BR-ADMIN-APP-DASH-04: 주문 상태 변경
- 상태 변경 버튼 텍스트:
  - PENDING → "준비 시작" 버튼
  - PREPARING → "완료 처리" 버튼
  - COMPLETED → 버튼 없음 (최종 상태)
- 변경 즉시 UI 반영 (낙관적 업데이트)
- 실패 시 롤백 + 에러 메시지

### BR-ADMIN-APP-DASH-05: 주문 삭제
- 확인 팝업 필수 ("이 주문을 삭제하시겠습니까?")
- 삭제 후 테이블 총 주문액 재계산 (클라이언트 측)
- 삭제 성공/실패 피드백

### BR-ADMIN-APP-DASH-06: 이용 완료
- 확인 팝업 필수 (경고 문구 포함)
- 성공 후: 테이블 카드 리셋, 모달 닫기
- 비어있는 테이블(세션 없음)에서는 이용 완료 버튼 비활성화

### BR-ADMIN-APP-DASH-07: 과거 내역 모달
- 날짜 필터: dateFrom, dateTo (기본값: 오늘)
- 시간 역순 정렬
- 각 이력: 주문 번호, 시각, 메뉴 목록, 총 금액, 이용 완료 시각

---

## 3. 테이블 관리 규칙 (Table Management Rules)

### BR-ADMIN-APP-TABLE-01: 테이블 등록 폼 검증
| 필드 | 규칙 | 에러 메시지 |
|------|------|-------------|
| tableNumber | 필수, 양의 정수 | "테이블 번호를 입력해주세요" |
| pin | 필수, 숫자 4~6자리 | "PIN은 4~6자리 숫자입니다" |

### BR-ADMIN-APP-TABLE-02: 테이블 삭제 제약
- 이용중(OCCUPIED) 테이블: 삭제 버튼 비활성화 또는 에러 표시
- 확인 팝업 필수

### BR-ADMIN-APP-TABLE-03: 중복 번호 처리
- TABLE_NUMBER_DUPLICATE 에러 시: "이미 존재하는 테이블 번호입니다"

---

## 4. 메뉴 관리 규칙 (Menu Management Rules)

### BR-ADMIN-APP-MENU-01: 메뉴 등록/수정 폼 검증
| 필드 | 규칙 | 에러 메시지 |
|------|------|-------------|
| name | 필수, 1~100자 | "메뉴명을 입력해주세요" |
| price | 필수, 0~1,000,000 | "가격은 0~1,000,000 범위입니다" |
| description | 선택 | — |
| imageUrl | 선택, 최대 500자 | "URL이 너무 깁니다" |
| categoryIds | 필수, 1개 이상 | "카테고리를 1개 이상 선택해주세요" |

### BR-ADMIN-APP-MENU-02: 메뉴 목록 표시
- 삭제된 메뉴(isDeleted=true): 회색 행 + "삭제됨" 뱃지
- 품절 메뉴(isSoldOut=true): "품절" 뱃지
- 정렬: displayOrder ASC

### BR-ADMIN-APP-MENU-03: 품절 토글
- 토글 스위치로 즉시 변경
- 삭제된 메뉴는 품절 토글 비활성화

### BR-ADMIN-APP-MENU-04: 순서 변경
- 위/아래 화살표 버튼
- 첫 번째 항목: 위 버튼 비활성화
- 마지막 항목: 아래 버튼 비활성화
- 변경 후 서버에 일괄 저장

### BR-ADMIN-APP-MENU-05: 메뉴 삭제
- 확인 팝업 필수
- 소프트 삭제 (목록에서 제거되지 않고 상태 변경)

---

## 5. 카테고리 관리 규칙 (Category Management Rules)

### BR-ADMIN-APP-CAT-01: 카테고리 등록/수정 폼 검증
| 필드 | 규칙 | 에러 메시지 |
|------|------|-------------|
| name | 필수, 1~50자 | "카테고리명을 입력해주세요" |

### BR-ADMIN-APP-CAT-02: 카테고리 삭제 제약
- CATEGORY_HAS_MENUS 에러 시: "메뉴가 있는 카테고리는 삭제할 수 없습니다"
- 확인 팝업 필수

### BR-ADMIN-APP-CAT-03: 순서 변경
- 위/아래 화살표 버튼 (메뉴와 동일 방식)

---

## 6. UI/UX 규칙

### BR-ADMIN-APP-UX-01: 네비게이션
- 좌측 사이드바 네비게이션 (대시보드, 테이블, 메뉴, 카테고리, 로그아웃)
- 현재 페이지 하이라이트
- 매장명/관리자명 표시

### BR-ADMIN-APP-UX-02: 확인 팝업
- 삭제/이용 완료 등 파괴적 액션에 확인 팝업 필수
- "확인"/"취소" 버튼
- 경고 문구 포함

### BR-ADMIN-APP-UX-03: 로딩/에러 상태
- API 호출 중 로딩 인디케이터
- 버튼 중복 클릭 방지
- 에러 시 토스트 또는 인라인 메시지

### BR-ADMIN-APP-UX-04: 금액 표시
- 원화 포맷: "9,000원"
- 총 주문액: 굵은 글씨

---

## 7. API 연동 규칙

### BR-ADMIN-APP-API-01: 엔드포인트 매핑

| 기능 | 메서드 | 엔드포인트 |
|------|--------|------------|
| 로그인 | POST | `/api/auth/admin/login` |
| 토큰 갱신 | POST | `/api/auth/refresh` |
| 로그아웃 | POST | `/api/auth/logout` |
| 테이블 목록 | GET | `/api/stores/{storeId}/tables` |
| 테이블 등록 | POST | `/api/tables` |
| 테이블 수정 | PUT | `/api/tables/{tableId}` |
| 테이블 삭제 | DELETE | `/api/tables/{tableId}` |
| 이용 완료 | POST | `/api/tables/{tableId}/complete` |
| 테이블 주문 조회 | GET | `/api/admin/tables/{tableId}/orders` |
| 주문 상태 변경 | PATCH | `/api/orders/{orderId}/status` |
| 주문 삭제 | DELETE | `/api/orders/{orderId}` |
| 과거 내역 | GET | `/api/admin/tables/{tableId}/history` |
| 메뉴 목록 (관리자) | GET | `/api/stores/{storeId}/menus` |
| 메뉴 등록 | POST | `/api/menus` |
| 메뉴 수정 | PUT | `/api/menus/{menuId}` |
| 메뉴 삭제 | DELETE | `/api/menus/{menuId}` |
| 품절 설정 | PATCH | `/api/menus/{menuId}/sold-out` |
| 메뉴 순서 변경 | PATCH | `/api/menus/order` |
| 카테고리 목록 | GET | `/api/stores/{storeId}/categories` |
| 카테고리 등록 | POST | `/api/categories` |
| 카테고리 수정 | PUT | `/api/categories/{categoryId}` |
| 카테고리 삭제 | DELETE | `/api/categories/{categoryId}` |
| 카테고리 순서 변경 | PATCH | `/api/categories/order` |
| SSE 구독 | GET | `/api/sse/admin/{storeId}` |

### BR-ADMIN-APP-API-02: 인증 헤더
- 모든 인증 필요 API에 `Authorization: Bearer {accessToken}` 헤더 추가
- 로그인 API는 인증 헤더 불필요
