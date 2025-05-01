# ProgwebApplication - Password Manager

A secure password management application built using Spring Boot.

## Features

- Store and manage passwords securely
- Generate strong random passwords
- Check password strength
- Track password expiration
- Modern, responsive UI with cybersecurity theming

## Technologies Used

- Java 11
- Spring Boot 2.7.8
- Thymeleaf
- HTML5/CSS3/JavaScript
- Docker
- Kubernetes

## Getting Started

### Prerequisites

- Java 11+
- Maven
- Docker
- Kubernetes (Minikube for local testing)

### Running Locally

1. Clone the repository
2. Build the application:
   mvn clean package
3. Run the application:

mvn spring-boot:run 4. Access the application at http://localhost:8080

### Docker Build & Run

1. Build Docker image:

docker build -t yourusername/progweb-password-manager:latest . 2. Run Docker container:

docker run -p 8080:8080 yourusername/progweb-password-manager:latest

### Kubernetes Deployment

1. Make sure Minikube is running:

minikube start 2. Deploy to Kubernetes:

kubectl apply -f kubernetes/deployment.yaml kubectl apply -f kubernetes/service.yaml 3. Access the service:

minikube service progweb-password-manager-service --url

## Project Structure

The project follows standard Spring Boot architecture:

- `controller`: Handles HTTP requests
- `model`: Contains data models
- `service`: Business logic
- `util`: Utility classes
- `resources`: Static resources and templates

## Security Features

- Password strength analysis
- Secure password generation
- Expiration tracking
- Modern UI with cybersecurity focus

## Screenshots

[Include screenshots here]
