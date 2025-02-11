import fetch from 'node-fetch';

async function checkServer() {
  try {
    const response = await fetch('http://localhost:8080/health');
    const data = await response.json();
    console.log('Server response:', data);
    return data.status === 'ok';
  } catch (error) {
    console.error('Error checking server:', error);
    return false;
  }
}

checkServer().then(isRunning => {
  console.log('Server is running:', isRunning);
  process.exit(isRunning ? 0 : 1);
});
