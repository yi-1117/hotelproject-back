-- run server �e
--CREATE DATABASE hotel COLLATE Chinese_Taiwan_Stroke_CI_AS;

-- run server ��-
USE hotel;

-- �ק�ӫ~�Ӥ��e�q
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


-- �Ы�
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
('��X�M��','�@�i���H��(150cmX200cm)',92,'���ä����t�D��','�W�ߪ��F�o�ϩM�p�H���x�f�t���a���A����������@���L��',1,2,2,3,2000.0,2000,400.0,40),
('�i���M��','�@�i���H��(170cmX200cm)',157,'���ä����t�D��','�ȩФ���ηŷx��t���C�A���ɽ�P�Ϊ^��P�C�����i���P���Ū��l�i�ۤ����M�A���P�z�����C�@�B�ӭM�C',1,2,2,4,3000.0,3000,600.0,30),
('�ѻڮM��','��i���H��(150cmX200cm)',230,'���دD�Ǥ������ۤj�z�ۯD��','�D���j�U�P�e�����u�@�ϡA�W�ߨp�H���x�N�������ѻڽu�ɦ������C',1,4,2,6,6000.0,3000,600.0,20);




INSERT INTO [hotel].[dbo].[employee] 
    ([full_name], [profile_picture], [address], [date_of_birth], [email], [gender], 
     [join_date], [password], [phone_number], [role], [status], [updated_at])
VALUES 
    
    ('���j��', '', '�x�_�������ϩ����F���@�q1��', '1985-07-12', 'staff1@4newlife.com', '�k', '2023-01-10', '123', '0912-345-678', 'STAFF', 'ACTIVE', '2023-05-20'),
    ('������', '', '����������ϸθ۸�200��', '1995-11-30', 'reservationist1@4newlife.com', '�k', '2023-07-01', '123', '0956-789-012', 'RESERVATIONIST', 'ACTIVE', '2023-08-25'),
    ('�B�ө�', '', '�s�_���O���Ϥ�Ƹ��G�q5��', '1992-02-14', 'merchandiser1@4newlife.com', '�k', '2022-06-25', '123', '0967-890-123', 'MERCHANDISER', 'ACTIVE', '2023-09-05'),
    ('�d�v��', '', '�y�������s�����q77��', '1993-12-25', 'manager1@4newlife.com', '�k', '2021-12-30', '123', '0922-234-567', 'MANAGER', 'ACTIVE', '2024-02-05'),

    ('�i����', '', '�x������ٰϥx�W�j�D�T�q100��', '1988-04-18', 'staff2@4newlife.com', '�k', '2023-03-20', '123', '0934-567-890', 'STAFF', 'RESIGNATION', '2023-07-10'),
    ('�G����', '', '�x�n���F�ϪF���88��', '1994-08-07', 'reservationist2@4newlife.com', '�k', '2023-09-15', '123', '0989-012-345', 'RESERVATIONIST', 'LEAVE', '2023-11-20'),
    ('���h��', '', '�򶩥����R�ϷR�T��66��', '1989-03-03', 'merchandiser2@4newlife.com', '�k', '2022-04-10', '123', '0990-123-456', 'MERCHANDISER', 'ACTIVE', '2023-12-01'),
    ('���ا�', '', '�s�˥��F�ϥ��_���G�q150��', '1990-10-10', 'manager2@4newlife.com', '�k', '2023-07-14', '123', '0943-345-678', 'MANAGER', 'ACTIVE', '2025-01-20'),

    ('���ا�', '', '��饫���c�Ϥ��s��50��', '1987-05-22', 'staff3@4newlife.com', '�k', '2021-10-10', '123', '0978-901-234', 'STAFF', 'ACTIVE', '2023-10-12'),
    ('���p��', '', '�x�_���H�q�ϫH�q�����q20��', '1990-09-23', 'reservationist3@4newlife.com', '�k', '2022-12-15', '123', '0923-456-789', 'RESERVATIONIST', 'ACTIVE', '2023-06-15'),
    ('�P�ߩ�', '', '�s�˥��_�ϭ��~��12��', '1991-06-19', 'merchandiser3@4newlife.com', '�k', '2023-05-05', '123', '0911-123-456', 'MERCHANDISER', 'ACTIVE', '2024-01-10'),
    ('�L���P', '', '�x�n���ñd�Ϥ�����99��', '1983-04-21', 'manager3@4newlife.com', '�k', '2020-11-05', '123', '0944-456-789', 'MANAGER', 'ACTIVE', '2024-04-10');

INSERT INTO product_category (category_name) VALUES ('�N�᭹�~');
INSERT INTO product_category (category_name) VALUES ('�����N�᭹�~');

-- ���]�N�᭹�~���O�� ID �� 1�A�����N�᭹�~���O�� ID �� 2
INSERT INTO product (product_name, sku, brand_name, product_unit_price, discount, product_description, created_date, last_updated_date, capacity, stock_quantity, seller_name, seller_phone, seller_email, seller_address, product_category_id) VALUES
('������', 'SKU011', '���׫~�P', 65.00, 5.00, '���������״��ѡA�A�X�H�N���Ѯ�', GETDATE(), GETDATE(), 1, 100, '��aK', '0912345679', 'sellerK@example.com', '�x�_��K��1��', 1),
('�����', 'SKU012', '�����~�P', 120.00, 15.00, '�ǲΪ������A�t�h�جõ}����', GETDATE(), GETDATE(), 1, 50, '��aL', '0922345680', 'sellerL@example.com', '�x�_��L��2��', 1),
('����', 'SKU013', '�����~�P', 45.00, 0.00, '�@�������״��A�i���S����', GETDATE(), GETDATE(), 1, 80, '��aM', '0932345681', 'sellerM@example.com', '�x�_��M��3��', 1),
('��l', 'SKU014', '��l�~�P', 50.00, 0.00, '��u�s�@��������l�A�ֳt�K�Q', GETDATE(), GETDATE(), 1, 150, '��aN', '0942345682', 'sellerN@example.com', '�x�_��N��4��', 1),
('ī�G��', 'SKU015', '���~�~�P', 30.00, 2.00, '�s�Aī�G�s�@���������A�������̳]�p', GETDATE(), GETDATE(), 1, 100, '��aO', '0952345683', 'sellerO@example.com', '�x�_��O��5��', 2);