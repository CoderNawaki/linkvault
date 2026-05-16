# LinkVault

LinkVault is a URL saver application. Users can save useful links, add a tag and description, view the saved list, filter by tag, and delete links they no longer need.

This project is being built step by step with an agile workflow. Each branch should focus on the current issue or sprint task instead of building every planned feature at once.

## Current Scope

Branch `feature/LS-01-save-link` sets up the first working project structure:

- Vue + Vite frontend
- Spring Boot backend
- PostgreSQL through Docker Compose
- GitHub Actions CI

## Project Structure

```text
linkvault/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── LinkForm.vue
│   │   │   ├── LinkList.vue
│   │   │   └── TagFilter.vue
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.vue
│   │   └── main.js
│   ├── .env.development
│   ├── .env.production
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── backend/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## Tech Stack

- Frontend: Vue 3, Vite, JavaScript
- Backend: Spring Boot, Java 25, Maven
- Database: PostgreSQL
- CI: GitHub Actions

## Local Development

Start PostgreSQL and the backend with Docker Compose:

```bash
docker compose up --build
```

Run the frontend in development mode:

```bash
cd frontend
npm install
npm run dev
```

The frontend reads the backend URL from `frontend/.env.development`:

```text
VITE_API_URL=http://localhost:8080
```

## Backend

The backend exposes the first API endpoints under `/api/links`:

- `GET /api/links` lists saved links
- `POST /api/links` saves a new link
- `DELETE /api/links/{id}` deletes a saved link

Run backend tests locally:

```bash
cd backend
mvn test
```

## Java Distribution Note

CI and Docker use Java 25 with Eclipse Temurin.

Temurin is the Eclipse Adoptium build of OpenJDK, so it keeps the project on an OpenJDK-based runtime while matching the official Maven and runtime Docker images used by the backend.
