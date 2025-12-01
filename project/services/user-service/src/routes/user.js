const express = require('express');
const { pool } = require('../db');
const { authMiddleware } = require('../middleware/auth');
const { getSpotifyUserProfile } = require('../services/spotify');
const logger = require('../observability/logger');

const router = express.Router();

// Get user profile
router.get('/profile', authMiddleware, async (req, res, next) => {
  try {
    const result = await pool.query(
      'SELECT * FROM user_profiles WHERE user_id = $1',
      [req.user.userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Profile not found' });
    }

    res.json(result.rows[0]);
  } catch (error) {
    next(error);
  }
});

// Create/Update user profile
router.post('/profile', authMiddleware, async (req, res, next) => {
  try {
    const { display_name, spotify_user_id } = req.body;
    
    const result = await pool.query(`
      INSERT INTO user_profiles (user_id, display_name, spotify_user_id, updated_at)
      VALUES ($1, $2, $3, CURRENT_TIMESTAMP)
      ON CONFLICT (user_id) 
      DO UPDATE SET display_name = $2, spotify_user_id = $3, updated_at = CURRENT_TIMESTAMP
      RETURNING *
    `, [req.user.userId, display_name, spotify_user_id]);

    logger.info('Profile updated', { userId: req.user.userId });
    res.json(result.rows[0]);
  } catch (error) {
    next(error);
  }
});

// Sync with Spotify profile
router.post('/sync-spotify', authMiddleware, async (req, res, next) => {
  try {
    const { access_token } = req.body;
    
    if (!access_token) {
      return res.status(400).json({ error: 'Spotify access token required' });
    }

    const spotifyProfile = await getSpotifyUserProfile(access_token);
    
    const result = await pool.query(`
      INSERT INTO user_profiles (user_id, display_name, spotify_user_id, profile_image_url, country, updated_at)
      VALUES ($1, $2, $3, $4, $5, CURRENT_TIMESTAMP)
      ON CONFLICT (user_id)
      DO UPDATE SET 
        display_name = $2, 
        spotify_user_id = $3, 
        profile_image_url = $4,
        country = $5,
        updated_at = CURRENT_TIMESTAMP
      RETURNING *
    `, [
      req.user.userId,
      spotifyProfile.display_name,
      spotifyProfile.id,
      spotifyProfile.images?.[0]?.url,
      spotifyProfile.country
    ]);

    logger.info('Spotify profile synced', { userId: req.user.userId });
    res.json(result.rows[0]);
  } catch (error) {
    next(error);
  }
});

// Get user playlists
router.get('/playlists', authMiddleware, async (req, res, next) => {
  try {
    const result = await pool.query(
      'SELECT * FROM user_playlists WHERE user_id = $1 ORDER BY created_at DESC',
      [req.user.userId]
    );

    res.json(result.rows);
  } catch (error) {
    next(error);
  }
});

module.exports = router;
