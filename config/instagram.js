module.exports = {
  // Facebook App configuration
  app: {
    id: process.env.FACEBOOK_APP_ID,
    secret: process.env.FACEBOOK_APP_SECRET,
  },
  
  // Instagram Business accounts
  accounts: {
    THEREAL_MENDES: {
      accessToken: process.env.INSTAGRAM_TOKEN_MENDES,
      userId: process.env.INSTAGRAM_USER_ID_MENDES,
    },
    ALGARVIOCHARITY: {
      accessToken: process.env.INSTAGRAM_TOKEN_CHARITY,
      userId: process.env.INSTAGRAM_USER_ID_CHARITY,
    }
  },
  
  // Required permissions
  scopes: [
    'instagram_basic',
    'instagram_content_publish',
    'instagram_manage_insights',
    'pages_read_engagement',
    'pages_manage_posts'
  ],
  
  // API endpoints
  endpoints: {
    base: 'https://graph.instagram.com/v12.0',
    oauth: 'https://api.instagram.com/oauth/authorize',
    token: 'https://api.instagram.com/oauth/access_token',
    longLivedToken: 'https://graph.instagram.com/access_token'
  }
};
