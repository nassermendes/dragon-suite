import { FullConfig } from '@playwright/test';
import { spawn } from 'child_process';
import path from 'path';

async function globalSetup(config: FullConfig) {
  const serverProcess = spawn('npm', ['run', 'test:server'], {
    shell: true,
    stdio: 'pipe',
    cwd: path.join(__dirname, '..', '..')
  });

  // Wait for server to start
  await new Promise<void>((resolve) => {
    serverProcess.stdout.on('data', (data) => {
      if (data.toString().includes('Server is running')) {
        resolve();
      }
    });
  });

  // Store the server process for cleanup
  (global as any).__SERVER__ = serverProcess;
}

export default globalSetup;
