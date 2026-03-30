# Multi-Tenant Microservice Ecosystem 🚀

A production-grade microservices architecture built with **Java 17**, **Spring Boot 3**, and **Kubernetes**, featuring a sophisticated **Schema-per-Tenant** isolation strategy.

## 🏗️ System Architecture
The project follows a distributed architecture where each service handles a specific business domain. Traffic is managed by a centralized API Gateway that handles security and tenant identification.



### Core Services
* **API Gateway (Port 8081):** Entry point. Performs JWT validation and injects `X-Tenant-ID` headers.
* **Auth-Service (Port 8089):** Identity Provider. Manages User Registration, Login, and JWT issuance.
* **User-Service (Port 8087):** Business Logic. Manages user profiles within isolated database schemas.
* **Notification-Service (Port 8091):** Communication. Handles email alerts via asynchronous triggers.
* **Command-Lib:** Shared library containing Multi-tenancy configurations and `TenantContext` logic.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3.x, Spring Cloud.
* **Database:** PostgreSQL (Multi-tenant Schema isolation).
* **Security:** JWT (JSON Web Tokens).
* **Discovery:** HashiCorp Consul.
* **Infrastructure:** Docker, Docker Compose, Kubernetes (Minikube).

## 🏢 Multi-Tenancy Implementation
This project implements **Data Isolation** at the Database level.
1.  **Intercept:** The `TenantInterceptor` catches the `X-Tenant-ID` header.
2.  **Context:** The ID is stored in a `ThreadLocal` `TenantContext`.
3.  **Switch:** The `MultiTenantConnectionProvider` executes `SET SCHEMA 'tenant_id'` before every Hibernate transaction.



## 🚀 Getting Started (Kubernetes)
To deploy the ecosystem to a local Kubernetes cluster:

1.  **Start Cluster:** `minikube start`
2.  **Set Environment:** `eval $(minikube docker-env)`
3.  **Build Images:** `docker build -t auth-service:latest ./auth-service` (Repeat for all)
4.  **Deploy:** `kubectl apply -f k8s/`

## 📬 API Endpoints
| Service | Endpoint | Method | Description |
| :--- | :--- | :--- | :--- |
| Auth | `/auth/register` | POST | Global User Registration |
| Auth | `/auth/token` | POST | Login & get JWT |
| User | `/users/profile` | GET | Fetch Tenant-specific profile |