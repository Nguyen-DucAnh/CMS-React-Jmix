# 🚀 Hướng Dẫn Triển Khai Hệ Thống Jmix - React - Keycloak

Tài liệu này hướng dẫn cách triển khai hệ thống Full Stack theo tiêu chuẩn hiện đại, sử dụng **Docker Compose**, quản lý biến môi trường qua `.env` và bảo mật với **Keycloak OIDC**.

## 🏗 Kiến Trúc Hệ Thống

Hệ thống bao gồm 4 thành phần chính được container hóa:
1.  **SQL Server**: Cơ sở dữ liệu chính.
2.  **Keycloak**: Quản lý định danh và quyền (Identity Provider).
3.  **Backend (Jmix/Spring Boot)**: Resource Server, cung cấp API và quản lý dữ liệu.
4.  **Frontend (React/Vite)**: Ứng dụng giao diện người dùng (Public Client).

---

## 🔐 Bảo Mật & Phân Quyền (Rất Quan Trọng)

Chúng ta sử dụng mô hình bảo mật chuẩn **OAuth2/OIDC**:

### 1. Tại sao Frontend là Public Client?
Ứng dụng React chạy trên trình duyệt của người dùng. Theo tiêu chuẩn, nó **không thể lưu giữ Secret** một cách an toàn. Vì vậy:
- **Keycloak Client Settings**: Phải đặt `Client Authentication: OFF` (Public).
- **Cơ chế thay thế**: Sử dụng **PKCE** (Proof Key for Code Exchange) để đảm bảo không ai có thể đánh cắp code để đổi lấy token.

### 2. Tại sao Backend dùng Client Secret?
Backend chạy trong môi trường Server an toàn, nên nó được coi là **Confidential Client**. Nó sử dụng `client-secret` để xác thực với Keycloak khi cần kiểm tra Token hoặc truy xuất thông tin User.

---

## 🛠 Hướng Dẫn Cài Đặt

### Bước 1: Chuẩn bị file biến môi trường (.env)
Tạo file `.env` tại thư mục gốc của Backend (`restapi/`) để quản lý các tham số nhạy cảm:

```env
# Database
MSSQL_SA_PASSWORD=Mật_khẩu_SQL_của_bạn

# Keycloak Admin
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin

# OIDC Secret (Lấy từ Keycloak Credentials)
KEYCLOAK_CLIENT_SECRET=Mã_secret_của_bạn
```

### Bước 2: Cấu hình Volumes (Lưu trữ dữ liệu)
Trong `docker-compose.yml`, chúng ta sử dụng **Volumes** để dữ liệu không bị mất khi container khởi động lại:
```yaml
volumes:
  mssql_data: # Lưu database SQL Server
  keycloak_data: # Lưu cấu hình Realm/User của Keycloak
```

### Bước 3: Build và Khởi chạy
Chạy lệnh sau tại thư mục chứa file `docker-compose.yml`:
```bash
# Build lại file JAR và khởi động container
./gradlew bootJar 
docker compose up -d --build
```

---

## 📝 Quy Trình Cấu Hình Keycloak (Lần đầu tiên)

1. Truy cập `http://localhost:8180` (admin/admin).
2. Tạo **Realm** (Vd: `cms-frontend`).
3. Tạo **Client**:
   - ID: `app-jmix`
   - Authentication: **OFF** (Dành cho React).
   - Valid Redirect URIs: `http://localhost:3000/*`
   - Web Origins: `*` (Tránh lỗi CORS).
4. Tạo **User**:
   - Tạo username và đặt password trong tab **Credentials**.

---

## 🚀 Lợi Ích Của Cách Làm Này
- **Tính Nhất Quán**: Mọi thành viên trong team đều chạy trên một môi trường Docker giống hệt nhau.
- **Dễ Bảo Trì**: Thay đổi cấu hình chỉ cần sửa file `.env`.
- **An Toàn**: Tách biệt rõ ràng giữa ứng dụng công khai (Frontend) và dịch vụ nội bộ (Backend).

---

## 🎓 Lộ Trình Học Tập Để Làm Chủ Deployment

Để có thể tự tay triển khai các hệ thống phức tạp hơn trên môi trường thực tế (Production), bạn nên tìm hiểu thêm các mảng kiến thức sau:

### 1. Docker & Docker Compose (Nâng cao)
- **Multi-stage Builds**: Cách tối ưu hóa kích thước Image (như cách chúng ta làm với React).
- **Docker Networking**: Hiểu sâu về `bridge`, `host`, và `overlay` networks.
- **Docker Swarm hoặc Kubernetes**: Khi ứng dụng của bạn cần chạy trên nhiều máy chủ cùng lúc.

### 2. Quản trị Web Server & Reverse Proxy (Nginx)
- **Cấu hình Reverse Proxy**: Cách dùng Nginx làm "người gác cổng" để điều hướng request vào Backend.
- **SSL/TLS (HTTPS)**: Cách cài đặt chứng chỉ bảo mật với Let's Encrypt.
- **Load Balancing**: Phân phối tải khi có hàng triệu người dùng.

### 3. CI/CD (Tự động hóa triển khai)
- **GitHub Actions / GitLab CI**: Tự động build và deploy ngay khi bạn `git push`.
- **Jenkins**: Công cụ tự động hóa mạnh mẽ cho các dự án lớn.

### 4. Quản trị Linux (Ubuntu/CentOS)
- **Bash Scripting**: Viết các đoạn mã tự động hóa các tác vụ lặp đi lặp lại.
- **SSH & Security**: Cách quản lý server từ xa và thiết lập tường lửa (Firewall).

### 5. Giám sát & Ghi log (Monitoring & Logging)
- **ELK Stack (Elasticsearch, Logstash, Kibana)**: Quản lý tập trung toàn bộ log của hệ thống.
- **Prometheus & Grafana**: Theo dõi biểu đồ sức khỏe của CPU, Ram và các dịch vụ.

---
**Lời khuyên**: Hãy bắt đầu bằng việc tự cài đặt Nginx trên một con VPS Linux thực tế và cấu hình HTTPS cho dự án này. Đó sẽ là bước tiến lớn nhất để bạn làm chủ kỹ năng Deployment!

---
*Chúc bạn học tập và phát triển dự án thành công!*

