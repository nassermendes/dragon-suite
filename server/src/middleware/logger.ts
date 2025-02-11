import { Request, Response, NextFunction } from 'express';
import { resolve } from 'path';
import { appendFileSync, existsSync, mkdirSync } from 'fs';

interface LogEntry {
  timestamp: string;
  method: string;
  path: string;
  statusCode?: number;
  responseTime: number;
  userAgent?: string;
  ip: string;
  error?: string;
  level: 'error' | 'info';
}

interface LogOptions {
  excludePaths?: string[];
  logLevel?: 'error' | 'info' | 'debug';
}

function createLogger(options: LogOptions = {}): (req: Request, res: Response, next: NextFunction) => void {
  const logDir = resolve(__dirname, '../../../logs');
  const logFile = resolve(logDir, 'api.log');

  // Initialize log directory
  if (!existsSync(logDir)) {
    mkdirSync(logDir, { recursive: true });
  }

  function formatLogEntry(entry: LogEntry): string {
    return JSON.stringify(entry) + '\n';
  }

  function writeLog(entry: LogEntry): void {
    try {
      const formattedEntry = formatLogEntry(entry);
      appendFileSync(logFile, formattedEntry, 'utf8');
    } catch (error) {
      console.error('Failed to write to log file:', error instanceof Error ? error.message : 'Unknown error');
    }
  }

  return function loggerMiddleware(req: Request, res: Response, next: NextFunction): void {
    // Skip logging for excluded paths
    if (options.excludePaths?.some(path => req.path.startsWith(path))) {
      return next();
    }

    const startTime = Date.now();

    // Capture response data
    const originalEnd = res.end;
    res.end = function(chunk?: any, encoding?: string | (() => void), cb?: () => void): Response {
      const responseTime = Date.now() - startTime;

      const logEntry: LogEntry = {
        timestamp: new Date().toISOString(),
        method: req.method,
        path: req.path,
        statusCode: res.statusCode,
        responseTime,
        userAgent: req.get('user-agent'),
        ip: req.ip,
        level: res.statusCode >= 400 ? 'error' : 'info'
      };

      // Only log errors if status code >= 400
      if (res.statusCode >= 400) {
        logEntry.error = res.statusMessage;
      }

      // Only write log if it meets the minimum log level
      if (
        options.logLevel === 'debug' ||
        (options.logLevel === 'error' && logEntry.error) ||
        (options.logLevel === 'info' && !logEntry.error)
      ) {
        writeLog(logEntry);
      }

      res.end = originalEnd;
      return originalEnd.call(this, chunk, encoding as string, cb);
    };

    next();
  };
}

// Create default logger instance
const logger = createLogger({
  excludePaths: ['/health'],
  logLevel: process.env.NODE_ENV === 'development' ? 'debug' : 'error'
});

export default logger;
