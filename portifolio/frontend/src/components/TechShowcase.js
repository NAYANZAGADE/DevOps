import React from 'react';
import { motion } from 'framer-motion';
import { useInView } from 'react-intersection-observer';
import './TechShowcase.css';

function TechShowcase() {
  const { ref, inView } = useInView({
    threshold: 0.1,
    triggerOnce: true,
  });

  const techStack = [
    { name: 'Kubernetes', icon: '☸️' },
    { name: 'Docker', icon: '🐳' },
    { name: 'Jenkins', icon: '🔧' },
    { name: 'ArgoCD', icon: '🚀' },
    { name: 'Terraform', icon: '🏗️' },
    { name: 'Ansible', icon: '⚙️' },
    { name: 'AWS', icon: '☁️' },
    { name: 'Python', icon: '🐍' },
    { name: 'Bash', icon: '💻' },
    { name: 'Git', icon: '📦' },
    { name: 'GitHub', icon: '🔗' },
    { name: 'Helm', icon: '⎈' }
  ];

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.08
      }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { 
      opacity: 1, 
      y: 0,
      transition: {
        duration: 0.4
      }
    }
  };

  return (
    <div className="tech-showcase" ref={ref}>
      <div className="container">
        <motion.div
          className="tech-header"
          initial={{ opacity: 0, y: 20 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.6 }}
        >
          <h2>Technologies</h2>
        </motion.div>
        
        <motion.div 
          className="tech-tags-container"
          variants={containerVariants}
          initial="hidden"
          animate={inView ? "visible" : "hidden"}
        >
          {techStack.map((tech) => (
            <motion.span
              key={tech.name}
              className="tech-tag-item"
              variants={itemVariants}
              whileHover={{ scale: 1.05, y: -4 }}
            >
              <span style={{ marginRight: '8px', fontSize: '1.2rem' }}>{tech.icon}</span>
              {tech.name}
            </motion.span>
          ))}
        </motion.div>
      </div>
    </div>
  );
}

export default TechShowcase;
