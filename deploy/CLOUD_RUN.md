# Cloud Run Deployment

This backend is prepared for:

- frontend on Vercel
- backend on GCP Cloud Run

## Architecture note

This project includes websocket chat using Spring's in-memory simple broker.

That means Cloud Run is usable for early deployment, but chat is safest when the service runs as a single instance.

If Cloud Run scales to multiple instances, websocket traffic and in-memory message state can become inconsistent across instances.

For now, deploy with:

- `--max-instances=1`

## 1. Enable required GCP services

```bash
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com
```

## 2. Create an Artifact Registry repository

```bash
gcloud artifacts repositories create highpass-artifacts \
  --repository-format=docker \
  --location=asia-northeast3
```

## 3. Build and deploy manually once

From `highpass_backend`:

```bash
gcloud builds submit --config cloudbuild.yaml
```

This config now:

- builds the backend image
- pushes it to Artifact Registry
- deploys the new image to the existing Cloud Run service

## 4. Create a Cloud SQL for MySQL instance

Recommended starter settings:

- engine: MySQL 8.0
- region: `asia-northeast3`
- instance name: `highpass-mysql`
- database name: `highpassdb`

Example commands:

```bash
gcloud services enable sqladmin.googleapis.com

gcloud sql instances create highpass-mysql \
  --database-version=MYSQL_8_0 \
  --region=asia-northeast3 \
  --edition=enterprise \
  --cpu=1 \
  --memory=3840MB \
  --root-password=<ROOT_PASSWORD>

gcloud sql databases create highpassdb \
  --instance=highpass-mysql
```

After creation, note the instance connection name:

```bash
gcloud sql instances describe highpass-mysql \
  --format="value(connectionName)"
```

## 5. Deploy to Cloud Run manually for the first time

Replace the DB and secret values with real ones.

```bash
gcloud run deploy highpass-backend \
  --image asia-northeast3-docker.pkg.dev/<PROJECT_ID>/highpass-artifacts/highpass-backend:<IMAGE_TAG> \
  --region asia-northeast3 \
  --platform managed \
  --allow-unauthenticated \
  --max-instances 1 \
  --add-cloudsql-instances <PROJECT_ID>:asia-northeast3:highpass-mysql \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-env-vars FRONTEND_URL=https://highpassfrontend.vercel.app \
  --set-env-vars SECURE_COOKIE=true \
  --set-env-vars SPRING_DATASOURCE_URL='jdbc:mysql:///highpassdb?cloudSqlInstance=<PROJECT_ID>:asia-northeast3:highpass-mysql&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul' \
  --set-env-vars SPRING_DATASOURCE_USERNAME=<DB_USER> \
  --set-env-vars SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD> \
  --set-env-vars JWT_SECRET_KEY=<JWT_SECRET_KEY> \
  --set-env-vars JWT_EXPIRATION=900000 \
  --set-env-vars PUBLIC_DATA_API_KEY=<PUBLIC_DATA_API_KEY> \
  --set-env-vars GOOGLE_CLIENT_ID=<OPTIONAL_LATER> \
  --set-env-vars GOOGLE_CLIENT_SECRET=<OPTIONAL_LATER> \
  --set-env-vars KAKAO_REST_API_KEY=<OPTIONAL_LATER>
```

After this first manual deploy, Cloud Run service settings are stored on the service.

That means later CI/CD deploys only need to update the image.

## 6. Create a Cloud Build Trigger for CI/CD

Recommended setup:

- repository: connected GitHub repository
- event: push to branch
- branch: `main`
- build config file: `highpass_backend/cloudbuild.yaml`

Behavior after setup:

- push to `main`
- Cloud Build runs
- backend image is rebuilt
- Cloud Run service `highpass-backend` is updated automatically

Important:

- this assumes `highpass-backend` already exists from the first manual deploy
- runtime env values and Cloud SQL attachment stay on the Cloud Run service
- the trigger should not be used before the first manual deploy is complete

## 7. Update the frontend env

In Vercel:

- `NEXT_PUBLIC_FRONTEND_URL=https://highpassfrontend.vercel.app`
- `NEXT_PUBLIC_API_BASE_URL=https://<your-cloud-run-domain-or-custom-domain>`

Redeploy the frontend after changing env values.

For Kakao social login, also update the Kakao Developers console:

- site/domain: `https://highpassfrontend.vercel.app`
- redirect URI: `https://<your-backend-domain>/login/oauth2/code/kakao`

## 8. Verify

Check:

- `/api-docs`
- `/swagger-ui.html`
- login cookie issuance from the Vercel frontend
- `/api/users/me` returns `401` before login, not `302`

## 9. Important behavior notes

- `env.properties` is for local development only.
- Cloud Run should receive runtime values from the first `gcloud run deploy` or Secret Manager.
- `SPRING_PROFILES_ACTIVE=prod` enables Flyway migrations and sets Hibernate schema handling to `validate` by default.
- `SECURE_COOKIE=true` is correct for Cloud Run because the service is served over HTTPS.
- CORS uses `FRONTEND_URL`, so that value must exactly match the Vercel origin.
- The Cloud Build trigger in this repository only updates the image and redeploys the existing service.
