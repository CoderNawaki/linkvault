# LinkVault

LinkVault is a personal URL manager for saving useful links with titles, descriptions, personal notes, tags, favourites, search, sorting, and JSON export.

The project is developed sprint by sprint. Each task is kept small and merged through a focused branch so the history stays easy to review.

## Current Features

- Save links with URL, optional title, description, personal note, and comma-separated tags.
- Auto-fetch a page title when the user leaves the title blank.
- List saved links newest first by default.
- Delete links with a confirmation prompt.
- Filter by tag and clear the active tag filter.
- Search across title, description, personal note, and URL.
- Mark links as favourites and view starred links only.
- Sort links by newest, oldest, or title.
- Export saved links as a dated JSON backup file.
- Preserve frontend view preferences in `localStorage`.
- Show clear empty states for first use and for filters with no matching links.

## Tech Stack

- Frontend: Vue 3, Vite, JavaScript, Vanilla CSS
- Backend: Spring Boot, Java 25, Maven
- Local database: file-based H2
- Docker database: PostgreSQL through Docker Compose
- Production database: PostgreSQL, currently prepared for Render
- Migrations: Flyway
- CI: GitHub Actions
- Containerization: Docker

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

## Local Development

Run the backend locally without Docker:

```bash
cd backend
mvn spring-boot:run
```

Local backend runs use an embedded H2 database stored under `backend/data/`. This keeps development simple because PostgreSQL does not need to be running.

Start PostgreSQL and the backend together with Docker Compose:

```bash
docker compose up --build
```

Docker Compose overrides the local H2 settings with PostgreSQL connection environment variables.

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

## Verification

Run backend tests:

```bash
cd backend
mvn test
```

Run the frontend production build:

```bash
cd frontend
npm run build
```

## Backend API

The backend exposes REST endpoints under `/api/links`:

- `GET /api/links` lists saved links by newest creation time.
- `POST /api/links` saves a new link and auto-fetches the title when needed.
- `PATCH /api/links/{id}/favourite` toggles a link's favourite state.
- `DELETE /api/links/{id}` deletes a saved link.

The `Link` model currently stores:

- `title`
- `url`
- `description`
- `personalNote`
- `tags`
- `favourite`
- `createdAt`

## Deployment Notes

For Render deployment, keep services separated:

- Render PostgreSQL database: managed database resource.
- Backend Web Service: deploy from `backend/Dockerfile`.
- Frontend Static Site: deploy from the `frontend` directory.

Backend Render settings:

```text
Runtime: Docker
Root Directory: backend
Dockerfile Path: ./Dockerfile
```

Backend environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<internal-host>:5432/<database-name>
SPRING_DATASOURCE_USERNAME=<database-user>
SPRING_DATASOURCE_PASSWORD=<database-password>
SPRING_H2_CONSOLE_ENABLED=false
```

The backend also reads Render's assigned `PORT` through:

```text
server.port=${PORT:8080}
```

Frontend Render settings:

```text
Root Directory: frontend
Build Command: npm install && npm run build
Publish Directory: dist
```

Frontend production environment variable:

```text
VITE_API_URL=https://<backend-primary-url>
```

Use the backend base URL only. Do not include `/api/links` in `VITE_API_URL`.

## Project History

### Foundation

- LS-01: Scaffolded the Vue + Vite frontend, Spring Boot backend, PostgreSQL Docker Compose setup, and CI workflow.
- Fixed initial backend compile and runtime issues by aligning test dependencies and making local H2 the default database.
- LS-02: Refined saved-link display with clearer feedback, error messaging, and dynamic counts.
- LS-03: Added delete confirmation before removing saved links.

### Link Capture And Organization

- LS-04: Added automatic title fetching through `TitleService`.
- LS-05: Added support for multiple comma-separated tags.
- LS-06: Added tag filtering, clear-filter control, and better tag spacing.
- LS-07: Added personal notes for saved links.

### Search, Persistence, And Productivity

- LS-08: Added client-side search across saved link fields, then polished it with a clear button, keyboard focus shortcut, hidden browser search cancel control, and better no-result behavior.
- LS-09: Added Flyway migrations for durable schema management and preserved frontend view settings in `localStorage`.
- Added `spring-boot-devtools` for backend hot reload during local development.

### Favourite, Sort, And Export

- LS-10: Added favourite toggling and a starred-only view.
- LS-11: Added sorting by newest, oldest, and title.
- LS-12: Added JSON export for saved links.

### Security And Deployment Readiness

- Hardened URL title fetching by validating fetchable URLs, allowing only public HTTP/HTTPS hosts, blocking local/private addresses, using request timeouts, and avoiding redirects.
- Added deployment support for platform-assigned ports with `server.port=${PORT:8080}`.
- Prepared Render deployment flow with managed PostgreSQL, backend Docker Web Service, and frontend Static Site.

### Sprint 4 UI Polish

- Added clearer empty states for first use and for filters/searches with no results.
- Added a clear-filters action when an existing vault has no matches.
- Polished the toolbar, card hover states, mobile layout widths, and neutral UI palette.

## Java Distribution Note

CI and Docker use Java 25 with Eclipse Temurin.

Temurin is the Eclipse Adoptium build of OpenJDK, so it keeps the project on an OpenJDK-based runtime while matching the official Maven and runtime Docker images used by the backend.
