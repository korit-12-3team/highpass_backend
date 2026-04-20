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

## 3. Build the container image

From `highpass_backend`:

```bash
gcloud builds submit --config cloudbuild.yaml
```

## 4. Deploy to Cloud Run

Replace the DB and secret values with real ones.

```bash
gcloud run deploy highpass-backend \
  --image asia-northeast3-docker.pkg.dev/<PROJECT_ID>/highpass-artifacts/highpass-backend:<IMAGE_TAG> \
  --region asia-northeast3 \
  --platform managed \
  --allow-unauthenticated \
  --max-instances 1 \
  --set-env-vars FRONTEND_URL=https://highpassfrontend.vercel.app \
  --set-env-vars SECURE_COOKIE=true \
  --set-env-vars SPRING_DATASOURCE_URL=jdbc:mariadb://<DB_HOST>:3306/highpassdb \
  --set-env-vars SPRING_DATASOURCE_USERNAME=<DB_USER> \
  --set-env-vars SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD> \
  --set-env-vars JWT_SECRET_KEY=<JWT_SECRET_KEY> \
  --set-env-vars JWT_EXPIRATION=900000 \
  --set-env-vars PUBLIC_DATA_API_KEY=<PUBLIC_DATA_API_KEY> \
  --set-env-vars GOOGLE_CLIENT_ID=<OPTIONAL_LATER> \
  --set-env-vars GOOGLE_CLIENT_SECRET=<OPTIONAL_LATER> \
  --set-env-vars KAKAO_REST_API_KEY=<OPTIONAL_LATER>
```

## 5. Update the frontend env

In Vercel:

- `NEXT_PUBLIC_FRONTEND_URL=https://highpassfrontend.vercel.app`
- `NEXT_PUBLIC_API_BASE_URL=https://<your-cloud-run-domain-or-custom-domain>`

Redeploy the frontend after changing env values.

## 6. Verify

Check:

- `/api-docs`
- `/swagger-ui.html`
- login cookie issuance from the Vercel frontend
- `/api/users/me` returns `401` before login, not `302`

## 7. Important behavior notes

- `env.properties` is for local development only.
- Cloud Run should receive runtime values from `--set-env-vars` or Secret Manager.
- `SECURE_COOKIE=true` is correct for Cloud Run because the service is served over HTTPS.
- CORS uses `FRONTEND_URL`, so that value must exactly match the Vercel origin.
