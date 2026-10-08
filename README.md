# 🍔 FoodHub Backend - REST API

Spring Boot REST API for the FoodHub food ordering app. It handles authentication, food menu, coupons, orders and admin operations.

**Frontend repo:** https://github.com/MSowbarnika/foodhub-frontend

## Features
- Register and login with token-based authentication
- Passwords stored securely using BCrypt
- Role-based access (USER / ADMIN)
- Food CRUD (create, update, delete allowed only for ADMIN)
- Coupon validation (`FOOD30` gives 30% off)
- Orders: prices and discount are recalculated on the server, so the client cannot change them
- Order status flow: Placed → Preparing → Delivered
- Sample foods are seeded automatically on first run

## Tech Stack
Java 17, Spring Boot, Spring Data JPA, MySQL, Maven, Docker

## API Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Create account |
| POST | `/api/auth/login` | Public | Login, returns token |
| GET | `/api/foods` | Public | List all foods |
| POST | `/api/foods` | Admin | Add food |
| PUT | `/api/foods/{id}` | Admin | Update food |
| DELETE | `/api/foods/{id}` | Admin | Delete food |
| GET | `/api/coupons/{code}` | Public | Validate coupon |
| POST | `/api/orders` | User | Place order |
| GET | `/api/orders` | User | View my orders |
| GET | `/api/admin/orders` | Admin | View all orders |
| PUT | `/api/admin/orders/{id}/status` | Admin | Update order status |

Protected endpoints need the header `Authorization: Bearer <token>`.

## Run Locally
1. Create a MySQL database named `foodhub`.
2. Create `src/main/resources/application-local.properties` (this file is git-ignored) with your MySQL password and admin email.
3. Run the app:

```bash
./mvnw spring-boot:run
```
4. API runs at `http://localhost:8080`

## Environment Variables (for deployment)
| Variable | Purpose |
|----------|---------|
| `DB_URL` | JDBC URL of the MySQL database |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `CORS_ORIGINS` | Allowed frontend URL |
| `ADMIN_EMAIL` | Email that gets the ADMIN role on registration |

## Author
**Sowbarnika M** - Java Full Stack Developer (fresher)
