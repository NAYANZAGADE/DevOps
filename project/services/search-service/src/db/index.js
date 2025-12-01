const { MongoClient } = require('mongodb');
const logger = require('../observability/logger');

let db;
let client;

async function initDatabase() {
  try {
    client = new MongoClient(process.env.MONGODB_URL);
    await client.connect();
    db = client.db('spotify');
    
    // Create indexes for search
    await db.collection('tracks').createIndex({ name: 'text', artist: 'text', album: 'text' });
    
    logger.info('Search database initialized');
  } catch (error) {
    logger.error('Database initialization failed:', error);
    throw error;
  }
}

function getDb() {
  return db;
}

module.exports = { initDatabase, getDb };
