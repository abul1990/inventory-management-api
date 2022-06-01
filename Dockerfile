FROM openjdk:16-alpine3.13

EXPOSE 8080

WORKDIR /app

COPY /home/runner/work/inventory-management-api/inventory-management-api/build/libs/inventory-management-0.0.1-SNAPSHOT.jar inventory-management-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "./inventory-management-0.0.1-SNAPSHOT.jar"]
