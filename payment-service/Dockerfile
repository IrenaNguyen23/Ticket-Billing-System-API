FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :payment-service:bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/payment-service/build/libs/*.jar /app/app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
