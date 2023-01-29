FROM openjdk:8
ADD target/bmyr.jar bmyr.jar
ENTRYPOINT ["java", "-jar", "/bmyr.jar"]
