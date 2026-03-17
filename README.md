# AI Stock Trader

A full‑stack, AI‑powered stock trading platform. Built with:

- **Backend**: Java 17 + Spring Boot
- **AI Service**: Python + FastAPI
- **Frontend**: React + TypeScript (coming soon)
- **Market Data Processor**: Rust (coming soon)

## Getting Started

1. Clone the repository
2. Run `./scripts/setup.sh` to install dependencies
3. Start PostgreSQL: `cd backend && docker-compose up -d`
4. Run the backend: `cd backend && ./mvnw spring-boot:run`
5. Run the AI service: `cd ai-service && uvicorn app.main:app --reload`

## CI/CD

GitHub Actions automatically builds and tests the backend on every push.

## License
MIT
