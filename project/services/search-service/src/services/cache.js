const redis = require('redis');
const logger = require('../observability/logger');

let client;

async function initRedis() {
  if (!client) {
    client = redis.createClient({ url: process.env.REDIS_URL });
    client.on('error', (err) => logger.error('Redis error:', err));
    await client.connect();
  }
  return client;
}

async function getCached(key) {
  try {
    const redisClient = await initRedis();
    return await redisClient.get(key);
  } catch (error) {
    logger.error('Cache get error:', error);
    return null;
  }
}

async function setCached(key, value, ttl = 3600) {
  try {
    const redisClient = await initRedis();
    await redisClient.setEx(key, ttl, value);
  } catch (error) {
    logger.error('Cache set error:', error);
  }
}

module.exports = { getCached, setCached };
