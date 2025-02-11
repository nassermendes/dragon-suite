import { config } from 'dotenv';
import { resolve } from 'path';
import express from 'express';
import cors from 'cors';
import fetch from 'node-fetch';
import instagramRouter from './routes/instagram';
import errorHandler from './middleware/errorHandler';
import logger from './middleware/logger';
import rateLimiter from './middleware/rateLimiter';

// Load environment variables from .env file
config({ path: resolve(__dirname, '../../.env') });

// Override with clean values
process.env.FACEBOOK_APP_ID = '1652134369042127';
process.env.FACEBOOK_APP_SECRET = 'e4c9d3340826ed90f625b8d52c538f92';

const app = express();
const port = process.env.PORT || 8080;

// Middleware
app.use(logger);
app.use(express.static(resolve(__dirname, '../../public')));
app.use(cors());
app.use(express.json());
app.use(rateLimiter);

// Verify required environment variables
const requiredEnvVars = ['FACEBOOK_APP_ID', 'FACEBOOK_APP_SECRET'];
for (const envVar of requiredEnvVars) {
  if (!process.env[envVar]) {
    throw new Error(`Missing required environment variable: ${envVar}`);
  }
}

// Instagram tokens configuration
const tokens: any = {
  THEREAL_MENDES: {
    accessToken: process.env.INSTAGRAM_PERSONAL_ACCESS_TOKEN || '',
    userId: process.env.INSTAGRAM_PERSONAL_USER_ID || ''
  },
  ALGARVIOCHARITY: {
    accessToken: process.env.INSTAGRAM_CHARITY_ACCESS_TOKEN || '',
    userId: process.env.INSTAGRAM_CHARITY_USER_ID || ''
  }
};

// Routes
app.use('/api/instagram', instagramRouter);

// Root endpoint with account selection
app.get('/', (_req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>Instagram Integration Test</title>
        <style>
          body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
          .button { padding: 10px 20px; background: #0095f6; color: white; border: none; border-radius: 4px; cursor: pointer; margin: 10px; }
        </style>
      </head>
      <body>
        <h1>Instagram Integration Test</h1>
        <div>
          <h2>Select Account:</h2>
          <a href="/api/instagram/auth/init/THEREAL_MENDES" class="button">@thereal.mendes</a>
          <a href="/api/instagram/auth/init/ALGARVIOCHARITY" class="button">@algarviocharity</a>
        </div>
      </body>
    </html>
  `);
});

// Error handling must be last
app.use(errorHandler);

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
