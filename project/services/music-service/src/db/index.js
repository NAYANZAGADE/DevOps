sticonst { Pool } = require('pg');
const logger = require('../observability/logger');

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
});

async function initDatabase() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS tracks (
        id SERIAL PRIMARY KEY,
        spotify_track_id VARCHAR(255) UNIQUE NOT NULL,
        name VARCHAR(500) NOT NULL,
        artist VARCHAR(500),
        album VARCHAR(500),
        duration_ms INTEGER,
        preview_url TEXT,
        image_url TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE TABLE IF NOT EXISTS playlists (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL,
        name VARCHAR(255) NOT NULL,
        description TEXT,
        spotify_playlist_id VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE TABLE IF NOT EXISTS playlist_tracks (
        id SERIAL PRIMARY KEY,
        playlist_id INTEGER REFERENCES playlists(id) ON DELETE CASCADE,
        track_id INTEGER REFERENCES tracks(id) ON DELETE CASCADE,
        position INTEGER,
        added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE INDEX IF NOT EXISTS idx_tracks_spotify_id ON tracks(spotify_track_id);
      CREATE INDEX IF NOT EXISTS idx_playlists_user_id ON playlists(user_id);
      CREATE INDEX IF NOT EXISTS idx_playlist_tracks_playlist_id ON playlist_tracks(playlist_id);
    `);
    logger.info('Music database initialized');
  } catch (error) {
    logger.error('Database initialization failed:', error);
    throw error;
  }
}

module.exports = { pool, initDatabase };
