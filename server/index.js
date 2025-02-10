const express = require('express');
const cors = require('cors');
const app = express();
const port = 8080;

// Enable CORS for all routes
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something broke!' });
});

// Mock data for testing
const mockConnections = {
  instagram: { status: 'connected', message: 'Instagram connection successful' },
  youtube: { status: 'connected', message: 'YouTube connection successful' },
  tiktok: { status: 'connected', message: 'TikTok connection successful' }
};

// Test endpoint
app.get('/test', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>Connection Test</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          padding: 20px;
        }
        .connection-status {
          margin: 10px 0;
          padding: 10px;
          border: 1px solid #ccc;
          border-radius: 4px;
        }
        .error-message {
          color: red;
          padding: 10px;
          border: 1px solid red;
          border-radius: 4px;
          margin: 10px 0;
        }
        #loading {
          display: none;
          padding: 10px;
          background-color: #f0f0f0;
          border-radius: 4px;
          margin: 10px 0;
        }
        #loading.active {
          display: block;
        }
      </style>
    </head>
    <body>
      <h1>Connection Test</h1>
      <button onclick="testConnections()">Test Connections</button>
      <div id="loading">Testing connections...</div>
      <div id="results"></div>

      <script>
        function sleep(ms) {
          return new Promise(resolve => setTimeout(resolve, ms));
        }

        async function testConnections() {
          const loading = document.getElementById('loading');
          const results = document.getElementById('results');
          
          try {
            console.log('Starting connection test...');
            loading.classList.add('active');
            results.innerHTML = '';
            
            // Add a small delay to ensure loading state is visible
            await sleep(500);
            
            console.log('Fetching connection status...');
            const response = await fetch('/api/test-connections');
            if (!response.ok) {
              throw new Error('Network response was not ok');
            }
            
            const data = await response.json();
            console.log('Received data:', data);
            
            // Add a small delay before showing results
            await sleep(500);
            
            Object.entries(data).forEach(([platform, info]) => {
              console.log('Adding status for:', platform);
              const div = document.createElement('div');
              div.className = 'connection-status';
              div.textContent = \`\${platform}: \${info.status} - \${info.message}\`;
              results.appendChild(div);
            });
          } catch (error) {
            console.error('Error:', error);
            const div = document.createElement('div');
            div.className = 'error-message';
            div.textContent = 'Unable to test connections: ' + error.message;
            results.appendChild(div);
          } finally {
            console.log('Test complete, hiding loading state');
            loading.classList.remove('active');
          }
        }
      </script>
    </body>
    </html>
  `);
});

// API endpoint for connection testing
app.get('/api/test-connections', (req, res) => {
  // Add a small delay to simulate network latency
  setTimeout(() => {
    try {
      console.log('Sending connection status:', mockConnections);
      res.json(mockConnections);
    } catch (error) {
      console.error('Error sending connection status:', error);
      res.status(500).json({ error: 'Failed to fetch connection status' });
    }
  }, 1000);
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok' });
});

const server = app.listen(port, () => {
  console.log('Test server running at http://localhost:' + port);
});

// Handle graceful shutdown
process.on('SIGTERM', () => {
  server.close(() => {
    console.log('Server shutdown complete');
  });
});
