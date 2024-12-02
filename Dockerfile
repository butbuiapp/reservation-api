FROM amazoncorretto:20
WORKDIR /app
EXPOSE 8080
COPY target/nail-shop-api-1.0.1.jar /app
ENTRYPOINT ["java", "-jar", "nail-shop-api-1.0.1.jar"]