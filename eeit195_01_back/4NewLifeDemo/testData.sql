-- run server 前
--CREATE DATABASE hotel COLLATE Chinese_Taiwan_Stroke_CI_AS;

-- run server 後-
USE hotel;

-- 修改商品照片容量
ALTER TABLE [hotel].[dbo].[product_photo]
ALTER COLUMN [photo] VARBINARY(MAX);

INSERT INTO member (password, email, status, created_at, updated_at, is_verified) VALUES
('123456', 'user1@example.com', 'ACTIVE', '2024-01-01 10:00:00', '2024-01-01 10:00:00', 1),
('abcdef', 'user2@example.com', 'INACTIVE', '2024-01-05 12:30:00', '2024-01-10 14:45:00', 0),
('password123', 'user3@example.com', 'SUSPENDED', '2024-02-01 08:15:00', '2024-02-02 09:20:00', 1),
('qwerty', 'user4@example.com', 'ACTIVE', '2024-02-10 15:20:00', '2024-02-15 16:25:00', 1),
('letmein', 'user5@example.com', 'PENDING', '2024-02-15 18:30:00', '2024-02-16 19:35:00', 0),
('welcome123', 'user6@example.com', 'ACTIVE', '2024-03-01 10:00:00', '2024-03-02 11:05:00', 1),
('123qwe', 'user7@example.com', 'INACTIVE', '2024-03-05 12:10:00', '2024-03-06 13:15:00', 0),
('testuser', 'user8@example.com', 'SUSPENDED', '2024-03-10 14:20:00', '2024-03-11 15:25:00', 1),
('mysecurepwd', 'user9@example.com', 'ACTIVE', '2024-04-01 16:30:00', '2024-04-02 17:35:00', 1),
('changeme', 'user10@example.com', 'PENDING', '2024-04-05 18:40:00', '2024-04-06 19:45:00', 0);


-- 房型
INSERT INTO room_type (
    room_type_name,
    bed_type,
    area,
    bathroom_type,
    room_type_description,
    is_handicap,
    adult_capacity,
    children_capacity,
    max_capacity,
    unit_price,
    additional_price_per_person,
    over_time_ratio,
    max_count
)
VALUES 
('日出套房','一張雙人床(150cmX200cm)',92,'乾溼分離含浴缸','獨立的沙發區和私人陽台搭配落地窗，絕美的景色一覽無遺',1,2,2,3,2000.0,2000,400.0,40),
('夕陽套房','一張雙人床(170cmX200cm)',157,'乾溼分離含浴缸','客房內選用溫暖色系裝潢，提升質感及氛圍感。金黃夕陽與湛藍的餘波相互輝映，放鬆您全身每一處細胞。',1,2,2,4,3000.0,3000,600.0,30),
('天際套房','兩張雙人床(150cmX200cm)',230,'奢華浴室內坐落著大理石浴缸','挑高大廳與寬敞的工作區，獨立私人陽台將城市的天際線盡收眼底。',1,4,2,6,6000.0,3000,600.0,20);




INSERT INTO [hotel].[dbo].[employee] 
    ([full_name], [profile_picture], [address], [date_of_birth], [email], [gender], 
     [join_date], [password], [phone_number], [role], [status], [updated_at])
VALUES 
    
    ('王大明', '', '台北市中正區忠孝東路一段1號', '1985-07-12', 'staff1@4newlife.com', '男', '2023-01-10', '123', '0912-345-678', 'STAFF', 'ACTIVE', '2023-05-20'),
    ('陳美玲', '', '高雄市左營區裕誠路200號', '1995-11-30', 'reservationist1@4newlife.com', '女', '2023-07-01', '123', '0956-789-012', 'RESERVATIONIST', 'ACTIVE', '2023-08-25'),
    ('劉志明', '', '新北市板橋區文化路二段5號', '1992-02-14', 'merchandiser1@4newlife.com', '男', '2022-06-25', '123', '0967-890-123', 'MERCHANDISER', 'ACTIVE', '2023-09-05'),
    ('吳宗翰', '', '宜蘭市中山路五段77號', '1993-12-25', 'manager1@4newlife.com', '男', '2021-12-30', '123', '0922-234-567', 'MANAGER', 'ACTIVE', '2024-02-05'),

    ('張偉傑', '', '台中市西屯區台灣大道三段100號', '1988-04-18', 'staff2@4newlife.com', '男', '2023-03-20', '123', '0934-567-890', 'STAFF', 'RESIGNATION', '2023-07-10'),
    ('鄭雅雯', '', '台南市東區東寧路88號', '1994-08-07', 'reservationist2@4newlife.com', '女', '2023-09-15', '123', '0989-012-345', 'RESERVATIONIST', 'LEAVE', '2023-11-20'),
    ('趙士傑', '', '基隆市仁愛區愛三路66號', '1989-03-03', 'merchandiser2@4newlife.com', '男', '2022-04-10', '123', '0990-123-456', 'MERCHANDISER', 'ACTIVE', '2023-12-01'),
    ('王建宏', '', '新竹市東區光復路二段150號', '1990-10-10', 'manager2@4newlife.com', '男', '2023-07-14', '123', '0943-345-678', 'MANAGER', 'ACTIVE', '2025-01-20'),

    ('黃建宏', '', '桃園市中壢區中山路50號', '1987-05-22', 'staff3@4newlife.com', '男', '2021-10-10', '123', '0978-901-234', 'STAFF', 'ACTIVE', '2023-10-12'),
    ('李小華', '', '台北市信義區信義路五段20號', '1990-09-23', 'reservationist3@4newlife.com', '女', '2022-12-15', '123', '0923-456-789', 'RESERVATIONIST', 'ACTIVE', '2023-06-15'),
    ('周心怡', '', '新竹市北區食品路12號', '1991-06-19', 'merchandiser3@4newlife.com', '女', '2023-05-05', '123', '0911-123-456', 'MERCHANDISER', 'ACTIVE', '2024-01-10'),
    ('林志鵬', '', '台南市永康區中正路99號', '1983-04-21', 'manager3@4newlife.com', '男', '2020-11-05', '123', '0944-456-789', 'MANAGER', 'ACTIVE', '2024-04-10');

INSERT INTO product_category (category_name) VALUES ('冷凍食品');
INSERT INTO product_category (category_name) VALUES ('素食冷凍食品');

-- 假設冷凍食品類別的 ID 為 1，素食冷凍食品類別的 ID 為 2
INSERT INTO product (product_name, sku, brand_name, product_unit_price, discount, product_description, created_date, last_updated_date, capacity, stock_quantity, seller_name, seller_phone, seller_email, seller_address, product_category_id) VALUES
('牛肉麵', 'SKU011', '牛肉品牌', 65.00, 5.00, '美味的牛肉湯麵，適合寒冷的天氣', GETDATE(), GETDATE(), 1, 100, '賣家K', '0912345679', 'sellerK@example.com', '台北市K區1號', 1),
('佛跳牆', 'SKU012', '佛跳牆品牌', 120.00, 15.00, '傳統的佛跳牆，含多種珍稀食材', GETDATE(), GETDATE(), 1, 50, '賣家L', '0922345680', 'sellerL@example.com', '台北市L區2號', 1),
('雞湯', 'SKU013', '雞湯品牌', 45.00, 0.00, '濃郁的雞肉湯，養身又美味', GETDATE(), GETDATE(), 1, 80, '賣家M', '0932345681', 'sellerM@example.com', '台北市M區3號', 1),
('餃子', 'SKU014', '餃子品牌', 50.00, 0.00, '手工製作的美味餃子，快速便利', GETDATE(), GETDATE(), 1, 150, '賣家N', '0942345682', 'sellerN@example.com', '台北市N區4號', 1),
('蘋果派', 'SKU015', '甜品品牌', 30.00, 2.00, '新鮮蘋果製作的香甜派，為素食者設計', GETDATE(), GETDATE(), 1, 100, '賣家O', '0952345683', 'sellerO@example.com', '台北市O區5號', 2);