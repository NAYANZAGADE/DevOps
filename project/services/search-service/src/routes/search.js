const express = require('express');
const { getDb } = require('../db');
const { authMiddleware } = require('../middleware/auth');
const { searchSpotify } = require('../services/spotify');
const { getCached, setCached } = require('../services/cache');
const logger = require('../observability/logger');

const router = express.Router();

// Search tracks, artists, albums
router.get('/', authMiddleware, async (req, res, next) => {
  try {
    const { q, type = 'track,artist,album', limit = 20 } = req.query;
    
    if (!q) {
      return res.status(400).json({ error: 'Query parameter "q" is required' });
    }

    // Check cache
    const cacheKey = `search:${q}:${type}:${limit}`;
    const cached = await getCached(cacheKey);
    if (cached) {
      logger.info('Search cache hit', { query: q });
      return res.json(JSON.parse(cached));
    }

    // Search Spotify
    const results = await searchSpotify(q, type, limit);
    
    // Cache results
    await setCached(cacheKey, JSON.stringify(results), 1800);
    
    // Store in MongoDB for analytics
    const db = getDb();
    await db.collection('search_history').insertOne({
      user_id: req.user.userId,
      query: q,
      type,
      results_count: results.tracks?.items?.length || 0,
      timestamp: new Date()
    });

    logger.info('Search completed', { query: q, userId: req.user.userId });
    res.json(results);
  } catch (error) {
    next(error);
  }
});

// Get search history
router.get('/history', authMiddleware, async (req, res, next) => {
  try {
    const db = getDb();
    const history = await db.collection('search_history')
      .find({ user_id: req.user.userId })
      .sort({ timestamp: -1 })
      .limit(50)
      .toArray();

    res.json(history);
  } catch (error) {
    next(error);
  }
});

module.exports = router;
