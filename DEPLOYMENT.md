# Backend Deployment

This backend is prepared for `Vercel + GCP Cloud Run`.

## Files

- `Dockerfile`: multi-stage Spring Boot image build
- `cloudbuild.yaml`: Cloud Build build + Cloud Run deploy config
- `env.properties`: local runtime configuration values
- `deploy/CLOUD_RUN.md`: Cloud Run deployment runbook

## Runtime config model

- local development: `env.properties`
- Cloud Run production: Cloud Run environment variables or Secret Manager

## CI/CD model

Recommended flow:

1. run the first Cloud Run deploy manually with all required env values
2. create a Cloud Build Trigger on the GitHub `main` branch
3. every push to `main` rebuilds the image and redeploys `highpass-backend`

This works because the trigger only replaces the container image.

Existing Cloud Run settings remain on the deployed service:

- environment variables
- Cloud SQL connection
- scaling settings
- public access settings

## Required production values

The backend expects the frontend origin in `FRONTEND_URL`.

That value must match the deployed Vercel origin so that:

- CORS works
- OAuth redirect flow works
- secure cookies use the correct cross-site policy

Typical production values:

- `FRONTEND_URL=https://highpassfrontend.vercel.app`
- `SECURE_COOKIE=true`
- `SPRING_DATASOURCE_URL=jdbc:mysql:///highpassdb?cloudSqlInstance=<PROJECT:REGION:INSTANCE>&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul`
- `SPRING_DATASOURCE_USERNAME=<production-db-user>`
- `SPRING_DATASOURCE_PASSWORD=<production-db-password>`
- `JWT_SECRET_KEY=<production-secret>`

If the frontend domain changes, update `FRONTEND_URL` on Cloud Run to the exact deployed origin. Social login providers must also allow that frontend origin and the backend callback URI for the active backend domain.

## Notes

- Spring uses `server.forward-headers-strategy=framework` so redirects and cookies behave correctly behind Cloud Run.
- Spring uses `server.port=${PORT:8080}` so the container can bind to the Cloud Run runtime port.
- API authentication failures under `/api/**` return `401` instead of redirecting to OAuth login.
- Detailed Cloud Run steps are in [deploy/CLOUD_RUN.md](./deploy/CLOUD_RUN.md).
