FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :api-gateway:bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/api-gateway/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
