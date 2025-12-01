const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

const portfolioData = {
  name: 'Nayan Zagade',
  title: 'Cloud & DevOps Engineer',
  email: 'nayanzagade7@gmail.com',
  phone: '9960936078',
  location: 'Pune, India',
  linkedin: 'https://linkedin.com/in/nayan-zagade-9152b1271',
  github: 'https://github.com/NAYANZAGADE',
  profile: 'Entry-Level Cloud & DevOps Engineer with hands-on experience in AWS, Kubernetes, Docker, Jenkins, and Terraform.',
  skills: {
    devops: ['CI/CD', 'Git', 'Jenkins', 'Docker', 'Kubernetes', 'Ansible', 'Terraform'],
    cloud: ['AWS', 'GCP', 'EC2', 'EKS'],
    languages: ['Python', 'Groovy DSL']
  }
};

app.get('/api/portfolio', (req, res) => {
  res.json(portfolioData);
});

app.get('/api/health', (req, res) => {
  res.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

app.listen(PORT, () => {
  console.log(`Backend API running on port ${PORT}`);
});
