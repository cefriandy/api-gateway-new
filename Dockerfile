FROM openjdk:17

ADD target/api-gateway-new.jar api-gateway-new.jar

ENTRYPOINT ["java", "-jar", "/api-gateway-new.jar"]