# Backend Deployment

This backend is prepared for `Vercel + GCP Cloud Run`.

## Files

- `Dockerfile`: multi-stage Spring Boot image build
- `cloudbuild.yaml`: Cloud Build image build config
- `env.properties`: local runtime configuration values
- `deploy/CLOUD_RUN.md`: Cloud Run deployment runbook

## Runtime config model

- local development: `env.properties`
- Cloud Run production: Cloud Run environment variables or Secret Manager

## Required production values

The backend expects the frontend origin in `FRONTEND_URL`.

That value must match the deployed Vercel origin so that:

- CORS works
- OAuth redirect flow works
- secure cookies use the correct cross-site policy

Typical production values:

- `FRONTEND_URL=https://highpassfrontend.vercel.app`
- `SECURE_COOKIE=true`
- `SPRING_DATASOURCE_URL=<production-db-url>`
- `SPRING_DATASOURCE_USERNAME=<production-db-user>`
- `SPRING_DATASOURCE_PASSWORD=<production-db-password>`
- `JWT_SECRET_KEY=<production-secret>`

## Notes

- Spring uses `server.forward-headers-strategy=framework` so redirects and cookies behave correctly behind Cloud Run.
- Spring uses `server.port=${PORT:8080}` so the container can bind to the Cloud Run runtime port.
- API authentication failures under `/api/**` return `401` instead of redirecting to OAuth login.
- Detailed Cloud Run steps are in [deploy/CLOUD_RUN.md](./deploy/CLOUD_RUN.md).
