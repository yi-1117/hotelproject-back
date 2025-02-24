-- 插入 room_type 資料
INSERT INTO room_type (
    room_type_name, bed_type, area, bathroom_type, room_type_description, is_handicap,
    adult_capacity, children_capacity, max_capacity, unit_price, additional_price_per_person,
    over_time_ratio, max_count
)
VALUES
('single_room', 'single', 20.0, 'shared', 'comfortable single bed room', 0, 1, 1, 2, 100.0, 20.0, 1.5, 2),
('double_room', 'double', 30.0, 'private', 'spacious double bed room', 1, 2, 2, 4, 200.0, 30.0, 1.5, 3),
('family_room', 'queen', 40.0, 'private', 'ideal for families', 0, 4, 2, 6, 300.0, 50.0, 2.0, 6);

-- 插入 room 資料
INSERT INTO room (room_number, room_floor, building_id, fk_room_type_id)
VALUES
(101, 1, 1, 1),
(102, 1, 1, 1),
(103, 1, 1, 1),
(104, 1, 1, 1),
(105, 1, 1, 1),
(106, 1, 1, 1),
(107, 1, 1, 1),
(108, 1, 1, 1),
(201, 2, 1, 2),
(202, 2, 1, 2),
(203, 2, 1, 2),
(204, 2, 1, 2),
(301, 3, 1, 3),
(302, 3, 1, 3),
(303, 3, 1, 3);

-- 插入 room_status 資料
INSERT INTO room_status (is_clear, is_clean, is_functional, is_disturbable, is_occupied, fk_room_id)
VALUES
(1, 1, 1, 1, 0, 1),
(1, 1, 1, 1, 0, 2),
(1, 1, 1, 1, 0, 3),
(1, 1, 1, 1, 0, 4),
(1, 1, 1, 1, 0, 5),
(1, 1, 1, 1, 0, 6),
(1, 1, 1, 1, 0, 7),
(1, 1, 1, 1, 0, 8),
(1, 1, 1, 1, 0, 9),
(1, 1, 1, 1, 0, 10),
(1, 1, 1, 1, 0, 11),
(1, 1, 1, 1, 0, 12),
(1, 1, 1, 1, 0, 13),
(1, 1, 1, 1, 0, 14),
(1, 1, 1, 1, 0, 15);


-- 插入 room_order 資料
INSERT INTO room_order (
    room_offer_id, order_status, resident_count, total_payment, order_time, starting_time,
    leaving_time, checkin_time, checkout_time
)
VALUES
(101, 'waiting', 1, 100.0, '2025-01-17 12:00:00', '2025-01-18 15:00:00', '2025-01-20 10:00:00', NULL, NULL),
(102, 'living', 2, 120.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL),
(201, 'living', 2, 200.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL),
(202, 'waiting', 3, 230.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL),
(301, 'living', 3, 300.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL),
(302, 'waiting', 4, 300.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL),
(303, 'left', 5, 350.0, '2025-01-17 13:00:00', '2025-01-18 15:00:00', '2025-01-22 10:00:00', NULL, NULL);

-- 插入 room_type_order 資料
INSERT INTO room_type_order (room_count, room_type_id)
VALUES
(1, 1),
(1, 2),
(1, 3);
