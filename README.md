# ContactHub — Contact Management System

A full-stack contact management application built with **Spring Boot 3** on the backend and **React + Vite** on the frontend. Features JWT authentication, a beautiful glassmorphism dark-mode UI, and full CRUD for contacts with paginated search.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 25, Spring Boot 3.3.5, Spring Security, Spring Data JPA |
| Database | SQL Server (production), H2 (dev/test) |
| Auth | JWT (JJWT 0.12.6) |
| Frontend | React 18, Vite 5, React Router 6, Axios |
| Code Gen | Lombok 1.18.46 |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Analysis | SonarQube (via Maven plugin) |

---

## 📁 Project Structure

```
contact-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/contactmanager/contact_management_system/
│   │   │   ├── controller/        # AuthController, ContactController, UserController
│   │   │   ├── service/           # AuthService, ContactService, UserService
│   │   │   ├── repository/        # JPA repositories
│   │   │   ├── entity/            # JPA entities (User, Contact, ContactEmail, ContactPhone)
│   │   │   ├── dto/               # Request/Response DTOs
│   │   │   ├── security/          # JWT filter, token provider, security config
│   │   │   └── exception/         # Global exception handler
│   │   └── resources/
│   │       ├── application.yml    # SQL Server config (production)
│   │       └── application-h2.yml # H2 file database (dev)
│   └── test/
│       └── java/.../
│           ├── controller/        # MockMvc integration tests
│           ├── service/           # Mockito unit tests
│           └── repository/        # DataJPA slice tests (H2)
├── frontend/
│   ├── src/
│   │   ├── pages/                 # LoginPage, RegisterPage, ContactsPage, ProfilePage
│   │   ├── components/            # Navbar, SearchBar, Pagination, ContactCard
│   │   ├── components/modals/     # ContactForm, Create/Update/Delete/ChangePassword modals
│   │   ├── api/                   # axiosConfig, authApi, contactApi, userApi
│   │   └── context/               # AuthContext (JWT + user state)
│   ├── index.html
│   └── vite.config.js             # API proxy → :8080
├── pom.xml
└── sonar-project.properties
```

---

## ⚡ Quick Start

### Prerequisites

- **Java 25** (JDK)
- **Maven** (or use the included `mvnw` wrapper)
- **Node.js 18+** and npm
- **SQL Server** running locally (or use H2 dev profile)

### 1. Backend

**With SQL Server** (default profile):

Update credentials in `src/main/resources/application.yml` if needed, then:

```powershell
# Set JAVA_HOME if not set system-wide
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
.\mvnw.cmd spring-boot:run
```

**With H2 in-memory DB** (no SQL Server required):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=h2"
```

The backend runs on **http://localhost:8080**

### 2. Frontend

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs on **http://localhost:5173** and proxies `/api` requests to the backend.

---

## 🧪 Running Tests

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
.\mvnw.cmd test "-Dspring.profiles.active=test"
```

Tests use H2 in-memory database (no external dependencies required).

**Test Coverage:**
- `AuthServiceTest` — 7 unit tests for register, login, change-password
- `ContactServiceTest` — 8 unit tests for all CRUD operations
- `UserServiceTest` — 3 unit tests for profile retrieval
- `AuthControllerTest` — 4 MockMvc integration tests
- `ContactControllerTest` — 5 MockMvc integration tests
- `ContactRepositoryTest` — 5 DataJPA slice tests
- `ContactManagementSystemApplicationTests` — Spring context smoke test

---

## 🔌 API Endpoints

### Auth (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |
| POST | `/api/auth/change-password` | Change password | Yes |

### Contacts (`/api/contacts`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/contacts` | List contacts (paginated, searchable) | Yes |
| GET | `/api/contacts/{id}` | Get contact by ID | Yes |
| POST | `/api/contacts` | Create a new contact | Yes |
| PUT | `/api/contacts/{id}` | Update a contact | Yes |
| DELETE | `/api/contacts/{id}` | Delete a contact | Yes |

### Users (`/api/users`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users/me` | Get current user profile | Yes |

**Query params for GET /api/contacts:**
- `page` — zero-based page number (default: 0)
- `size` — page size (default: 10)
- `search` — filter by first/last name (case-insensitive)

---

## 🗄️ Database Schema

```
users
  id (PK), firstName, lastName, email (UNIQUE), phoneNumber (UNIQUE),
  passwordHash, role (USER/ADMIN), createdAt

contacts
  id (PK), firstName, lastName, title, owner_id (FK → users)

contact_emails
  id (PK), email, label (PERSONAL/WORK/OTHER), contact_id (FK → contacts)

contact_phones
  id (PK), phoneNumber, label (MOBILE/HOME/WORK/OTHER), contact_id (FK → contacts)
```

---

## 🔒 Security

- Stateless JWT-based authentication (no sessions)
- BCrypt password hashing
- All `/api/contacts/**` and `/api/users/**` endpoints require a valid `Authorization: Bearer <token>` header
- Contacts are user-scoped — users can only access their own contacts

---

## 📊 SonarQube Analysis

Requires a running SonarQube instance (default: `http://localhost:9000`).

1. Set your token in `sonar-project.properties` (`sonar.token=<your-token>`)
2. Run: `.\mvnw.cmd sonar:sonar`

---

## 🏗️ Development Notes

- Frontend uses Vite's dev proxy so all `/api/*` requests are forwarded to `:8080`
- The H2 console is available at `http://localhost:8080/h2-console` when using the `h2` profile
- JWT token expiration is 24 hours (configurable via `app.jwt.expiration` in `application.yml`)
