# HiBook

HiBook is a cloud-native web application for book lovers. Users can add books, track reading progress, and manage their personal reading list.

## Current Features

- Add books with title, author, and genre
- View all books
- Add books to a reading list
- View reading progress
- Two microservices:
  - Book Service
  - Reading Service
- PostgreSQL database storage
- React frontend with two pages

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- React
- Vite
- REST APIs

## Services

### Book Service

Runs on:

```text
http://localhost:8081
brew services start postgresql@16
createdb hibook_books
createdb hibook_reading
GET    /books
POST   /books
GET    /books/{id}
DELETE /books/{id}
GET    /actuator/health
http://localhost:8082
GET    /reading
POST   /reading
PUT    /reading/{id}
DELETE /reading/{id}
GET    /actuator/health
http://localhost:5173
brew services start postgresql@16
cd backend/book-service
./mvnw spring-boot:run
cd backend/reading-service
./mvnw spring-boot:run
cd frontend
npm install
npm run dev

---

# Step 4 — Initialize Git

Copy:

```bash
git init
cat > .gitignore <<'EOF'
.DS_Store

# Java / Maven
target/
*.class

# Node / React
node_modules/
dist/

# IDE
.idea/
.vscode/

# Logs
*.log
