import express, { Request, Response, NextFunction } from 'express';
import cors from 'cors';
import multer, { diskStorage, FileFilterCallback } from 'multer';
import path from 'path';
import fs from 'fs';
import { instagramRouter } from './routes/instagram';
import youtubeRouter from './routes/youtube';
import tiktokRouter from './routes/tiktok';
import logger from './utils/logger';
import dotenv from 'dotenv';
import { ApiResponse } from './types/social';

// Load environment variables
dotenv.config();

const app = express();
const port = process.env.NODE_ENV === 'test' ? 8081 : (process.env.PORT || 8080);

// Create uploads directory if it doesn't exist
const uploadsDir = path.join(__dirname, '../uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

// Configure multer for handling file uploads
const storage = diskStorage({
  destination: (_req: Request, _file: Express.Multer.File, cb: (error: Error | null, destination: string) => void) => {
    cb(null, uploadsDir);
  },
  filename: (_req: Request, file: Express.Multer.File, cb: (error: Error | null, filename: string) => void) => {
    const uniqueSuffix = `${Date.now()}-${Math.round(Math.random() * 1E9)}`;
    cb(null, `${file.fieldname}-${uniqueSuffix}${path.extname(file.originalname)}`);
  }
});

const fileFilter = (_req: Request, file: Express.Multer.File, cb: FileFilterCallback) => {
  const allowedMimes = ['video/mp4', 'video/quicktime', 'video/x-msvideo'];
  if (allowedMimes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Invalid file type. Only MP4, MOV, and AVI files are allowed.'));
  }
};

const upload = multer({ 
  storage,
  fileFilter,
  limits: {
    fileSize: 1024 * 1024 * 100 // 100MB limit
  }
});

// Error handling middleware
const errorHandler = (err: Error, _req: Request, res: Response, _next: NextFunction) => {
  const response: ApiResponse = {
    success: false,
    error: {
      message: err.message || 'Internal Server Error',
      type: 'SERVER_ERROR',
      code: 500
    }
  };
  res.status(500).json(response);
};

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(logger);

// Routes
app.use('/api/instagram', instagramRouter);
app.use('/api/youtube', youtubeRouter);
app.use('/api/tiktok', tiktokRouter);

// Error handling
app.use(errorHandler);

// Health check endpoint
app.get('/health', (_req: Request, res: Response) => {
  res.json({ status: 'ok' });
});

// Start server
if (require.main === module) {
  app.listen(port, () => {
    logger.info(`Server running at http://localhost:${port}`);
  });
}

export default app;
