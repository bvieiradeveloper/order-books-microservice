FROM openjdk:17-alpine
LABEL authors="bvieira"
COPY build/libs/*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]