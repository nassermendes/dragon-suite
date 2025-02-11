import { ErrorRequestHandler } from 'express';

interface ApiError extends Error {
  statusCode?: number;
  code?: string;
}

const errorHandler: ErrorRequestHandler = (err: ApiError, _req, res, _next) => {
  const statusCode = err.statusCode || 500;
  
  res.status(statusCode).json({
    success: false,
    error: {
      type: err.code || 'InternalError',
      message: err.message || 'An unexpected error occurred',
      code: statusCode,
    },
  });
};

export default errorHandler;
