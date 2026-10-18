# =========================================
# STAGE 1: BUILD APPLICATION
# =========================================
FROM gradle:8.7-jdk17-alpine AS builder

USER root
WORKDIR /workspace
RUN chown -R gradle:gradle /workspace
USER gradle

# Lưu cache Gradle trong filesystem của stage build
ENV GRADLE_USER_HOME=/workspace/.gradle-cache

# Copy file cấu hình trước để tận dụng layer cache
COPY --chown=gradle:gradle build.gradle settings.gradle ./

# Tải và phân giải dependencies
RUN gradle dependencies --no-daemon

# Copy source code sau khi dependencies đã được xử lý
COPY --chown=gradle:gradle src ./src

# Chạy test và build file JAR bên trong Docker
RUN gradle test bootJar --no-daemon

# =========================================
# STAGE 2: RUNTIME APPLICATION
# =========================================
FROM eclipse-temurin:17-jre-noble AS runtime

WORKDIR /app

# Tạo user không có quyền root để chạy ứng dụng
RUN useradd --system --uid 10001 --create-home appuser

# Chỉ lấy JAR từ stage builder
COPY --from=builder --chown=appuser:appuser /workspace/build/libs/docker-spring-lab-0.0.1-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]



# # 1. Sử dụng Java 17 runtime image làm nền
# FROM eclipse-temurin:17-jre-noble

# # 2. Thiết lập thư mục làm việc bên trong container
# WORKDIR /app

# # 3. Sao chép file JAR từ máy host vào image
# COPY build/libs/docker-spring-lab-0.0.1-SNAPSHOT.jar app.jar

# # 4. Khai báo cổng mà ứng dụng sử dụng
# EXPOSE 8080

# # 5. Lệnh khởi chạy ứng dụng khi container bắt đầu
# ENTRYPOINT [ "java", "-jar", "app.jar" ]