FROM eclipse-temurin:17-jdk

WORKDIR /app

# Копируем JAR в рабочую директорию
COPY target/Botfoure-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]


