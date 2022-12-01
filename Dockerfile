FROM openjdk:11
EXPOSE 8080
EXPOSE 27017
ARG JAR_FILE=target/repay-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://mongo:27017/test", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]