FROM amazoncorretto:20
WORKDIR /app
EXPOSE 8888
COPY target/reservation-management-1.0.1.jar /app
ENTRYPOINT ["java", "-jar", "reservation-management-1.0.1.jar"]