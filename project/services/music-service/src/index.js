require('dotenv').config();
const { initTracing } = require('./observability/tracing');
const { initMetrics } = require('./observability/metrics');
const logger = require('./observability/logger');

initTracing();
const { metricsMiddleware, register } = initMetrics();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const musicRoutes = require('./routes/music');
const { initDatabase } = require('./db');

const app = express();
const PORT = process.env.PORT || 3003;

app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(metricsMiddleware);

app.get('/health', (req, res) => {
  res.json({ status: 'healthy', service: 'music-service' });
});

app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});

app.use('/api/music', musicRoutes);

app.use((err, req, res, next) => {
  logger.error('Error:', { error: err.message, stack: err.stack });
  res.status(err.status || 500).json({ error: err.message || 'Internal server error' });
});

async function start() {
  try {
    await initDatabase();
    app.listen(PORT, () => {
      logger.info(`Music service running on port ${PORT}`);
    });
  } catch (error) {
    logger.error('Failed to start server:', error);
    process.exit(1);
  }
}

start();
