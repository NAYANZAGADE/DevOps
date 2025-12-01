const axios = require('axios');
const logger = require('../observability/logger');

let accessToken = null;
let tokenExpiry = null;

const USE_MOCK = !process.env.SPOTIFY_CLIENT_ID || process.env.SPOTIFY_CLIENT_ID === 'mock';

async function getSpotifyAccessToken() {
  if (USE_MOCK) {
    return 'mock-token';
  }

  if (accessToken && tokenExpiry && Date.now() < tokenExpiry) {
    return accessToken;
  }

  try {
    const response = await axios.post(
      'https://accounts.spotify.com/api/token',
      'grant_type=client_credentials',
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': `Basic ${Buffer.from(
            `${process.env.SPOTIFY_CLIENT_ID}:${process.env.SPOTIFY_CLIENT_SECRET}`
          ).toString('base64')}`
        }
      }
    );

    accessToken = response.data.access_token;
    tokenExpiry = Date.now() + (response.data.expires_in * 1000);
    return accessToken;
  } catch (error) {
    logger.error('Failed to get Spotify token:', error.message);
    return 'mock-token';
  }
}

async function searchSpotify(query, type, limit) {
  if (USE_MOCK) {
    logger.warn('Using mock search results');
    return {
      tracks: {
        items: [
          {
            id: 'mock-track-1',
            name: `Mock Result for "${query}"`,
            artists: [{ name: 'Mock Artist' }],
            album: { name: 'Mock Album', images: [{ url: 'https://via.placeholder.com/300' }] },
            duration_ms: 180000
          }
        ]
      },
      artists: {
        items: [
          {
            id: 'mock-artist-1',
            name: `Mock Artist for "${query}"`,
            genres: ['pop'],
            images: [{ url: 'https://via.placeholder.com/300' }]
          }
        ]
      },
      albums: {
        items: [
          {
            id: 'mock-album-1',
            name: `Mock Album for "${query}"`,
            artists: [{ name: 'Mock Artist' }],
            images: [{ url: 'https://via.placeholder.com/300' }]
          }
        ]
      }
    };
  }

  try {
    const token = await getSpotifyAccessToken();
    const response = await axios.get('https://api.spotify.com/v1/search', {
      headers: { Authorization: `Bearer ${token}` },
      params: { q: query, type, limit }
    });
    return response.data;
  } catch (error) {
    logger.error('Spotify search error:', error.message);
    return {
      tracks: { items: [] },
      artists: { items: [] },
      albums: { items: [] }
    };
  }
}

module.exports = { searchSpotify };
