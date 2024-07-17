FROM openjdk:17

WORKDIR /jinkshower

ARG JAR_FILE=/build/libs/*.jar

COPY ${JAR_FILE} /jinkshower/jinkshower.jar

ENTRYPOINT ["java", "-jar", "jinkshower.jar"]
