import { FullConfig } from '@playwright/test';

async function globalTeardown(config: FullConfig) {
  const serverProcess = (global as any).__SERVER__;
  if (serverProcess) {
    serverProcess.kill();
  }
}

export default globalTeardown;
