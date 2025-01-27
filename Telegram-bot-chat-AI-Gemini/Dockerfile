# Sử dụng Maven để build
FROM maven:3.9.0-eclipse-temurin-17 AS build

# Thiết lập thư mục làm việc
WORKDIR /build

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng (tạo file JAR)
RUN mvn clean package -DskipTests

# Tạo image từ file JAR
FROM openjdk:17-jdk-slim

WORKDIR /app

# Sao chép file JAR từ giai đoạn build
COPY --from=build /build/target/Telegram-bot-chat-AI-Gemini-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
