import { Request, Response, NextFunction } from 'express';

interface RateLimitWindow {
  count: number;
  resetTime: number;
}

class RateLimiter {
  private static instance: RateLimiter;
  private limits: Map<string, RateLimitWindow>;
  private readonly windowMs: number = 3600000; // 1 hour
  private readonly maxRequests: number = 200; // Instagram API typically allows 200 requests per hour

  private constructor() {
    this.limits = new Map();
  }

  public static getInstance(): RateLimiter {
    if (!RateLimiter.instance) {
      RateLimiter.instance = new RateLimiter();
    }
    return RateLimiter.instance;
  }

  private getKey(req: Request): string {
    // Use IP address and account as the key
    const account = req.params.account || 'default';
    return `${req.ip}-${account}`;
  }

  private cleanup(): void {
    const now = Date.now();
    for (const [key, window] of this.limits.entries()) {
      if (window.resetTime <= now) {
        this.limits.delete(key);
      }
    }
  }

  public middleware(req: Request, res: Response, next: NextFunction): void {
    this.cleanup();
    
    const key = this.getKey(req);
    const now = Date.now();
    
    let window = this.limits.get(key);
    
    if (!window || window.resetTime <= now) {
      window = {
        count: 0,
        resetTime: now + this.windowMs
      };
    }

    if (window.count >= this.maxRequests) {
      res.status(429).json({
        success: false,
        error: {
          type: 'RateLimitError',
          message: 'Rate limit exceeded. Please try again later.',
          code: 429
        }
      });
      return;
    }

    window.count++;
    this.limits.set(key, window);

    // Add rate limit info to response headers
    res.setHeader('X-RateLimit-Limit', this.maxRequests.toString());
    res.setHeader('X-RateLimit-Remaining', (this.maxRequests - window.count).toString());
    res.setHeader('X-RateLimit-Reset', Math.ceil(window.resetTime / 1000).toString());

    next();
  }
}

export default RateLimiter.getInstance().middleware.bind(RateLimiter.getInstance());
