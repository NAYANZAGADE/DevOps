import React, { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useInView } from 'react-intersection-observer';
import './TechStack.css';

function TechStack() {
  const [hoveredTech, setHoveredTech] = useState(null);
  const { ref, inView } = useInView({
    threshold: 0.1,
    triggerOnce: false,
  });

  const techStack = [
    { name: 'Kubernetes', logo: '/logos/kubernetes.svg', color: '#326CE5', url: 'https://kubernetes.io', desc: 'Container Orchestration' },
    { name: 'Docker', logo: '/logos/docker.svg', color: '#2496ED', url: 'https://docker.com', desc: 'Containerization' },
    { name: 'Jenkins', logo: '/logos/jenkins.svg', color: '#D24939', url: 'https://jenkins.io', desc: 'CI/CD Automation' },
    { name: 'ArgoCD', logo: '/logos/argocd.svg', color: '#EF7B4D', url: 'https://argo-cd.readthedocs.io', desc: 'GitOps CD' },
    { name: 'Terraform', logo: '/logos/terraform.svg', color: '#7B42BC', url: 'https://terraform.io', desc: 'Infrastructure as Code' },
    { name: 'Ansible', logo: '/logos/ansible.svg', color: '#EE0000', url: 'https://ansible.com', desc: 'Configuration Management' },
    { name: 'AWS', logo: '/logos/aws.svg', color: '#FF9900', url: 'https://aws.amazon.com', desc: 'Cloud Platform' },
    { name: 'Python', logo: '/logos/python.svg', color: '#3776AB', url: 'https://python.org', desc: 'Programming' },
    { name: 'Bash', logo: '/logos/bash.svg', color: '#4EAA25', url: 'https://gnu.org/software/bash', desc: 'Shell Scripting' },
    { name: 'Groovy', logo: '/logos/groovy.svg', color: '#4298B8', url: 'https://groovy-lang.org', desc: 'Jenkins Pipeline' },
    { name: 'Git', logo: '/logos/git.svg', color: '#F05032', url: 'https://git-scm.com', desc: 'Version Control' },
    { name: 'GitHub', logo: '/logos/github.svg', color: '#181717', url: 'https://github.com', desc: 'Code Repository' },
    { name: 'Ubuntu', logo: '/logos/linux.svg', color: '#E95420', url: 'https://ubuntu.com', desc: 'Linux OS' },
    { name: 'Prometheus', logo: '/logos/prometheus.svg', color: '#E6522C', url: 'https://prometheus.io', desc: 'Monitoring' },
    { name: 'Grafana', logo: '/logos/grafana.svg', color: '#F46800', url: 'https://grafana.com', desc: 'Visualization' },
    { name: 'YAML', logo: '/logos/yaml.svg', color: '#CB171E', url: 'https://yaml.org', desc: 'Configuration' },
  ];

  const handleMouseEnter = useCallback((tech) => {
    setHoveredTech(tech);
  }, []);

  const handleMouseLeave = useCallback(() => {
    setHoveredTech(null);
  }, []);

  return (
    <div className="tech-stack-container" ref={ref}>
      <div className="tech-stack-grid">
        {techStack.map((tech, index) => (
          <motion.a
            key={tech.name}
            href={tech.url}
            target="_blank"
            rel="noopener noreferrer"
            className="tech-item"
            initial={{ opacity: 0, scale: 0.8, y: 20 }}
            animate={inView ? { 
              opacity: 1, 
              scale: 1, 
              y: 0,
              transition: {
                duration: 0.5,
                delay: index * 0.05,
                ease: [0.43, 0.13, 0.23, 0.96]
              }
            } : {}}
            whileHover={{ 
              scale: 1.1, 
              y: -8,
              transition: { duration: 0.2 }
            }}
            whileTap={{ scale: 0.95 }}
            onMouseEnter={() => handleMouseEnter(tech)}
            onMouseLeave={handleMouseLeave}
          >
            <motion.div 
              className="tech-logo-wrapper" 
              style={{ '--tech-color': tech.color }}
              animate={{
                boxShadow: hoveredTech?.name === tech.name 
                  ? `0 8px 30px ${tech.color}40` 
                  : '0 1px 3px var(--shadow-color)'
              }}
            >
              <img src={tech.logo} alt={tech.name} className="tech-logo" />
            </motion.div>
            <span className="tech-name">{tech.name}</span>
            
            <AnimatePresence>
              {hoveredTech?.name === tech.name && (
                <motion.div
                  className="tech-tooltip"
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: 10 }}
                  transition={{ duration: 0.2 }}
                >
                  {tech.desc}
                </motion.div>
              )}
            </AnimatePresence>
          </motion.a>
        ))}
      </div>
      
      <div className="devops-infinity">
        <svg viewBox="0 0 200 100" className="infinity-svg">
          <path
            d="M 20,50 C 20,20 40,20 50,50 C 60,80 80,80 100,50 C 120,20 140,20 150,50 C 160,80 180,80 180,50"
            fill="none"
            stroke="var(--accent-primary)"
            strokeWidth="3"
            className="infinity-path"
          />
        </svg>
        <div className="devops-text">
          <span className="dev">Dev</span>
          <span className="ops">Ops</span>
        </div>
      </div>
    </div>
  );
}

export default TechStack;
