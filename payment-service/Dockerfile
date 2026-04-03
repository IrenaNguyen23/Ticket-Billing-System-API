FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
ADD https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip /tmp/newrelic-java.zip
RUN mkdir -p /opt/newrelic \
  && cd /opt/newrelic \
  && jar xf /tmp/newrelic-java.zip \
  && rm /tmp/newrelic-java.zip \
  && if [ -d /opt/newrelic/newrelic ]; then mv /opt/newrelic/newrelic/* /opt/newrelic/ && rmdir /opt/newrelic/newrelic; fi
RUN gradle :payment-service:bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/payment-service/build/libs/*.jar /app/app.jar
COPY --from=build /opt/newrelic /opt/newrelic
ENV JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -javaagent:/opt/newrelic/newrelic.jar"
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
