const express = require('express');
const axios = require('axios');
const { authMiddleware } = require('../middleware/auth');
const { getCached, setCached } = require('../services/cache');
const logger = require('../observability/logger');

const router = express.Router();

// Get streaming URL for a track
router.get('/play/:trackId', authMiddleware, async (req, res, next) => {
  try {
    const { trackId } = req.params;
    
    // Check cache
    const cached = await getCached(`stream:${trackId}`);
    if (cached) {
      return res.json(JSON.parse(cached));
    }

    // Get track info from music service
    const musicResponse = await axios.get(
      `${process.env.MUSIC_SERVICE_URL}/api/music/tracks/${trackId}`,
      { headers: { Authorization: req.headers.authorization } }
    );

    const track = musicResponse.data;
    
    // In production, this would return actual streaming URL
    // For now, return preview URL from Spotify
    const streamData = {
      track_id: track.spotify_track_id,
      name: track.name,
      artist: track.artist,
      preview_url: track.preview_url || 'https://example.com/mock-stream.mp3',
      duration_ms: track.duration_ms,
      image_url: track.image_url
    };

    await setCached(`stream:${trackId}`, JSON.stringify(streamData), 300);
    
    logger.info('Stream requested', { trackId, userId: req.user.userId });
    res.json(streamData);
  } catch (error) {
    next(error);
  }
});

// Record playback event
router.post('/playback', authMiddleware, async (req, res, next) => {
  try {
    const { track_id, position_ms, event_type } = req.body;
    
    // Store playback event (could be sent to analytics service)
    logger.info('Playback event', {
      userId: req.user.userId,
      trackId: track_id,
      position: position_ms,
      event: event_type
    });

    res.json({ message: 'Playback event recorded' });
  } catch (error) {
    next(error);
  }
});

// Get currently playing
router.get('/now-playing', authMiddleware, async (req, res, next) => {
  try {
    const cached = await getCached(`now-playing:${req.user.userId}`);
    
    if (cached) {
      return res.json(JSON.parse(cached));
    }

    res.json({ playing: false });
  } catch (error) {
    next(error);
  }
});

module.exports = router;
