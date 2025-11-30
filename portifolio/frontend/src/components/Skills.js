import React from 'react';
import { motion } from 'framer-motion';
import './Skills.css';

function Skills() {
  const skills = {
    devops: ['CI/CD', 'Progressive Delivery', 'Jenkins', 'Docker', 'Kubernetes', 'Ansible', 'Terraform', 'ArgoCD', 'Helm', 'SonarQube', 'GitOps', 'IaC', 'Pipeline as Code'],
    cloud: ['AWS', 'GCP', 'EC2', 'EKS', 'VPC', 'S3', 'Lambda', 'CloudFormation', 'Karpenter'],
    monitoring: ['Prometheus', 'Grafana', 'CloudWatch', 'ELK Stack', 'DynamoDB'],
    languages: ['Python', 'Groovy DSL', 'Bash', 'YAML', 'JSON'],
    tools: ['Git', 'GitHub', 'Maven', 'Docker Hub', 'Linux', 'Ubuntu']
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.15
      }
    }
  };

  const cardVariants = {
    hidden: { 
      opacity: 0, 
      y: 30 
    },
    visible: { 
      opacity: 1, 
      y: 0,
      transition: {
        duration: 0.5,
        ease: "easeOut"
      }
    }
  };

  return (
    <div className="skills">
      <div className="container">
        <motion.h2
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          Technical Skills
        </motion.h2>
        
        <motion.div 
          className="skills-grid"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.2 }}
        >
          <motion.div
            className="skill-category"
            variants={cardVariants}
          >
            <h3>DevOps</h3>
            <div className="skill-tags">
              {skills.devops.map((skill, i) => (
                <span key={i} className="skill-tag">{skill}</span>
              ))}
            </div>
          </motion.div>

          <motion.div
            className="skill-category"
            variants={cardVariants}
          >
            <h3>Cloud</h3>
            <div className="skill-tags">
              {skills.cloud.map((skill, i) => (
                <span key={i} className="skill-tag">{skill}</span>
              ))}
            </div>
          </motion.div>

          <motion.div
            className="skill-category"
            variants={cardVariants}
          >
            <h3>Monitoring</h3>
            <div className="skill-tags">
              {skills.monitoring.map((skill, i) => (
                <span key={i} className="skill-tag">{skill}</span>
              ))}
            </div>
          </motion.div>

          <motion.div
            className="skill-category"
            variants={cardVariants}
          >
            <h3>Languages</h3>
            <div className="skill-tags">
              {skills.languages.map((skill, i) => (
                <span key={i} className="skill-tag">{skill}</span>
              ))}
            </div>
          </motion.div>

          <motion.div
            className="skill-category"
            variants={cardVariants}
          >
            <h3>Tools</h3>
            <div className="skill-tags">
              {skills.tools.map((skill, i) => (
                <span key={i} className="skill-tag">{skill}</span>
              ))}
            </div>
          </motion.div>
        </motion.div>
      </div>
    </div>
  );
}

export default Skills;
