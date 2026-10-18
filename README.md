# Docker Spring Lab (docker-spring-lab)

> **Dự án mẫu Spring Boot 3 & Java 17 chuẩn bị cho lộ trình thực hành Docker từ cơ bản đến Docker Compose.**

---

## 1. Mục tiêu của dự án

Dự án này là một ứng dụng REST API quản lý sản phẩm (Product Management API) viết bằng Java 17 và Spring Boot 3. 

Mục tiêu chính là cung cấp một **backend hoàn chỉnh, chạy ổn định ở môi trường local** mà sinh viên có thể kiểm thử ngay. Sau khi làm quen với ứng dụng, sinh viên sẽ dùng chính source code này để từng bước thực hành các chủ đề Docker:

1. **Docker Engine**: Phân biệt Virtual Machine (VM) và Container; kiến trúc Docker Daemon & Docker Client.
2. **Docker Image & Container cơ bản**: Các thao tác `docker build`, `docker run`, `docker ps`, `docker stop`, `docker rm`.
3. **Single-stage Dockerfile**: Đóng gói ứng dụng Java chạy trên container.
4. **Multi-stage Build & Layer Cache**: Tối ưu dung lượng Image từ ~500MB xuống dưới ~200MB và tăng tốc độ build.
5. **Docker Networking**: Kết nối ứng dụng Spring Boot container với container MySQL qua user-defined bridge network.
6. **Docker Volumes**: Gắn Named Volume để bảo toàn dữ liệu database khi MySQL container bị stop/remove.
7. **Docker Compose**: Điều phối toàn bộ stack gồm Spring Boot và MySQL chỉ với một lệnh `docker compose up -d`.

---

## 2. Công nghệ sử dụng

- **Java Runtime**: OpenJDK 17 (LTS) - Sử dụng Gradle Java Toolchain Java 17.
- **Framework**: Spring Boot `3.5.16` (Dòng 3.5.x GA ổn định, tương thích hoàn toàn Java 17).
- **Build Tool**: Gradle 8.7 với Gradle Wrapper (`gradlew`, `gradlew.bat`).
- **Data Access & ORM**: Spring Data JPA & Hibernate.
- **In-Memory Database**: H2 Database (mặc định cho local development, không cần cài MySQL trước).
- **Relational Database Driver**: MySQL Connector/J `com.mysql:mysql-connector-j` (sẵn sàng khi chạy kết nối container MySQL).
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`).
- **Testing**: JUnit 5, Spring Boot Starter Test, Mockito, MockMvc.
- **Code Style**: Java thuần (Plain Old Java Objects - POJO), **không dùng Lombok** để người mới bắt đầu dễ dàng đọc hiểu luồng getter/setter/constructor.

---

## 3. Cấu trúc thư mục và vai trò các package

```text
docker-spring-lab/
├── build.gradle                          # Khai báo dependencies, Java toolchain 17, plugins
├── settings.gradle                       # Cấu hình tên project (docker-spring-lab)
├── gradlew                               # Script chạy Gradle Wrapper trên Linux / macOS
├── gradlew.bat                           # Script chạy Gradle Wrapper trên Windows
├── gradle/wrapper/
│   ├── gradle-wrapper.jar                # Binary chạy wrapper độc lập
│   └── gradle-wrapper.properties         # Cấu hình phiên bản Gradle 8.7
├── src/
│   ├── main/
│   │   ├── java/com/example/dockerspringlab/
│   │   │   ├── DockerSpringLabApplication.java # Class chính chứa hàm main và init data mẫu
│   │   │   ├── controller/               # Lớp tiếp nhận HTTP Request & trả về Response
│   │   │   │   ├── HealthController.java # API kiểm tra sức khỏe ứng dụng & Java runtime
│   │   │   │   └── ProductController.java# API CRUD quản lý sản phẩm
│   │   │   ├── service/                  # Lớp xử lý nghiệp vụ (Business Logic)
│   │   │   │   └── ProductService.java
│   │   │   ├── repository/               # Lớp truy vấn cơ sở dữ liệu qua Spring Data JPA
│   │   │   │   └── ProductRepository.java
│   │   │   ├── entity/                   # Định nghĩa bảng dữ liệu JPA (@Entity)
│   │   │   │   └── Product.java
│   │   │   ├── dto/                      # Data Transfer Objects (Request/Response payload)
│   │   │   │   ├── ProductRequest.java   # Payload tạo/sửa có validation Jakarta
│   │   │   │   ├── ProductResponse.java  # DTO trả về cho client
│   │   │   │   ├── HealthResponse.java   # DTO trả về cho endpoint health
│   │   │   │   └── ErrorResponse.java    # Cấu trúc lỗi chuẩn hóa
│   │   │   └── exception/                # Xử lý ngoại lệ tập trung (@RestControllerAdvice)
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties    # Cấu hình datasource linh hoạt (H2 / MySQL)
│   └── test/java/com/example/dockerspringlab/
│       ├── DockerSpringLabApplicationTests.java
│       ├── controller/
│       │   ├── HealthControllerTest.java # Test endpoint /api/health
│       │   └── ProductControllerTest.java# Test CRUD & Validation
│       └── service/
│           └── ProductServiceTest.java   # Unit test nghiệp vụ Service bằng Mockito
├── .gitignore
└── README.md
```

---

## 4. Điều kiện tiên quyết & Kiểm tra môi trường

### 4.1. Kiểm tra phiên bản Java

Máy tính cần cài đặt **JDK 17**. Kiểm tra bằng terminal:

```bash
java -version
```

Kết quả mong đợi tương tự:
```text
openjdk version "17.0.x" ...
OpenJDK Runtime Environment ...
```

### 4.2. Kiểm tra Gradle

Dự án đã tích hợp sẵn **Gradle Wrapper**, bạn **không cần cài Gradle thủ công vào máy**.

- Trên **Windows (PowerShell / CMD)**:
  ```powershell
  .\gradlew.bat --version
  ```
- Trên **Linux / macOS / Ubuntu**:
  ```bash
  chmod +x ./gradlew
  ./gradlew --version
  ```

---

## 5. Hướng dẫn chạy Test và khởi động ứng dụng

### 5.1. Chạy toàn bộ Test kiểm thử

- **Windows**:
  ```powershell
  .\gradlew.bat test
  ```
- **Linux / macOS**:
  ```bash
  ./gradlew test
  ```

Báo cáo kết quả test chi tiết được sinh ra tại: `build/reports/tests/test/index.html`.

### 5.2. Chạy ứng dụng Spring Boot

- **Windows**:
  ```powershell
  .\gradlew.bat bootRun
  ```
- **Linux / macOS**:
  ```bash
  ./gradlew bootRun
  ```

Ứng dụng sẽ khởi động tại cổng **8080** (`http://localhost:8080`).

---

## 6. Danh sách REST API & Ví dụ kiểm thử với cURL

### 6.1. Bảng tóm tắt API

| Phương thức | Đường dẫn URL | Mô tả | Mã HTTP thành công |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | Kiểm tra trạng thái ứng dụng & Java runtime | `200 OK` |
| `GET` | `/api/products` | Lấy danh sách tất cả sản phẩm | `200 OK` |
| `GET` | `/api/products/{id}` | Lấy thông tin chi tiết một sản phẩm theo ID | `200 OK` (404 nếu không thấy) |
| `POST` | `/api/products` | Thêm mới sản phẩm | `201 CREATED` (400 nếu dữ liệu sai) |
| `PUT` | `/api/products/{id}` | Cập nhật thông tin sản phẩm | `200 OK` |
| `DELETE` | `/api/products/{id}` | Xóa sản phẩm theo ID | `204 NO CONTENT` |

---

### 6.2. Kiểm tra Health Endpoint

```bash
curl -i http://localhost:8080/api/health
```

**Response mẫu (HTTP 200):**
```json
{
  "status": "UP",
  "appName": "docker-spring-lab",
  "javaVersion": "17.0.20.1",
  "timestamp": "2026-10-12T19:20:00.123456"
}
```

---

### 6.3. Lấy danh sách sản phẩm

```bash
curl -i http://localhost:8080/api/products
```

**Response mẫu (HTTP 200):**
```json
[
  {
    "id": 1,
    "name": "Bàn phím cơ DareU EK87",
    "description": "Bàn phím cơ tenkeyless, switch quang học, LED RGB",
    "price": 650000.0,
    "quantity": 25
  },
  {
    "id": 2,
    "name": "Chuột Logitech G102",
    "description": "Chuột chơi game cảm biến 8000 DPI, đèn LED Lightsync",
    "price": 420000.0,
    "quantity": 40
  }
]
```

---

### 6.4. Tạo sản phẩm mới (POST)

**Cấu trúc JSON Request:**
```json
{
  "name": "Tai nghe Sony WH-1000XM5",
  "description": "Tai nghe chống ồn chủ động cao cấp",
  "price": 7990000.0,
  "quantity": 10
}
```

**Lệnh cURL:**

- **Trên Linux / macOS / Git Bash:**
  ```bash
  curl -i -X POST http://localhost:8080/api/products \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Tai nghe Sony WH-1000XM5",
      "description": "Tai nghe chống ồn chủ động cao cấp",
      "price": 7990000.0,
      "quantity": 10
    }'
  ```

- **Trên Windows PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -ContentType "application/json" -Body '{"name":"Tai nghe Sony WH-1000XM5","description":"Tai nghe chống ồn chủ động cao cấp","price":7990000.0,"quantity":10}'
  ```

**Response mẫu (HTTP 201 Created):**
```json
{
  "id": 4,
  "name": "Tai nghe Sony WH-1000XM5",
  "description": "Tai nghe chống ồn chủ động cao cấp",
  "price": 7990000.0,
  "quantity": 10
}
```

---

### 6.5. Cập nhật sản phẩm (PUT)

- **Linux / macOS:**
  ```bash
  curl -i -X PUT http://localhost:8080/api/products/1 \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Bàn phím cơ DareU EK87 Pro",
      "description": "Nâng cấp switch quang học Gen 2",
      "price": 720000.0,
      "quantity": 30
    }'
  ```

---

### 6.6. Xóa sản phẩm (DELETE)

- **Linux / macOS / PowerShell:**
  ```bash
  curl -i -X DELETE http://localhost:8080/api/products/1
  ```
**Response mẫu:** `HTTP/1.1 204 No Content`

---

### 6.7. Kiểm tra Validation và Báo lỗi

Khi gửi dữ liệu không hợp lệ (ví dụ: giá âm, tên để trống):
```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"","price":-100,"quantity":-1}'
```

**Response mẫu (HTTP 400 Bad Request):**
```json
{
  "timestamp": "2026-10-12T19:25:00.000000",
  "status": 400,
  "error": "Bad Request",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "details": {
    "name": "Tên sản phẩm không được để trống",
    "price": "Giá sản phẩm không được âm",
    "quantity": "Số lượng không được âm"
  }
}
```

---

## 7. Kiểm tra Datasource Local (H2 Console)

Khi chạy ở môi trường local, ứng dụng sử dụng H2 In-Memory Database. Bạn có thể kiểm tra trực tiếp qua giao diện Web:

1. Mở trình duyệt truy cập: `http://localhost:8080/h2-console`
2. Nhập các thông tin đăng nhập:
   - **Driver Class**: `org.h2.Driver`
   - **JDBC URL**: `jdbc:h2:mem:productdb`
   - **User Name**: `sa`
   - **Password**: *(để trống)*
3. Bấm **Connect**. Bạn sẽ nhìn thấy bảng `PRODUCTS` và có thể chạy câu lệnh SQL:
   ```sql
   SELECT * FROM PRODUCTS;
   ```

---

## 8. Cơ chế cấu hình Datasource chuyển đổi môi trường (H2 sang MySQL)

Trong file `src/main/resources/application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:sa}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
```

Cú pháp `${TÊN_BIẾN:giá_trị_mặc_định}` cho phép Spring Boot:
- **Mặc định ở máy local**: Nếu không truyền biến môi trường, Spring Boot dùng `jdbc:h2:mem:productdb` với user `sa`.
- **Khi chạy qua Docker / Container**: Chúng ta truyền biến môi trường mà **không cần sửa bất kỳ dòng code nào**:
  ```bash
  SPRING_DATASOURCE_URL=jdbc:mysql://mysql-container:3306/productdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
  SPRING_DATASOURCE_USERNAME=root
  SPRING_DATASOURCE_PASSWORD=rootpassword
  ```

> **Lưu ý:** Không viết cứng `localhost` trong cấu hình khi kết nối container. Khi hai container chạy chung trong Docker Network, Spring Boot container sẽ gọi MySQL container bằng chính tên service hoặc tên container (ví dụ `mysql-container` hoặc `mysqldb`).

---

## 9. Roadmap thực hành Docker dự kiến (Các bước tiếp theo)

> **Ghi chú quan trọng:** Trong dự án hiện tại, các file `Dockerfile`, `.dockerignore`, `compose.yaml` **chưa được tạo sẵn**. Người học sẽ tự tay tạo từng file theo các bài thực hành dưới đây:

1. **Bài 1 - Single-stage Dockerfile**:
   - Sử dụng lệnh `gradlew bootJar` để build ra file `.jar`.
   - Viết `Dockerfile` cơ bản với `eclipse-temurin:17-jre`.
   - Build image: `docker build -t docker-spring-lab:1.0 .`
   - Chạy container: `docker run -d -p 8080:8080 --name spring-app docker-spring-lab:1.0`
2. **Bài 2 - Multi-stage Build & Caching**:
   - Viết `Dockerfile` gồm 2 stage: `builder` (dùng JDK và Gradle wrapper) và `runner` (dùng JRE siêu nhẹ).
   - Tối ưu thứ tự `COPY` để cache các layer dependencies.
3. **Bài 3 - Docker Networking & MySQL Container**:
   - Tạo custom network: `docker network create spring-net`.
   - Chạy MySQL container: `docker run -d --name mysql-db --network spring-net -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=productdb mysql:8.0`.
   - Chạy Spring Boot container kết nối sang `mysql-db` qua biến môi trường.
4. **Bài 4 - Docker Volumes**:
   - Gắn volume `-v mysql_data:/var/lib/mysql` cho MySQL container để bảo toàn dữ liệu khi xóa container.
5. **Bài 5 - Docker Compose**:
   - Viết `compose.yaml` định nghĩa cả hai service `backend` và `database`, cấu hình `depends_on`, `volumes`, `networks` và biến môi trường.
   - Khởi động toàn bộ hệ thống bằng `docker compose up -d`.
