FROM openjdk:8
ADD bmyr.jar bmyr.jar
ENTRYPOINT ["java", "-jar", "/bmyr.jar"]
