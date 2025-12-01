const axios = require('axios');
const logger = require('../observability/logger');

async function getSpotifyUserProfile(accessToken) {
  // Mock mode if no real token
  if (!accessToken || accessToken === 'mock-token') {
    logger.warn('Using mock Spotify profile data');
    return {
      id: 'mock-user-123',
      display_name: 'Mock User',
      email: 'mock@example.com',
      country: 'US',
      images: [{ url: 'https://via.placeholder.com/150' }]
    };
  }

  try {
    const response = await axios.get('https://api.spotify.com/v1/me', {
      headers: { Authorization: `Bearer ${accessToken}` }
    });
    return response.data;
  } catch (error) {
    logger.error('Spotify API error, using mock data:', error.message);
    return {
      id: 'mock-user-123',
      display_name: 'Mock User',
      email: 'mock@example.com',
      country: 'US',
      images: [{ url: 'https://via.placeholder.com/150' }]
    };
  }
}

module.exports = { getSpotifyUserProfile };
