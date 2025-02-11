import { setupWorker, SetupWorkerApi, StartOptions } from 'msw';
import { handlers } from './handlers';

export const worker: SetupWorkerApi = setupWorker(...handlers);

// Start the worker with proper error handling
async function startWorker(): Promise<void> {
  if (process.env.NODE_ENV === 'development') {
    try {
      const options: StartOptions = {
        onUnhandledRequest: 'bypass',
        quiet: process.env.NODE_ENV === 'test'
      };

      await worker.start(options);
      console.log('MSW worker started successfully');
    } catch (error) {
      console.error('Failed to start MSW worker:', error instanceof Error ? error.message : 'Unknown error');
      throw error; // Re-throw to handle in higher level error boundary
    }
  }
}

// Initialize worker
startWorker().catch(error => {
  console.error('Worker initialization failed:', error);
  process.exit(1);
});
