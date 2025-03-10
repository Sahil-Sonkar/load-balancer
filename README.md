# Load Balancer

## Overview
This project implements a **Load Balancer** using **Java and Spring Boot**. It distributes HTTP requests among backend servers using multiple load-balancing strategies:
* **Round-Robin (Default)**
* **Random Selection**
* **Least Connections**

The system also includes:
* **Health Check Mechanism** to monitor and remove unhealthy servers.
* **Dynamic Backend Server Registration and Removal**.
* **Thread-Safe and Concurrent Request Handling**.

## Features
* **Load Balancing Algorithms**: Supports Round-Robin, Random Selection, and Least Connections.
* **Health Check**: Periodically verifies backend server availability and removes unhealthy servers.
* **Dynamic Server Management**: Servers can be added or removed at runtime via API calls.
* **Thread-Safe Implementation**: Uses concurrent collections and atomic variables for efficient concurrent request handling.

## Technologies Used
* **Java 21**
* **Spring Boot**
* **Spring Web** (for API endpoints)
* **RestTemplate** (for HTTP request forwarding and health checks)
* **Scheduled Tasks** (for periodic health checks)
* **Atomic Variables & Concurrent Collections** (for thread-safe operations)

## Setup Instructions

### 1. Prerequisites
* Install **Java 21**
* Install **Maven**
* Install **Docker** (optional, for running backend services)

### 2. Clone the Repository

```sh
git clone https://github.com/your-repo/load-balancer.git
cd load-balancer
```

### 3. Build and Run the Application

```sh
mvn clean install
mvn spring-boot:run
```

The Load Balancer will start and listen for HTTP requests on **port 8080**.

## Usage

### 1. Registering Backend Servers
You can dynamically add backend servers using the following API:

```sh
POST /api/servers/register
{
  "serverUrl": "http://localhost:8081"
}
```

To remove a server:

```sh
DELETE /api/servers/remove
{
  "serverUrl": "http://localhost:8081"
}
```

### 2. Changing Load Balancing Algorithm
Modify the strategy dynamically using the API:

```sh
POST /api/strategy/change
{
  "strategy": "randomselection"
}
```

To check the current strategy:

```sh
GET /api/strategy/current
```

### 3. Health Check Mechanism
* The health check runs **every 10 seconds**.
* If a server fails to respond to a `GET /health` request, it is marked as **unhealthy**.
* Once healthy again, it must be **manually re-registered** using the registration API.

## High-Level Architecture

```plaintext
Client Request
    │
    ▼
Load Balancer (Spring Boot)
    ├── Round-Robin Strategy
    ├── Random Selection Strategy
    ├── Least Connections Strategy
    │
    ▼
Backend Server Pool
```

## Future Enhancements
* **WebSocket-based real-time monitoring of load balancing decisions**.
* **Support for weighted load balancing**.
* **Integration with Kubernetes for automated scaling**.

## Contributing
Feel free to submit issues and pull requests to improve the project!

## License
This project is licensed under the **MIT License**.
