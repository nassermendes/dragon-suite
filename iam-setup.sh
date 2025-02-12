#!/bin/bash

# 1. Create custom roles with least privilege
gcloud iam roles create dragonSuiteAppEngine \
    --project=dragon-suite \
    --title="Dragon Suite App Engine Role" \
    --description="Custom role for App Engine with minimal permissions" \
    --permissions=appengine.applications.get,appengine.instances.list,appengine.services.get \
    --stage=GA

gcloud iam roles create dragonSuiteFirebase \
    --project=dragon-suite \
    --title="Dragon Suite Firebase Role" \
    --description="Custom role for Firebase with minimal permissions" \
    --permissions=firebase.projects.get,firebase.clients.get \
    --stage=GA

# 2. Update service account permissions
# Remove editor role from App Engine default service account
gcloud projects remove-iam-policy-binding dragon-suite \
    --member="serviceAccount:dragon-suite@appspot.gserviceaccount.com" \
    --role="roles/editor"

# Add custom role instead
gcloud projects add-iam-policy-binding dragon-suite \
    --member="serviceAccount:dragon-suite@appspot.gserviceaccount.com" \
    --role="projects/dragon-suite/roles/dragonSuiteAppEngine"

# 3. Enable audit logging
gcloud projects add-iam-policy-binding dragon-suite \
    --member="serviceAccount:dragon-suite@appspot.gserviceaccount.com" \
    --role="roles/logging.logWriter"

# Create log sink for audit logs
gcloud logging sinks create dragon-suite-audit-logs \
    storage.googleapis.com/dragon-suite-audit-logs \
    --log-filter="resource.type=service_account"

# 4. Set up key rotation policy
# Create a Cloud Scheduler job to rotate keys every 90 days
gcloud scheduler jobs create http rotate-service-account-keys \
    --schedule="0 0 1 */3 *" \
    --uri="https://dragon-suite.cloudfunctions.net/rotateServiceAccountKeys" \
    --http-method=POST \
    --attempt-deadline=30s \
    --time-zone="UTC"

# 5. Remove unused service accounts
# List all service accounts
gcloud iam service-accounts list --format="table(email,disabled)" --filter="disabled=true"

# Delete disabled service accounts
for account in $(gcloud iam service-accounts list --format="value(email)" --filter="disabled=true"); do
    gcloud iam service-accounts delete $account --quiet
done

# 6. Set up security alerts
# Create alert policy for suspicious activities
gcloud alpha monitoring policies create \
    --notification-channels="projects/dragon-suite/notificationChannels/email" \
    --display-name="Suspicious Service Account Activity" \
    --documentation="Alert for suspicious service account activities" \
    --filter="resource.type=service_account AND severity>=WARNING" \
    --combiner=OR \
    --conditions="display-name='High volume of failed requests',filter='metric.type=\"logging.googleapis.com/user/failed_requests\" AND resource.type=\"service_account\"',threshold=50,comparison=COMPARISON_GT,duration=300s"
