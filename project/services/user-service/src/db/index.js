const { Pool } = require('pg');
const logger = require('../observability/logger');

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
});

async function initDatabase() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS user_profiles (
        id SERIAL PRIMARY KEY,
        user_id INTEGER UNIQUE NOT NULL,
        display_name VARCHAR(255),
        spotify_user_id VARCHAR(255),
        profile_image_url TEXT,
        country VARCHAR(10),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE TABLE IF NOT EXISTS user_playlists (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL,
        spotify_playlist_id VARCHAR(255) NOT NULL,
        name VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles(user_id);
      CREATE INDEX IF NOT EXISTS idx_user_playlists_user_id ON user_playlists(user_id);
    `);
    logger.info('User database initialized');
  } catch (error) {
    logger.error('Database initialization failed:', error);
    throw error;
  }
}

module.exports = { pool, initDatabase };
