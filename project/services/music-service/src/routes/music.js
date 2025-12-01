const express = require('express');
const { pool } = require('../db');
const { authMiddleware } = require('../middleware/auth');
const { getSpotifyTrack, getSpotifyAlbum, getSpotifyArtist } = require('../services/spotify');
const { getCached, setCached } = require('../services/cache');
const logger = require('../observability/logger');

const router = express.Router();

// Get track by Spotify ID
router.get('/tracks/:spotifyId', authMiddleware, async (req, res, next) => {
  try {
    const { spotifyId } = req.params;
    
    // Check cache
    const cached = await getCached(`track:${spotifyId}`);
    if (cached) {
      return res.json(JSON.parse(cached));
    }

    // Check database
    let result = await pool.query(
      'SELECT * FROM tracks WHERE spotify_track_id = $1',
      [spotifyId]
    );

    if (result.rows.length === 0) {
      // Fetch from Spotify API
      const spotifyTrack = await getSpotifyTrack(spotifyId);
      
      result = await pool.query(`
        INSERT INTO tracks (spotify_track_id, name, artist, album, duration_ms, preview_url, image_url)
        VALUES ($1, $2, $3, $4, $5, $6, $7)
        RETURNING *
      `, [
        spotifyTrack.id,
        spotifyTrack.name,
        spotifyTrack.artists.map(a => a.name).join(', '),
        spotifyTrack.album.name,
        spotifyTrack.duration_ms,
        spotifyTrack.preview_url,
        spotifyTrack.album.images?.[0]?.url
      ]);
    }

    const track = result.rows[0];
    await setCached(`track:${spotifyId}`, JSON.stringify(track), 3600);
    
    res.json(track);
  } catch (error) {
    next(error);
  }
});

// Create playlist
router.post('/playlists', authMiddleware, async (req, res, next) => {
  try {
    const { name, description } = req.body;
    
    const result = await pool.query(`
      INSERT INTO playlists (user_id, name, description)
      VALUES ($1, $2, $3)
      RETURNING *
    `, [req.user.userId, name, description]);

    logger.info('Playlist created', { playlistId: result.rows[0].id, userId: req.user.userId });
    res.status(201).json(result.rows[0]);
  } catch (error) {
    next(error);
  }
});

// Get user playlists
router.get('/playlists', authMiddleware, async (req, res, next) => {
  try {
    const result = await pool.query(
      'SELECT * FROM playlists WHERE user_id = $1 ORDER BY created_at DESC',
      [req.user.userId]
    );

    res.json(result.rows);
  } catch (error) {
    next(error);
  }
});

// Add track to playlist
router.post('/playlists/:playlistId/tracks', authMiddleware, async (req, res, next) => {
  try {
    const { playlistId } = req.params;
    const { spotify_track_id } = req.body;

    // Verify playlist ownership
    const playlistResult = await pool.query(
      'SELECT * FROM playlists WHERE id = $1 AND user_id = $2',
      [playlistId, req.user.userId]
    );

    if (playlistResult.rows.length === 0) {
      return res.status(404).json({ error: 'Playlist not found' });
    }

    // Get or create track
    let trackResult = await pool.query(
      'SELECT id FROM tracks WHERE spotify_track_id = $1',
      [spotify_track_id]
    );

    let trackId;
    if (trackResult.rows.length === 0) {
      const spotifyTrack = await getSpotifyTrack(spotify_track_id);
      trackResult = await pool.query(`
        INSERT INTO tracks (spotify_track_id, name, artist, album, duration_ms, preview_url, image_url)
        VALUES ($1, $2, $3, $4, $5, $6, $7)
        RETURNING id
      `, [
        spotifyTrack.id,
        spotifyTrack.name,
        spotifyTrack.artists.map(a => a.name).join(', '),
        spotifyTrack.album.name,
        spotifyTrack.duration_ms,
        spotifyTrack.preview_url,
        spotifyTrack.album.images?.[0]?.url
      ]);
    }
    trackId = trackResult.rows[0].id;

    // Add to playlist
    await pool.query(`
      INSERT INTO playlist_tracks (playlist_id, track_id)
      VALUES ($1, $2)
    `, [playlistId, trackId]);

    logger.info('Track added to playlist', { playlistId, trackId });
    res.status(201).json({ message: 'Track added to playlist' });
  } catch (error) {
    next(error);
  }
});

module.exports = router;
