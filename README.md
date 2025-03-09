# **Load Balancer**

## **Overview**

This project implements a simple yet functional **Load Balancer** using **Java and Spring Boot**. It distributes HTTP requests among backend servers using multiple load-balancing strategies:

- **Round-Robin (Default)**
- **Random Selection**
- **Least Connections**

The system also includes:

- **Health Check Mechanism** to remove unhealthy servers.
- **Dynamic Backend Server Registration and Removal**.
- **Thread-Safe and Concurrent Request Handling**.

## **Features**

- **Load Balancing Algorithms**: Supports Round-Robin, Random Selection, and Least Connections.
- **Health Check**: Periodically verifies backend server availability.
- **Dynamic Server Management**: Servers can be added or removed at runtime.
- **Thread-Safe Implementation**: Ensures efficient concurrent request handling.

## **Technologies Used**

- **Java 17**
- **Spring Boot**
- **RestTemplate** (for HTTP request forwarding and health checks)
- **Scheduled Tasks** (for periodic health checks)
- **Atomic Variables & Concurrent Collections** (for thread-safe operations)

## **Setup Instructions**

### **1\. Prerequisites**

- Install **Java 17**
- Install **Maven**
- Install **Docker** (optional, for running backend services)

### **2\. Clone the Repository**

git clone <https://github.com/your-repo/load-balancer.git>

cd load-balancer

### **3\. Build and Run the Application**

mvn clean install

mvn spring-boot:run

The Load Balancer will start and listen for HTTP requests.

## **Usage**

### **1\. Registering Backend Servers**

You can configure backend servers in the application or dynamically add/remove them using an API (to be implemented in future versions).

### **2\. Changing Load Balancing Algorithm**

Modify the strategy dynamically:

LoadBalancer.setStrategy(new RandomSelectionStrategy());

### **3\. Health Check Mechanism**

- The health check runs **every 10 seconds**.
- If a server fails to respond to a GET /health request, it is removed from the list.
- Once healthy again, it must be **manually re-registered**.

## **High-Level Architecture**

<code>
Client Request

	│
	▼

Load Balancer (Spring Boot)
</code>
>	├── Round-Robin Strategy

>	├── Random Selection Strategy

>	├── Least Connections Strategy

>	│

>	▼

>Backend Server Pool

## **Future Enhancements**

- **API to dynamically add/remove backend servers**.
- **WebSocket-based real-time monitoring**.
- **Support for weighted load balancing**.

## **Contributing**

Feel free to submit issues and pull requests to improve the project!

## **License**

MIT License