FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :trip-service:bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/trip-service/build/libs/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
