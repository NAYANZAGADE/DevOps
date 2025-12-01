import React from 'react';
import { motion } from 'framer-motion';
import { TypeAnimation } from 'react-type-animation';
import './Hero.css';

function Hero() {
  return (
    <div className="hero">
      <div className="container">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
        >
          <motion.div className="hero-badge">
            <span className="badge-icon">☁️</span>
            <span>Cloud & DevOps Engineer</span>
          </motion.div>

          <motion.h1 
            className="hero-title"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            Nayan Zagade
          </motion.h1>
          
          <h2 className="hero-subtitle">
            <TypeAnimation
              sequence={[
                '☸️ Kubernetes Orchestration',
                2000,
                '🐳 Docker Containerization',
                2000,
                '⚙️ CI/CD Pipeline Automation',
                2000,
                '☁️ AWS Cloud Architecture',
                2000,
                '🔧 Infrastructure as Code',
                2000,
              ]}
              wrapper="span"
              speed={50}
              repeat={Infinity}
            />
          </h2>
          
          <motion.p 
            className="hero-description"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1, delay: 0.5 }}
          >
            Entry-Level Cloud & DevOps Engineer with hands-on experience in AWS, Kubernetes, Docker, Jenkins,
            and Terraform. Skilled in building CI/CD pipelines, infrastructure as code (IaC), container
            orchestration, and cost-optimized cloud deployments.
          </motion.p>
          
          <motion.div 
            className="hero-contact"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.8 }}
          >
            <motion.a 
              href="mailto:nayanzagade7@gmail.com"
              whileHover={{ scale: 1.05, y: -2 }}
              whileTap={{ scale: 0.95 }}
            >
              📧 nayanzagade7@gmail.com
            </motion.a>
            <motion.a 
              href="tel:9960936078"
              whileHover={{ scale: 1.05, y: -2 }}
              whileTap={{ scale: 0.95 }}
            >
              📞 9960936078
            </motion.a>
            <motion.a 
              href="https://linkedin.com/in/nayan-zagade-9152b1271" 
              target="_blank" 
              rel="noopener noreferrer"
              whileHover={{ scale: 1.05, y: -2 }}
              whileTap={{ scale: 0.95 }}
            >
              💼 LinkedIn
            </motion.a>
          </motion.div>
        </motion.div>
      </div>
    </div>
  );
}

export default Hero;
