# ContactHub — Contact Management System

A full-stack contact management application built with **Spring Boot 3** on the backend and **React + Vite** on the frontend. Features JWT authentication, a beautiful glassmorphism dark-mode UI, and full CRUD for contacts with paginated search.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21/25, Spring Boot 3.3.5, Spring Security, Spring Data JPA |
| Database | PostgreSQL (production), H2 (dev/test) |
| Auth | JWT (JJWT 0.12.6) |
| Frontend | React 18, Vite 5, React Router 6, Axios |
| Testing | JUnit 5, Mockito, JaCoCo Code Coverage (>80%) |

---

## 📁 Project Structure

The project has been separated into two independent folders for clean code organization:

```
contact-management-system/
├── backend/               # Spring Boot Application (Java)
│   ├── src/
│   ├── pom.xml
│   └── mvnw
├── frontend/              # React + Vite Application (JavaScript)
│   ├── src/
│   ├── package.json
│   └── vite.config.js
└── README.md
```

---

## ⚡ Quick Start

### Prerequisites

- **Java 21 or 25** (JDK)
- **Node.js 18+** and npm
- **PostgreSQL** running locally (or use H2 dev profile)

### 1. Backend Setup

The backend connects to PostgreSQL by default. To securely connect to your database, you must set environment variables before running the application:

```powershell
cd backend

# Set your Java Environment (if not configured globally)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"

# Set Database Credentials & JWT Secret
$env:DB_PASSWORD = "your_postgres_password"
$env:JWT_SECRET = "YourSuperSecretKeyUsedForJWTGeneration256Bit"

# Start the application
.\mvnw.cmd spring-boot:run
```

**With H2 in-memory DB** (no PostgreSQL required, no password needed):
```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=h2"
```

The backend runs on **http://localhost:8080**

### 2. Frontend Setup

Open a new terminal and run:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs on **http://localhost:5173** and proxies `/api` requests to the backend.

---

## 🧪 Running Tests & Code Coverage

The backend is fully tested with over 80% code coverage using JaCoCo.

```powershell
cd backend
.\mvnw.cmd clean test
```

A code coverage report will be generated in `backend/target/site/jacoco/index.html`.

---

## 🔌 API Endpoints

### Auth (`/api/auth`)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT
- `POST /api/auth/change-password` - Change password (Requires Auth)

### Contacts (`/api/contacts`)
- `GET /api/contacts` - List contacts (paginated, searchable)
- `GET /api/contacts/{id}` - Get contact by ID
- `POST /api/contacts` - Create a new contact
- `PUT /api/contacts/{id}` - Update a contact
- `DELETE /api/contacts/{id}` - Delete a contact
- `GET /api/contacts/export` - Export contacts to CSV
- `POST /api/contacts/import` - Import contacts from CSV

### Users (`/api/users`)
- `GET /api/users/me` - Get current user profile

---

## 🔒 Security Enhancements
- Passwords are hashed using BCrypt.
- Stateless JWT-based authentication (no sessions).
- JWT Secrets and Database Credentials are now injected via environment variables to prevent hardcoded secrets.
- Contacts are strictly user-scoped.
