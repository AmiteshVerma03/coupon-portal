# Coupon Portal System

A scalable backend system for managing and redeeming coupons with secure authentication, role-based access control, and performance-tested APIs.

---

## 🔥 Key Features

- JWT-based Authentication with Refresh Tokens
- Role-Based Access Control (Admin, Manager, User)
- Coupon Creation, Validation & Redemption APIs
- Multi-tenant architecture support
- Rate Limiting and Global Exception Handling
- Performance Testing using Apache JMeter
- API Testing and Workflow Validation using Postman

---

## 🏗️ Tech Stack

- Backend: Java, Spring Boot
- Security: Spring Security, JWT
- Database: MySQL
- Testing: Postman, Apache JMeter
- Build Tool: Maven

---

## ⚙️ System Design Overview

- RESTful API architecture for modular and scalable services  
- Layered architecture (Controller → Service → Repository)  
- Secure endpoints using JWT filters and role-based authorization  
- Centralized exception handling and validation  

---

## 📊 Performance Testing

- Conducted load testing using Apache JMeter  
- Simulated 1000+ requests with concurrent users  
- Achieved:
  - ~18 requests/sec throughput  
  - 0% error rate  
  - Average response time: ~8 seconds under load  

### Insights:
- Identified performance bottlenecks in database queries  
- Observed increased latency under high concurrency  
- Future optimization: caching (Redis) and query tuning  

---

## 🔐 Authentication Flow

1. User logs in via `/api/auth/login`
2. Server validates credentials and returns JWT token
3. Token is used to access protected endpoints
4. Role-based authorization controls access to APIs

---

## 📂 API Endpoints (Sample)

| Method | Endpoint                | Description              |
|--------|------------------------|--------------------------|
| POST   | /api/auth/login        | User login               |
| POST   | /api/coupons           | Create coupon (Admin)    |
| GET    | /api/coupons           | Get all coupons          |
| POST   | /api/coupons/redeem    | Redeem coupon            |

---

## 🧪 Testing

### Postman
- Used for API validation and authentication flow testing  
- Tested protected routes and role-based access  

### JMeter
- Used for load and stress testing  
- Analyzed system behavior under concurrent requests  

---

## 🚧 Future Improvements

- Redis caching to reduce response time  
- Deployment using Docker & cloud platforms (AWS/Render)  
- Real-time notifications (Email/In-App)  
- Enhanced analytics dashboard for admins  

---

## ▶️ How to Run Locally

```bash
# Clone the repository
git clone <your-repo-link>

# Navigate to project
cd coupon-portal

# Configure database in application.properties

# Run the application
mvn spring-boot:run
