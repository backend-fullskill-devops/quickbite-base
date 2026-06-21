-- 1. Tạo các User riêng cho từng Service
CREATE USER quickbite_user WITH PASSWORD 'quickbite_user';
CREATE USER quickbite_restaurant WITH PASSWORD 'quickbite_restaurant';
CREATE USER quickbite_order WITH PASSWORD 'quickbite_order';
CREATE USER quickbite_notification WITH PASSWORD 'quickbite_notification';

-- 2. Tạo các Database tương ứng (Tạo cả hai dạng tên để tương thích mọi Session)
CREATE DATABASE quickbite_user_db OWNER quickbite_user;
CREATE DATABASE quickbite_restaurant_db OWNER quickbite_restaurant;
CREATE DATABASE quickbite_order_db OWNER quickbite_order;
CREATE DATABASE quickbite_notification_db OWNER quickbite_notification;
-- 3. Phân quyền truy cập toàn diện trên từng database cho user tương ứng
GRANT ALL PRIVILEGES ON DATABASE quickbite_user_db TO quickbite_user;
GRANT ALL PRIVILEGES ON DATABASE quickbite_restaurant_db TO quickbite_restaurant;
GRANT ALL PRIVILEGES ON DATABASE quickbite_order_db TO quickbite_order;
GRANT ALL PRIVILEGES ON DATABASE quickbite_notification_db TO quickbite_notification;