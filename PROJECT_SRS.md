# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS) - QUICKBITE SYSTEM

## 1. Tổng quan hệ thống (System Overview)
**QuickBite** là hệ thống đặt món ăn trực tuyến được xây dựng theo kiến trúc Microservices. Dự án phục vụ mục đích thiết kế kiến trúc phần mềm, phát triển backend, và thực hành xây dựng các dịch vụ độc lập với Spring Boot.

**Nguyên lý kiến trúc cốt lõi:**
- **Microservices Pattern**: Phân tách logic nghiệp vụ thành các dịch vụ độc lập.
- **API Gateway Pattern**: Cung cấp một điểm truy cập duy nhất (Single Entry Point) cho toàn bộ hệ thống từ phía Client.
- **Database-per-service**: Mỗi dịch vụ quản lý một cơ sở dữ liệu riêng biệt. Giao tiếp và liên kết dữ liệu giữa các dịch vụ thông qua Soft Reference (chỉ lưu ID, không tạo Foreign Key vật lý).
- **Saga Pattern (Orchestration)**: Xử lý giao dịch phân tán qua nhiều service, với `order-service` đóng vai trò là nhạc trưởng điều phối toàn bộ quá trình.
- **Eventual Consistency & Idempotency**: Đảm bảo tính nhất quán cuối cùng của dữ liệu và tính lũy đẳng trong các giao dịch nhạy cảm (như thanh toán, hoàn tiền).
- **Centralized Logging & Observability**: Bắt buộc chuẩn hóa log toàn hệ thống theo định dạng ECS để phục vụ thu thập tập trung.

## 2. Kiến trúc Microservices (Services Architecture)

Hệ thống bao gồm 1 API Gateway và 4 Microservices chính:

### 2.1. API Gateway (`gateway-service`)
- **Port:** `8080`
- **Công nghệ:** Spring Cloud Gateway, WebFlux/Netty (Non-blocking IO).
- **Chức năng:** Định tuyến request (Routing), giới hạn truy cập (Rate Limiting), có thể đính kèm Authentication Filter để xác thực JWT. Tạo và khởi tạo Trace ID cho chuỗi request.
- **Routing Rules (Định tuyến API):**
  - `/api/v1/users/**` ➔ `user-service`
  - `/api/v1/restaurants/**` ➔ `restaurant-service`
  - `/api/v1/orders/**` ➔ `order-service`
  - `/api/v1/notifications/**` ➔ `notification-service`

### 2.2. User Service (`user-service`)
- **Port:** `8081`
- **Công nghệ:** Java 17, Spring Boot.
- **Chức năng:** Quản lý người dùng, xử lý đăng ký/đăng nhập, xác thực (JWT), quản lý địa chỉ nhận hàng và ví điện tử.
- **Database:** `quickbite_user_db` (PostgreSQL)

### 2.3. Restaurant Service (`restaurant-service`)
- **Port:** `8082`
- **Công nghệ:** Java 21, Spring Boot.
- **Chức năng:** Quản lý danh sách nhà hàng, danh mục (categories), món ăn (menu items), trạng thái đóng/mở cửa của nhà hàng.
- **Database:** `quickbite_restaurant_db` (PostgreSQL)

### 2.4. Order Service (`order-service`)
- **Port:** `8083`
- **Công nghệ:** Java 21, Spring Boot.
- **Chức năng:** Quản lý đơn hàng, chi tiết đơn hàng, lịch sử thay đổi trạng thái. Xử lý logic đặt hàng và điều phối Saga pattern.
- **Database:** `quickbite_order_db` (PostgreSQL)

### 2.5. Notification Service (`notification-service`)
- **Port:** `8084`
- **Công nghệ:** Java 21, Spring Boot.
- **Chức năng:** Quản lý và gửi thông báo cho người dùng (IN_APP, EMAIL, SMS) về các thay đổi trạng thái trong hệ thống.
- **Database:** `quickbite_notification_db` (PostgreSQL)

---

## 3. Thiết kế Cơ sở dữ liệu (Database Schema)

Các cơ sở dữ liệu được thiết kế hoàn toàn tách biệt. Có thể sử dụng cơ chế Hibernate DDL-auto (update) để tự động tạo bảng.

### 3.1. Database: `quickbite_user_db`
- **`users`**: `id`, `username`, `password`, `full_name`, `role` (CUSTOMER, DRIVER, MERCHANT)
- **`user_addresses`**: `id`, `user_id` (FK ➔ users), `label`, `detail_address`, `is_default`
- **`user_wallets`**: `id`, `user_id` (FK ➔ users, Unique), `balance` (Decimal 12,2)

### 3.2. Database: `quickbite_restaurant_db`
- **`restaurants`**: `id`, `owner_id` (Soft Ref ➔ users), `name`, `is_open`
- **`menu_categories`**: `id`, `restaurant_id` (FK ➔ restaurants), `name`
- **`menu_items`**: `id`, `category_id` (FK ➔ menu_categories), `name`, `base_price`, `is_available`

### 3.3. Database: `quickbite_order_db`
- Tích hợp **Snapshot Pattern** để lưu cứng tên/giá trị tại thời điểm đặt đơn.
- **`orders`**: `id`, `customer_id` (Soft Ref), `customer_name` (Snapshot), `restaurant_id` (Soft Ref), `merchant_name` (Snapshot), `driver_id`, `delivery_address_id`, `items_price`, `shipping_fee`, `total_price`, `status` (PENDING, ACCEPTED, SHIPPING, DELIVERED, CANCELLED, FAILED).
- **`order_items`**: `id`, `order_id` (FK ➔ orders), `menu_item_id` (Soft Ref), `item_name` (Snapshot), `quantity`, `price` (Snapshot).
- **`order_status_history`**: `id`, `order_id` (FK ➔ orders), `status`, `note`, `changed_at`.

### 3.4. Database: `quickbite_notification_db`
- **`notifications`**: `id`, `user_id` (Soft Ref), `title`, `content`, `type` (IN_APP, EMAIL, SMS), `delivery_status` (PENDING, SENT, FAILED), `created_at`.

---

## 4. Tiêu chuẩn Hệ thống Logging (ECS Logging & Distributed Tracing)

Để đảm bảo khả năng theo dõi (Observability) và thuận tiện cho tác nhân thu thập log (như Fluentd/Filebeat) đọc, phân tích và đẩy lên hệ thống Centralized Logging, **toàn bộ các service (kể cả API Gateway) bắt buộc phải tuân thủ nghiêm ngặt các quy định phát triển logging sau đây**:

### 4.1. Định dạng Log chuẩn (Elastic Common Schema - ECS)
- Các log không được xuất ra dưới dạng plain text thông thường (ví dụ: `2026-06-25 INFO [Thread] ...`). Thay vào đó, toàn bộ log xuất ra `stdout`/`stderr` hoặc ghi ra file `.json` phải tuân thủ chuẩn JSON của ECS (Mỗi dòng log là một JSON Object độc lập - Single-line JSON).
- **Cài đặt kỹ thuật**: 
  - Sử dụng thư viện `ecs-logging-logback` (hoặc tương đương) tích hợp vào Spring Boot.
  - Cấu hình file `logback-spring.xml` sử dụng `<encoder class="co.elastic.logging.logback.EcsEncoder">`.
  - Phải chỉ định rõ `service.name` trong log để phân biệt log đến từ service nào (ví dụ: `user-service`, `order-service`).

### 4.2. Truy vết phân tán (Distributed Tracing)
Trong môi trường Microservices, một request từ user có thể đi qua Gateway, gọi tới Order, sau đó gọi sang User và Restaurant. Việc gom nhóm log của chuỗi request này là bắt buộc.
- **Trace ID**: Mỗi request vào hệ thống từ API Gateway phải được gắn một mã `trace.id` (ví dụ: `X-Trace-Id` UUID) vào HTTP Header.
- **Log Context (MDC)**: Các microservices khi nhận được request phải trích xuất header `X-Trace-Id` và đưa vào Mapped Diagnostic Context (MDC) của SLF4J. Từ đó, bộ EcsEncoder sẽ tự động append trường `trace.id` vào cấu trúc JSON của từng dòng log.
- **OpenFeign Interceptor**: Khi một service gọi sang service khác (VD: `order-service` gọi `user-service`), bắt buộc phải có một `RequestInterceptor` của Feign tự động nhúng `X-Trace-Id` từ MDC vào Header của HTTP Request tiếp theo.

### 4.3. Quy ước mức độ Log (Log Levels)
- **ERROR**: Các Exception không thể tự phục hồi (ví dụ lỗi DB, gọi ngoại vi thất bại). Phải kèm theo Error Stack Trace đầy đủ (ECS hỗ trợ trường `error.stack_trace`).
- **WARN**: Các lỗi nghiệp vụ (Validation, sai mật khẩu, trừ tiền thất bại do thiếu số dư).
- **INFO**: Ghi lại các mốc thời gian, trạng thái quan trọng (ví dụ: "Đã khởi tạo đơn hàng X", "Bắt đầu thanh toán cho đơn Y").

---

## 5. Giao tiếp liên dịch vụ (Inter-service Communication)

Toàn bộ hệ thống giao tiếp giữa các dịch vụ theo cơ chế **Synchronous (Đồng bộ) qua REST API**, sử dụng `Spring Cloud OpenFeign`.
- *(Bắt buộc)*: Cấu hình Feign Client phải gắn header `X-Trace-Id` cho mục đích tracking như định nghĩa ở mục 4.
- `GET /users/{id}`: `order-service` lấy thông tin chi tiết người dùng.
- `POST /users/{id}/wallet/deduct`: Trừ tiền ví điện tử khi đơn hàng được khởi tạo.
- `POST /users/{id}/wallet/refund`: Hoàn tiền vào ví (Compensating Transaction khi nhà hàng từ chối).
- `GET /restaurants/{id}/status`: `order-service` kiểm tra trạng thái mở cửa của nhà hàng.
- `GET /restaurants/{id}/menu-items`: `order-service` lấy danh sách món để tính giá trị đơn hàng, tạo Snapshot.
- `POST /notifications`: `order-service` hoặc các service khác gọi sang `notification-service` để tạo và gửi thông báo cho khách hàng khi có thay đổi nghiệp vụ.

---

## 6. Nghiệp vụ cốt lõi: Luồng đặt hàng (Saga Orchestration Flow)

Luồng thực thi đặt hàng từ đầu đến cuối (Happy Path & Compensating Transactions):

1. **Khởi tạo đơn (Create Order)**
   - Client gửi yêu cầu đặt đơn qua API Gateway đến `order-service`. API Gateway khởi tạo `trace.id`.
   - `order-service` gọi `restaurant-service` để lấy thông tin chi tiết để tạo Data Snapshot.
   - `order-service` tạo bản ghi `orders` với trạng thái `PENDING`. Lưu các Snapshot món ăn vào `order_items`. Ghi log [INFO].
2. **Thanh toán (Deduct Wallet)**
   - `order-service` gọi API sang `user-service` yêu cầu trừ tiền.
   - Cập nhật lịch sử: `order-service` gọi sang `notification-service` gửi thông báo.
   - *Nếu ví không đủ tiền hoặc API gọi thất bại*: Chuyển trạng thái đơn sang `FAILED`. Ghi log [WARN]/[ERROR]. Kết thúc chu trình Saga.
3. **Chấp nhận đơn (Restaurant Approval)**
   - `order-service` thông báo sang `restaurant-service` để nhà hàng chuẩn bị món. Đơn hàng đổi thành `ACCEPTED`.
   - *Nếu nhà hàng từ chối*: 
     - **Giao dịch bù (Compensating Transaction)**: `order-service` gọi ngược lại API của `user-service` thực hiện hành động `refund`.
     - Đổi trạng thái đơn thành `CANCELLED`. Gọi `notification-service` báo lý do cho khách hàng. Kết thúc chu trình Saga.
4. **Vận chuyển (Shipping)**
   - Tìm được Driver (Giả lập), đổi trạng thái đơn thành `SHIPPING`. Tiếp tục gọi `notification-service`.
5. **Hoàn thành (Delivered)**
   - Giao hàng thành công, đổi trạng thái thành `DELIVERED`. Gửi thông báo hoàn thành qua `notification-service`. Ghi log [INFO] chu trình hoàn tất.

*(Lưu ý quan trọng: Tính lũy đẳng - Idempotency cần được ưu tiên xử lý đặc biệt ở API Deduct và Refund của User Service. Việc này để ngăn chặn tình trạng một request bị retry tự động do network timeout dẫn đến việc trừ hoặc cộng tiền nhiều lần cho cùng một giao dịch).*
