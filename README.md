# ⚽ Turf-Ballers

> **Production-ready Sports Turf Booking & Management System**

A full-stack web application for managing sports turf bookings, members, payments, and attendance — built with Spring Boot 3 + Vanilla JS.

![Dashboard Preview](frontend/css/preview.png)

---

## 🚀 Live Demo

| Layer | URL |
|---|---|
| 🌐 Frontend | [turfballers1.vercel.app](https://turfballers1.vercel.app) |
| ⚙️ Backend API | [turfballers-api-v2.onrender.com](https://turfballers-api-v2.onrender.com) |

**Authentication:** Register a new account or Sign In directly on the website. Default seeded admin credentials: `admin@turfballers.com` / `Admin@123`

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure login with BCrypt password hashing
- 👥 **Member Management** — Add, edit, filter by plan (Basic / Standard / VIP)
- 📅 **Smart Bookings** — Slot booking with **double-booking prevention**
- 💳 **Payment Tracking** — Cash, UPI, Card, Net Banking
- ✅ **Attendance** — Daily check-in/out with rate analytics
- 📊 **Live Dashboard** — KPIs, revenue trend chart, membership breakdown donut
- 🌱 **Auto-seeded Demo Data** — Admin user + 5 members + bookings + payments on first run

---

## 🛠️ Tech Stack

### Backend
- **Java 21** + **Spring Boot 3.2**
- Spring Security (Stateless JWT)
- Spring Data JPA + Hibernate
- MySQL 8
- Lombok, MapStruct

### Frontend
- Vanilla HTML5 / CSS3 / JavaScript
- Chart.js (revenue trend, donut)
- Dark neon design system
- Deployed on **Vercel**

---

## 📁 Project Structure

```
turfballers/
├── backend/                  # Spring Boot application
│   ├── src/main/java/com/turfballers/backend/
│   │   ├── config/           # Security, JWT, CORS, DataInitializer
│   │   ├── controller/       # REST controllers (6 modules)
│   │   ├── service/          # Business logic
│   │   ├── repository/       # JPA repositories
│   │   ├── model/            # JPA entities
│   │   ├── dto/              # Request/Response DTOs
│   │   └── exception/        # Global exception handling
│   └── src/main/resources/
│       └── application.properties
├── frontend/                 # Static frontend
│   ├── index.html            # Dashboard
│   ├── login.html
│   ├── members.html
│   ├── bookings.html
│   ├── payments.html
│   ├── attendance.html
│   ├── css/styles.css
│   └── js/api.js
└── vercel.json               # Vercel deployment config
```

---

## ⚙️ Local Setup

### Prerequisites
- Java 21+, Maven 3.9+
- MySQL 8+

### Backend

```bash
# 1. Create MySQL database
mysql -u root -p -e "CREATE DATABASE turfballers_db;"

# 2. Set environment variables (or edit application.properties)
export DB_URL=jdbc:mysql://localhost:3306/turfballers_db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-256-bit-secret-key-here-minimum-32-chars

# 3. Run
cd backend
mvn spring-boot:run
```

API runs at: `http://localhost:8080`  
Default admin: `admin@turfballers.com` / `Admin@123`

### Frontend

```bash
# Just open in browser (no build step needed)
cd frontend
# Open index.html with Live Server (VS Code extension)
# Or serve with Python:
python -m http.server 3000
```

---

## 🌐 Deployment

### Frontend → Vercel
1. Connect this GitHub repo to [vercel.com](https://vercel.com)
2. No build settings needed — `vercel.json` handles routing from `frontend/`

### Backend → Render
1. Create a new **Web Service** on [render.com](https://render.com)
2. Build command: `cd backend && mvn clean package -DskipTests`
3. Start command: `java -jar backend/target/*.jar`
4. Add environment variables in Render dashboard:
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`

### Database → Railway / PlanetScale
- Provision MySQL and copy the connection string into `DB_URL`

---

## 🔑 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login → returns JWT |
| GET | `/api/members` | List members (paginated) |
| POST | `/api/members` | Create member |
| GET | `/api/bookings` | List bookings |
| POST | `/api/bookings` | Create booking (overlap check) |
| PATCH | `/api/bookings/{id}/cancel` | Cancel booking |
| GET | `/api/payments` | List all payments |
| POST | `/api/payments` | Record payment |
| GET | `/api/attendance` | List attendance |
| POST | `/api/attendance` | Mark attendance |
| GET | `/api/dashboard/stats` | Dashboard KPIs |

---

## 📄 License

MIT © 2024 rogd2006-del
