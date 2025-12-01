const axios = require('axios');
const logger = require('../observability/logger');

let accessToken = null;
let tokenExpiry = null;

// Mock data for when Spotify API is not configured
const mockTracks = {
  default: {
    id: 'mock-track-1',
    name: 'Mock Song',
    artists: [{ name: 'Mock Artist' }],
    album: {
      name: 'Mock Album',
      images: [{ url: 'https://via.placeholder.com/300' }]
    },
    duration_ms: 180000,
    preview_url: 'https://example.com/preview.mp3'
  }
};

const USE_MOCK = !process.env.SPOTIFY_CLIENT_ID || process.env.SPOTIFY_CLIENT_ID === 'your_spotify_client_id_here';

async function getSpotifyAccessToken() {
  if (USE_MOCK) {
    logger.warn('Using mock Spotify data - no API credentials configured');
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
    logger.error('Failed to get Spotify token, using mock data:', error.message);
    return 'mock-token';
  }
}

async function getSpotifyTrack(trackId) {
  if (USE_MOCK) {
    return {
      ...mockTracks.default,
      id: trackId,
      name: `Mock Track ${trackId.substring(0, 8)}`
    };
  }

  try {
    const token = await getSpotifyAccessToken();
    const response = await axios.get(`https://api.spotify.com/v1/tracks/${trackId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (error) {
    logger.error('Spotify API error, using mock data:', error.message);
    return {
      ...mockTracks.default,
      id: trackId,
      name: `Mock Track ${trackId.substring(0, 8)}`
    };
  }
}

async function getSpotifyAlbum(albumId) {
  if (USE_MOCK) {
    return {
      id: albumId,
      name: `Mock Album ${albumId.substring(0, 8)}`,
      artists: [{ name: 'Mock Artist' }],
      images: [{ url: 'https://via.placeholder.com/300' }],
      tracks: { items: [] }
    };
  }

  try {
    const token = await getSpotifyAccessToken();
    const response = await axios.get(`https://api.spotify.com/v1/albums/${albumId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (error) {
    logger.error('Spotify API error, using mock data:', error.message);
    return {
      id: albumId,
      name: `Mock Album ${albumId.substring(0, 8)}`,
      artists: [{ name: 'Mock Artist' }],
      images: [{ url: 'https://via.placeholder.com/300' }],
      tracks: { items: [] }
    };
  }
}

async function getSpotifyArtist(artistId) {
  if (USE_MOCK) {
    return {
      id: artistId,
      name: `Mock Artist ${artistId.substring(0, 8)}`,
      genres: ['pop', 'rock'],
      images: [{ url: 'https://via.placeholder.com/300' }]
    };
  }

  try {
    const token = await getSpotifyAccessToken();
    const response = await axios.get(`https://api.spotify.com/v1/artists/${artistId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (error) {
    logger.error('Spotify API error, using mock data:', error.message);
    return {
      id: artistId,
      name: `Mock Artist ${artistId.substring(0, 8)}`,
      genres: ['pop', 'rock'],
      images: [{ url: 'https://via.placeholder.com/300' }]
    };
  }
}

module.exports = {
  getSpotifyTrack,
  getSpotifyAlbum,
  getSpotifyArtist
};
