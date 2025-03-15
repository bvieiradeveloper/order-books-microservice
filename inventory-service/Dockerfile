FROM openjdk:17-alpine
LABEL authors="bvieira"
COPY build/libs/*.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"]