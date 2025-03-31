# 使用 OpenJDK 17 作為基底映像
FROM openjdk:17-jdk-slim

# 設定工作目錄
WORKDIR /app

# 複製 Spring Boot JAR 檔到容器內
COPY target/eeit195_01_back-0.0.1-SNAPSHOT.jar app.jar

# 設定容器啟動時執行的指令
CMD ["java", "-jar", "app.jar"]

# Expose 8080 port
EXPOSE 8080