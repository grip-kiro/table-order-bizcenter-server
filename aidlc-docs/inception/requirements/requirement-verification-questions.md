# 요구사항 명확화 질문

제공해주신 요구사항 정의서를 분석했습니다. 아래 질문들에 답변해 주시면 더 정확한 설계와 구현이 가능합니다.
각 질문의 [Answer]: 태그 뒤에 선택지 문자를 입력해 주세요.

---

## Question 1
프론트엔드 기술 스택으로 어떤 것을 사용하시겠습니까?

A) React (JavaScript/TypeScript)
B) Vue.js (JavaScript/TypeScript)
C) Next.js (React 기반 풀스택 프레임워크)
D) Angular (TypeScript)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 2
백엔드 기술 스택으로 어떤 것을 사용하시겠습니까?

A) Node.js + Express (JavaScript/TypeScript)
B) Node.js + NestJS (TypeScript)
C) Python + FastAPI
D) Java + Spring Boot
X) Other (please describe after [Answer]: tag below)

[Answer]: D

## Question 3
데이터베이스로 어떤 것을 사용하시겠습니까?

A) PostgreSQL (관계형 데이터베이스)
B) MySQL (관계형 데이터베이스)
C) MongoDB (NoSQL 문서형 데이터베이스)
D) SQLite (경량 관계형 데이터베이스, 개발/MVP에 적합)
X) Other (please describe after [Answer]: tag below)

[Answer]: X

## Question 4
배포 환경은 어떻게 계획하고 계십니까?

A) AWS (EC2, ECS, Lambda 등)
B) 로컬 서버 / 온프레미스
C) Docker 컨테이너 기반 (배포 환경 미정)
D) 아직 결정하지 않음 (MVP 단계에서는 로컬 개발 환경만 사용)
X) Other (please describe after [Answer]: tag below)

[Answer]: D

## Question 5
동시 접속 매장 수와 테이블 수의 예상 규모는 어느 정도입니까?

A) 소규모 (1~5개 매장, 매장당 10~20개 테이블)
B) 중규모 (5~50개 매장, 매장당 20~50개 테이블)
C) 대규모 (50개 이상 매장, 매장당 50개 이상 테이블)
D) MVP 단계에서는 단일 매장만 고려
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 6
메뉴 이미지 관리 방식은 어떻게 하시겠습니까?

A) 외부 이미지 URL 직접 입력 (별도 이미지 서버 없음)
B) 로컬 파일 업로드 후 서버에 저장
C) 클라우드 스토리지 (S3 등) 업로드
D) MVP 단계에서는 이미지 URL만 사용하고, 추후 업로드 기능 추가
X) Other (please describe after [Answer]: tag below)

[Answer]: D

## Question 7
고객용 인터페이스와 관리자용 인터페이스를 어떻게 구성하시겠습니까?

A) 하나의 웹 애플리케이션에서 라우팅으로 분리
B) 별도의 두 개 웹 애플리케이션으로 분리
C) 모노레포 구조에서 별도 패키지로 관리
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 8
테이블 태블릿의 자동 로그인 정보 저장 방식은 어떻게 하시겠습니까?

A) 브라우저 localStorage 사용
B) 브라우저 쿠키 사용
C) 둘 다 사용 (토큰은 쿠키, 설정은 localStorage)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 9: Security Extensions
이 프로젝트에 보안 확장 규칙을 적용하시겠습니까?

A) Yes — 모든 SECURITY 규칙을 blocking constraint로 적용 (프로덕션 수준 애플리케이션에 권장)
B) No — 모든 SECURITY 규칙 건너뛰기 (PoC, 프로토타입, 실험적 프로젝트에 적합)
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 10
메뉴 관리 기능은 MVP 범위에 포함하시겠습니까? (요구사항 정의서 MVP 섹션에 메뉴 관리가 명시적으로 포함되어 있지 않습니다)

A) MVP에 포함 — 관리자가 메뉴를 등록/수정/삭제할 수 있어야 함
B) MVP에서 제외 — 초기 메뉴 데이터는 시드 데이터로 제공하고, 추후 메뉴 관리 기능 추가
X) Other (please describe after [Answer]: tag below)

[Answer]: A
