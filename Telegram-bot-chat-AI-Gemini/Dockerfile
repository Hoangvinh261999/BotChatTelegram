# Giai đoạn 1: Build ứng dụng
FROM maven:3.9.0-eclipse-temurin-17-slim AS build

# Thiết lập thư mục làm việc
WORKDIR /build

# Chỉ sao chép các tệp cần thiết trước để tối ưu cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Sao chép toàn bộ mã nguồn (sau khi dependency đã cache)
COPY src ./src

# Build ứng dụng (tạo file JAR)
RUN mvn clean package -DskipTests && \
    rm -rf ~/.m2/repository

# Giai đoạn 2: Image runtime
FROM eclipse-temurin:17-jre-alpine

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ giai đoạn build
COPY --from=build /build/target/Telegram-bot-chat-AI-Gemini-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
