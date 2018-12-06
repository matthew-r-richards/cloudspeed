FROM openjdk:8-jdk
ADD ./target/springboot-1.2-SNAPSHOT.jar springboot-1.2-SNAPSHOT.jar
CMD ["java", "-jar", "springboot-1.2-SNAPSHOT.jar"]