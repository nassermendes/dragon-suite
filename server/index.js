const express = require('express');
const cors = require('cors');
const fetch = require('node-fetch');
const app = express();
const port = 8080;

// Tokens from build.gradle
const tokens = {
  instagram: {
    THEREAL_MENDES: 'IGQWRPeUxGZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGp',
    ALGARVIOCHARITY: 'IGQWRQWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJK'
  },
  youtube: {
    THEREAL_MENDES: 'AIzaSyBZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGp',
    ALGARVIOCHARITY: 'AIzaSyBQWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJK'
  },
  tiktok: {
    THEREAL_MENDES: 'act.ZAZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGp',
    ALGARVIOCHARITY: 'act.QWVhYZA2FDcGpZAZAjRhOVJKMXBQWVhYZA2FDcGpZAZAjRhOVJK'
  }
};

const InstagramAuth = require('../utils/instagramAuth');
const instagramConfig = require('../config/instagram');

// Enable CORS for all routes
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something broke!' });
});

app.get('/test', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>Connection Test</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          #loading { display: none; }
          #loading.active { display: block; }
          .connection-status { margin: 10px 0; padding: 10px; border-radius: 4px; }
          .connection-status.success { background: #e6ffe6; }
          .connection-status.error { background: #ffe6e6; }
        </style>
      </head>
      <body>
        <h1>Connection Test</h1>
        <button onclick="testConnections()">Test Connections</button>
        <div id="loading">Testing connections...</div>
        <div id="results"></div>

        <script>
        async function testConnections() {
          const loading = document.getElementById('loading');
          const results = document.getElementById('results');
          
          try {
            loading.classList.add('active');
            results.innerHTML = '';
            
            const response = await fetch('/api/test-connections');
            if (!response.ok) throw new Error('Network response was not ok');
            
            const data = await response.json();
            
            Object.entries(data).forEach(([platform, accounts]) => {
              Object.entries(accounts).forEach(([account, info]) => {
                const div = document.createElement('div');
                div.className = 'connection-status ' + (info.isConnected ? 'success' : 'error');
                div.textContent = \`\${platform} - \${account}: \${info.isConnected ? 'connected' : 'error'}\${info.accountName ? ' (' + info.accountName + ')' : ''}\${info.error ? ' - ' + info.error : ''}\`;
                results.appendChild(div);
              });
            });
          } catch (error) {
            const div = document.createElement('div');
            div.className = 'error-message';
            div.textContent = 'Unable to test connections: ' + error.message;
            results.appendChild(div);
          } finally {
            loading.classList.remove('active');
          }
        }
        </script>
      </body>
    </html>
  `);
});

app.get('/api/test-connections', async (req, res) => {
  console.log('Testing connections...');
  try {
    const results = {
      Instagram: {},
      YouTube: {},
      TikTok: {}
    };

    // Test Instagram connections
    for (const [account, token] of Object.entries(tokens.instagram)) {
      console.log(`Testing Instagram connection for ${account}...`);
      try {
        const response = await fetch('https://graph.instagram.com/v12.0/me', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        console.log(`Instagram ${account} response:`, data);
        results.Instagram[account] = {
          isConnected: response.ok,
          accountName: data.username,
          error: response.ok ? null : data.error?.message || 'Unknown error'
        };
      } catch (error) {
        console.error(`Instagram ${account} error:`, error);
        results.Instagram[account] = {
          isConnected: false,
          accountName: null,
          error: error.message || 'Connection failed'
        };
      }
    }

    // Test YouTube connections
    for (const [account, token] of Object.entries(tokens.youtube)) {
      console.log(`Testing YouTube connection for ${account}...`);
      try {
        const response = await fetch('https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        console.log(`YouTube ${account} response:`, data);
        results.YouTube[account] = {
          isConnected: response.ok,
          accountName: response.ok ? data.items?.[0]?.snippet?.title : null,
          error: response.ok ? null : data.error?.message || 'Unknown error'
        };
      } catch (error) {
        console.error(`YouTube ${account} error:`, error);
        results.YouTube[account] = {
          isConnected: false,
          accountName: null,
          error: error.message || 'Connection failed'
        };
      }
    }

    // Test TikTok connections
    for (const [account, token] of Object.entries(tokens.tiktok)) {
      console.log(`Testing TikTok connection for ${account}...`);
      try {
        const response = await fetch('https://open-api.tiktok.com/v2/user/info/', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        console.log(`TikTok ${account} response:`, data);
        results.TikTok[account] = {
          isConnected: response.ok,
          accountName: response.ok ? data.data?.user?.display_name : null,
          error: response.ok ? null : data.error?.message || 'Unknown error'
        };
      } catch (error) {
        console.error(`TikTok ${account} error:`, error);
        results.TikTok[account] = {
          isConnected: false,
          accountName: null,
          error: error.message || 'Connection failed'
        };
      }
    }

    console.log('Final results:', results);
    res.json(results);
  } catch (error) {
    console.error('Global error:', error);
    res.status(500).json({ error: error.message || 'Internal server error' });
  }
});

// Add new endpoint for testing Instagram specifically
app.get('/api/test-instagram', async (req, res) => {
  console.log('Testing Instagram connections...');
  try {
    const results = {};
    
    for (const [account, token] of Object.entries(tokens.instagram)) {
      console.log(`Testing Instagram connection for ${account}...`);
      try {
        // First, verify the token
        const meResponse = await fetch('https://graph.instagram.com/v12.0/me', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const meData = await meResponse.json();
        console.log(`Instagram ${account} /me response:`, meData);
        
        if (!meResponse.ok) {
          throw new Error(meData.error?.message || 'Failed to verify token');
        }
        
        // Then get user media to verify posting capabilities
        const mediaResponse = await fetch(`https://graph.instagram.com/v12.0/me/media?fields=id,caption,media_type,media_url,permalink`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const mediaData = await mediaResponse.json();
        console.log(`Instagram ${account} /media response:`, mediaData);

        results[account] = {
          isConnected: true,
          accountName: meData.username,
          accountId: meData.id,
          mediaCount: mediaData.data?.length || 0,
          scopes: meData.scopes || [],
          error: null
        };
      } catch (error) {
        console.error(`Instagram ${account} error:`, error);
        results[account] = {
          isConnected: false,
          accountName: null,
          error: error.message || 'Connection failed'
        };
      }
    }
    
    console.log('Instagram test results:', results);
    res.json(results);
  } catch (error) {
    console.error('Global Instagram test error:', error);
    res.status(500).json({ error: error.message || 'Internal server error' });
  }
});

// Instagram OAuth endpoints
app.get('/auth/instagram', async (req, res) => {
  const redirectUri = `${req.protocol}://${req.get('host')}/auth/instagram/callback`;
  const url = await InstagramAuth.getOAuthUrl(redirectUri);
  res.redirect(url);
});

app.get('/auth/instagram/callback', async (req, res) => {
  const { code } = req.query;
  const redirectUri = `${req.protocol}://${req.get('host')}/auth/instagram/callback`;
  
  try {
    // Get short-lived access token
    const shortLivedToken = await InstagramAuth.getAccessToken(code, redirectUri);
    
    // Exchange for long-lived access token
    const longLivedToken = await InstagramAuth.getLongLivedToken(shortLivedToken.access_token);
    
    // Store the token securely (you'll need to implement this)
    // For now, we'll just show it in the response
    res.json({
      success: true,
      token: longLivedToken.access_token,
      expires_in: longLivedToken.expires_in
    });
  } catch (error) {
    console.error('Instagram auth error:', error);
    res.status(500).json({ error: error.message });
  }
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
