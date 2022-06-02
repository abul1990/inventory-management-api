FROM openjdk:16-alpine3.13

RUN addgroup -S spring && adduser -S springuser -G spring
USER springuser
WORKDIR /home/springuser

ARG JAR_FILE=build/libs/inventory-management-0.0.1-SNAPSHOT.jar

COPY ./build/libs/inventory-management-0.0.1-SNAPSHOT.jar inventory-management-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "./inventory-management-0.0.1-SNAPSHOT.jar"]
