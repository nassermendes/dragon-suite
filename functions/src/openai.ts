import * as functions from "firebase-functions/v2";
import { getFirestore } from "firebase-admin/firestore";

interface OpenAIMessage {
  role: "system" | "user" | "assistant";
  content: string;
}

export const analyzeSecurityEvent = functions.https.onCall(async (data) => {
  const db = getFirestore();
  
  // Use environment variable for API key
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    throw new Error("OpenAI API key not configured");
  }

  const messages: OpenAIMessage[] = [
    {
      role: "system",
      content: "You are a security analyst reviewing potential security incidents.",
    },
    {
      role: "user",
      content: `Analyze this security event: ${JSON.stringify(data)}`,
    },
  ];

  try {
    // Store analysis in Firestore
    await db.collection("security-analysis").add({
      event: data,
      timestamp: new Date(),
      status: "analyzed",
    });

    return {
      status: "success",
      message: "Security event analyzed and stored",
    };
  } catch (error) {
    console.error("Error analyzing security event:", error);
    throw new functions.https.HttpsError("internal", "Failed to analyze security event");
  }
});
