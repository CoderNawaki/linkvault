# Project Instructions (GEMINI.md)

Foundational mandates for LinkVault development.

## Tech Stack

- **Backend:** Spring Boot (Java 25), Maven
- **Database:** Local H2 (file-based), Production PostgreSQL
- **Frontend:** Vue 3 (Composition API), Vite, Vanilla CSS
- **Containerization:** Docker + Docker Compose
- **CI/CD:** GitHub Actions (Current), planning transition to Depot/similar.
- **Deployment:** Railway or Render

## Architectural Conventions

### Backend (Spring Boot)
- **Pattern:** Layered architecture: `Controller` -> `Service` -> `Repository`.
- **Validation:** Use `jakarta.validation` constraints on models.
- **API:** RESTful endpoints under `/api/links`.
- **Title Fetching:** Use `TitleService` (HttpClient + Regex) for auto-fetching titles.

### Frontend (Vue 3)
- **Pattern:** Composition API (`<script setup>`).
- **Services:** Centralized API calls in `src/services/api.js`.
- **Styles:** Prefer Vanilla CSS for maximum flexibility. Avoid TailwindCSS unless requested.
- **State:** Use `reactive` for forms and `ref` for simple state.

## Agile Workflow

- **Structure:** Development is organized into Epics and Sprints.
- **Tasks:** Each sprint typically contains 4 tasks (LS-01 through LS-04).
- **Branching:** Use `feature/LS-XX-...` for task-specific changes.
- **Process:** Research -> Strategy -> Execution (Plan, Act, Validate).

## Verification Commands

### Backend
- **Test:** `cd backend && mvn test`
- **Run:** `cd backend && mvn spring-boot:run`

### Frontend
- **Build:** `cd frontend && npm run build`
- **Dev:** `cd frontend && npm run dev`

### System
- **Docker:** `docker compose up --build`
