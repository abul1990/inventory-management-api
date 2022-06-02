FROM openjdk:11


WORKDIR /home

ARG JAR_FILE=build/libs/inventory-management-0.0.1-SNAPSHOT.jar

RUN ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
