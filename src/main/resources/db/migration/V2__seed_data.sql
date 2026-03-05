-- 시드 데이터: 개발/테스트용

-- 매장 (master_pin: 000000)
INSERT INTO stores (name, master_pin) VALUES ('테스트 매장', '000000');

-- 관리자 계정 (username: admin, password: admin1234 → bcrypt hash)
INSERT INTO admin_accounts (store_id, username, password_hash)
VALUES (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- 카테고리
INSERT INTO categories (store_id, name, display_order) VALUES
(1, '메인', 1),
(1, '사이드', 2),
(1, '음료', 3),
(1, '디저트', 4);

-- 메뉴
INSERT INTO menus (store_id, name, description, price, display_order) VALUES
(1, '불고기 정식', '소불고기와 밥, 반찬이 함께 제공됩니다', 12000, 1),
(1, '김치찌개', '돼지고기 김치찌개', 9000, 2),
(1, '된장찌개', '두부 된장찌개', 8000, 3),
(1, '비빔밥', '야채 비빔밥', 10000, 4),
(1, '감자튀김', '바삭한 감자튀김', 5000, 1),
(1, '샐러드', '신선한 야채 샐러드', 6000, 2),
(1, '콜라', '코카콜라 355ml', 2000, 1),
(1, '사이다', '칠성사이다 355ml', 2000, 2),
(1, '아이스크림', '바닐라 아이스크림', 3000, 1);

-- 메뉴-카테고리 매핑
INSERT INTO menu_categories (menu_id, category_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1),
(5, 2), (6, 2),
(7, 3), (8, 3),
(9, 4);

-- 테이블
INSERT INTO restaurant_tables (store_id, table_number, pin) VALUES
(1, 1, '1234'),
(1, 2, '1234'),
(1, 3, '1234'),
(1, 4, '1234'),
(1, 5, '1234');
