const fetch = require('node-fetch');
const config = require('../config/instagram');

class InstagramAuth {
  static async getOAuthUrl(redirectUri) {
    const params = new URLSearchParams({
      client_id: config.app.id,
      redirect_uri: redirectUri,
      scope: config.scopes.join(','),
      response_type: 'code',
    });
    
    return `${config.endpoints.oauth}?${params.toString()}`;
  }
  
  static async getAccessToken(code, redirectUri) {
    const response = await fetch(config.endpoints.token, {
      method: 'POST',
      body: new URLSearchParams({
        client_id: config.app.id,
        client_secret: config.app.secret,
        grant_type: 'authorization_code',
        redirect_uri: redirectUri,
        code,
      }),
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(`Failed to get access token: ${error.error_message}`);
    }
    
    return response.json();
  }
  
  static async getLongLivedToken(shortLivedToken) {
    const params = new URLSearchParams({
      grant_type: 'ig_exchange_token',
      client_secret: config.app.secret,
      access_token: shortLivedToken,
    });
    
    const response = await fetch(`${config.endpoints.longLivedToken}?${params.toString()}`);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(`Failed to get long-lived token: ${error.error_message}`);
    }
    
    return response.json();
  }
  
  static async refreshLongLivedToken(token) {
    const params = new URLSearchParams({
      grant_type: 'ig_refresh_token',
      access_token: token,
    });
    
    const response = await fetch(`${config.endpoints.longLivedToken}?${params.toString()}`);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(`Failed to refresh token: ${error.error_message}`);
    }
    
    return response.json();
  }
}

module.exports = InstagramAuth;
