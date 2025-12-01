const axios = require('axios');
const logger = require('../observability/logger');

async function authMiddleware(req, res, next) {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    
    if (!token) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const response = await axios.post(
      `${process.env.AUTH_SERVICE_URL}/api/auth/verify`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );

    if (!response.data.valid) {
      return res.status(401).json({ error: 'Invalid token' });
    }

    req.user = response.data.user;
    next();
  } catch (error) {
    logger.error('Auth middleware error:', error.message);
    res.status(401).json({ error: 'Authentication failed' });
  }
}

module.exports = { authMiddleware };
