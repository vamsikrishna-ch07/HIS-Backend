# HIS-Backend
Health Insurance System – Backend Microservices

## Project Overview
HIS-Backend is an enterprise-grade backend system designed to manage the complete lifecycle of government health insurance programs such as Medicaid or Medicare.

The system supports:
- Insurance plan management
- Citizen application registration
- Eligibility determination
- Notification and correspondence
- Benefit issuance
- Reporting and analytics

## Architecture
- Architecture Style: Event-Driven Microservices
- Deployment: Docker containers using Docker Compose

### Communication
- Synchronous: REST APIs using OpenFeign and WebClient
- Asynchronous: Apache Kafka

### Security
- OAuth2 and OpenID Connect
- Keycloak for authentication and authorization
- Role-Based Access Control (RBAC)

## Infrastructure Services

### Service Registry (Eureka)
All services register with Eureka and communicate using service names instead of hardcoded IP addresses.

### Config Server
Centralized configuration management for all microservices.

### API Gateway (Spring Cloud Gateway)
Responsibilities:
- Routing requests to appropriate services
- JWT token validation
- Circuit breakers and rate limiting
- Swagger documentation aggregation

## Business Services

### Admin API (his-admin-api)
Used by government administrators to create and manage insurance plans.

Tech Stack:
- Spring Boot
- Spring MVC
- JPA
- H2 Database

### Application Registration Service (his-ar-api)
Allows citizens or caseworkers to submit insurance applications.

Tech Stack:
- Spring Boot
- Spring MVC
- JPA

### Data Collection Service (his-dc-api)
Collects income, education, and family details for eligibility processing.

Tech Stack:
- Spring Boot
- Spring MVC
- JPA

### Eligibility Determination Service (his-ed-api)
Core service that determines approval or denial of applications.

Responsibilities:
- Fetches data using Feign clients
- Applies eligibility rules
- Publishes results to Kafka

Tech Stack:
- Spring Boot
- OpenFeign
- Kafka Producer

### Correspondence Service (his-co-api)
Generates approval or denial notices.

Behavior:
- Consumes Kafka events
- Generates PDF documents

Tech Stack:
- Spring WebFlux
- Kafka Consumer

### Benefit Issuance Service (his-bi-api)
Processes benefit payments in bulk using scheduled jobs.

Tech Stack:
- Spring Batch

### Reports Service (his-reports-api)
Generates Excel and PDF reports for management.

Tech Stack:
- Spring WebFlux

## End-to-End Flow
1. Admin creates insurance plans.
2. Citizen submits an application.
3. Data is collected for eligibility.
4. Eligibility is determined and published to Kafka.
5. Correspondence service generates notices.
6. Benefit issuance processes payments.

## Security Implementation
- OAuth2/OIDC with Keycloak
- JWT-based authentication
- Custom role mapping
- Method-level authorization using @PreAuthorize

## Resilience and Scalability
- Resilience4j for circuit breakers and fallbacks
- Kafka for decoupled asynchronous processing
- WebFlux for high concurrency

## Observability
- Spring Boot Actuator
- Eureka health monitoring
- Gateway-based routing control

## Summary
This project demonstrates enterprise-grade backend development using modern Java, microservices, event-driven architecture, and OAuth2 security.


=====
/*
* “Keycloak issues JWT tokens with roles inside the realm_access.roles claim.
  Spring Security doesn’t recognize them by default, so we implemented a custom JwtGrantedAuthoritiesConverter to map Keycloak roles into Spring Security authorities with the ROLE_ prefix.
  This converter is plugged into the SecurityFilterChain, enabling role-based access control using @PreAuthorize and URL-level security across all microservices
* */


========
docker exec -it keycloak curl -X POST \
http://keycloak:8080/realms/master/protocol/openid-connect/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "client_id=his-client" \
-d "client_secret=4OlIlR3h3dgG0gsUSXThukU6f5TDhPU4" \
-d "grant_type=client_credentials"


