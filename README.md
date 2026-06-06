# template — Full-Stack Project Template

Reusable monorepo template with:

- **React** (Vite + TypeScript) frontend
- **Spring Boot** backend with embedded JWT auth
- **PostgreSQL** database with Flyway migrations
- **MinIO** (S3-compatible) object storage

## Quick Start (new project from template)

1. Create a repo from this GitHub template (or clone it).
2. Run the init script:

```bash
./scripts/init-project.sh my-new-app com.mycompany.mynewapp
```

3. Start infrastructure:

```bash
docker compose up -d
```

4. Start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

5. Start the frontend:

```bash
cd frontend
npm install
npm run dev
```

6. Open [http://localhost:5173](http://localhost:5173)

## Local Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | — |
| Backend API | http://localhost:8080/api | — |
| PostgreSQL | localhost:5432 | `postgres` / `postgres` |
| MinIO API | http://localhost:9000 | `minioadmin` / `minioadmin` |
| MinIO Console | http://localhost:9001 | `minioadmin` / `minioadmin` |

## API Endpoints

### Auth (`/api/auth`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Create account |
| POST | `/login` | Login, returns access JWT + httpOnly refresh cookie |
| POST | `/refresh` | Refresh access token using cookie |
| POST | `/logout` | Revoke refresh token |
| GET | `/me` | Current user profile (authenticated) |

### Files (`/api/files`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/upload` | Upload file to S3/MinIO (authenticated) |
| GET | `/{key}` | Get presigned download URL (authenticated) |

## Configuration

Copy environment files after running the init script, or manually:

```bash
cp .env.example .env
cp frontend/.env.example frontend/.env
```

Key variables:

- `JWT_SECRET` — change to a long random string (32+ chars)
- `DB_*` — PostgreSQL connection
- `S3_*` — MinIO locally; swap endpoint/credentials for real AWS S3 in production
- `VITE_API_URL` — frontend API base URL

## Project Structure

```
template/
├── frontend/          # React app
├── backend/           # Spring Boot API
├── docker-compose.yml # Postgres + MinIO
├── scripts/           # init-project.sh
└── .env.example       # Shared configuration
```

## Extending the Template

### Add a new database entity

1. Create a Flyway migration in `backend/src/main/resources/db/migration/`
2. Add JPA entity + repository
3. Add REST controller and DTOs

### Add a new React page

1. Create a page component in `frontend/src/pages/`
2. Register the route in `frontend/src/App.tsx`
3. Add API functions in `frontend/src/api/`

### Use real AWS S3

Update `.env`:

```env
S3_ENDPOINT=https://s3.amazonaws.com
S3_ACCESS_KEY=your-access-key
S3_SECRET_KEY=your-secret-key
S3_BUCKET=your-bucket
S3_REGION=us-east-1
```

No code changes required.

## Security Notes

- Passwords are hashed with BCrypt (strength 12)
- Refresh tokens are stored as SHA-256 hashes in the database
- Refresh tokens are sent via httpOnly cookies scoped to `/api/auth`
- Access tokens are kept in memory on the frontend
- Never commit `.env` files

## GitHub Template Setup

To use this as a GitHub template repository:

1. Push this repo to GitHub
2. Go to **Settings → General → Template repository** and enable it
3. New projects: **Use this template** → run `init-project.sh`
