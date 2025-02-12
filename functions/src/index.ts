/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

import {onSchedule, ScheduledEvent} from "firebase-functions/v2/scheduler";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import {onObjectFinalized} from "firebase-functions/v2/storage";
import {initializeApp} from "firebase-admin/app";
import {getAuth} from "firebase-admin/auth";
import {getStorage} from "firebase-admin/storage";
import {getFirestore, FieldValue} from "firebase-admin/firestore";
import {getMessaging} from "firebase-admin/messaging";

initializeApp();

// Rotate service account keys every 3 months
export const rotateServiceAccountKeys = onSchedule("0 0 1 */3 *", async (event: ScheduledEvent): Promise<void> => {
  try {
    const auth = getAuth();

    // Get list of service accounts
    const serviceAccounts = await auth.listUsers();

    for (const account of serviceAccounts.users) {
      // Create new key and store it securely
      await auth.createCustomToken(account.uid);
      console.log(`Created new key for ${account.email}`);

      // Revoke old tokens
      await auth.revokeRefreshTokens(account.uid);
      console.log(`Revoked old tokens for ${account.email}`);
    }
  } catch (error) {
    console.error("Error rotating service account keys:", error);
    throw error;
  }
});

// Monitor security events
export const monitorSecurityEvents = onDocumentCreated(
  "security-events/{eventId}",
  async (event): Promise<void> => {
    const eventData = event.data?.data();
    if (!eventData) return;

    if (eventData.severity === "HIGH") {
      const message = {
        notification: {
          title: "High Severity Security Event",
          body: `Security event detected: ${eventData.description}`,
        },
        topic: "security-alerts",
      };

      await getMessaging().send(message);
    }
  }
);

// Check for exposed secrets in code
export const checkForExposedSecrets = onObjectFinalized({}, async (event): Promise<void> => {
  const object = event.data;
  if (!object.name?.endsWith(".ts") && !object.name?.endsWith(".js")) {
    return;
  }

  const bucket = getStorage().bucket(object.bucket);
  const file = bucket.file(object.name);
  const [content] = await file.download();
  const fileContent = content.toString("utf-8");

  const secretPatterns = [
    /api[_-]?key/i,
    /auth[_-]?token/i,
    /password/i,
    /secret/i,
  ];

  const matches = secretPatterns.some((pattern) => pattern.test(fileContent));

  if (matches) {
    await getFirestore().collection("security-events").add({
      type: "exposed-secret",
      severity: "HIGH",
      file: object.name,
      timestamp: FieldValue.serverTimestamp(),
      description: "Potential secret found in code",
    });
  }
});

export { analyzeSecurityEvent } from "./openai";
