# Food-Delivery-Fujitsu
Spring Boot based food delivery backend application developed as a trial task for Java Developer position at Fujitsu.

This document provides an overview of the Food Delivery Fee Calculator project, including its tech stack and setup instructions.

## Overview 

This is a Spring Boot backend application that calculates the delivery fee for couriers based on regional base fees, vehicle type, and current weather conditions.
The project consists of:
- Database for storing weather data and delivery fees
- A scheduled task (Cron Job) to import weather data from ilmateenistus.ee
- Fee calculation logic based on business rules
- REST API for requesting delivery fees and managing the business rules

## Description

The goal of this project is to implement a backend service that calculates the delivery fee based on station-based fees and weather conditions.

The system fetches real-time weather data, enforces business rules for different vehicle types (CAR, SCOOTER, BIKE), and provides a REST API for querying and managing fees.

## Delivery fee calculation and hard-coded rules
### Fees:
- Base fee
- Temperature below -10°C
- Temperature between -10°C and 0°C
- Wind speed between 10m/s - 20m/s
- Weather phenomenon related to rain
- Weather phenomenon related to snow/sleet

All of the fees can be managed via API.

### Hard-coded rules

If the wind speed is over 20m/s then vehicle type BIKE is forbidden.

If the weather phenomenon includes glaze, hail or thunder then vehicle types BIKE and SCOOTER are forbidden.

## Technologies Used
### Backend
- Java 21
- Spring Boot 3.4.3
- Spring Web, Spring Data JPA
- H2 Database
- Liquibase
- Swagger
- SLF4J
- Lombok
- Gradle

## H2 Database
When running the application, the H2 database is configured to store its data in a file inside the `/data` directory. 
This is specified in the application.properties:
`spring.datasource.url=jdbc:h2:file:./data/food_delivery_db`

The H2 database console will be available at: `http://localhost:8080/h2-console`

### Database schema
![DatabaseSchema](https://github.com/user-attachments/assets/0e013711-1895-4bd0-9017-1aafc8f9311b)

## Cron job for updated weather data
When the application is running a cron job is set up to fetch weather data hourly at 15 minutes past the hour.

Fetches weather data from the Estonian Environment Agency API: `https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php`

Inserts new weather data into the database.

## Setup Instructions
### Backend (Spring Boot + H2 Database)
### Prerequisites
- Java 21
- Gradle

Clone the repository:

```
git clone https://github.com/henrygrunberg/Food-Delivery-Fujitsu.git
cd Food-Delivery-Fujitsu
```

Make sure that application properties in src/main/resources/application.properties are correct:

```
spring.application.name=Food-Delivery-Fujitsu
spring.datasource.url=jdbc:h2:file:./data/food_delivery_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.liquibase.change-log=classpath:/db/changelog/changelog-master.xml

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.hibernate.ddl-auto=none

weather.import.cron=0 15 * * * *
```

Build and run the backend:

```
./gradlew clean build
java -jar build/libs/food-delivery-fujitsu.jar
```

Once the application starts, the API will be available at: `http://localhost:8080`

## API Overview 

The API documentation is available via Swagger. All endpoints and DTOs can be found at:

`http://localhost:8080/swagger-ui/index.html`

## Tests

The project includes unit tests (using JUnit and Mockito) to verify the business logic in services and integration tests 
(using Spring Boot Test) to ensure that API endpoints work correctly with the database.

The tests cover fee calculations, weather data processing, and API responses with a coverage of more than 90%. 

All tests can be run using `./gradlew test`.







