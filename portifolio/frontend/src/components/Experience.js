import React from 'react';
import { motion } from 'framer-motion';
import './Experience.css';

function Experience() {
  return (
    <div className="experience">
      <div className="container">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
        >
          Professional Experience
        </motion.h2>

        <motion.div
          className="experience-card"
          initial={{ opacity: 0, y: 50 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <div className="experience-header">
            <div>
              <h3>DevOps Engineer - Intern</h3>
              <p className="company">HumanCloud Technologies</p>
            </div>
            <span className="date">07/2025 - Present</span>
          </div>
          <ul className="experience-list">
            <li>Implemented cost optimization strategies on AWS, reducing infrastructure spending by optimizing EC2/EKS workloads and leveraging auto-scaling solutions</li>
            <li>Deployed and managed EKS clusters with Karpenter, enabling efficient SPOT node provisioning for scalable applications</li>
            <li>Configured and implemented advanced deployment strategies (blue-green and canary), ensuring zero-downtime releases</li>
            <li>Designed and implemented a centralized logging solution, exporting logs to Amazon S3 with KMS encryption for security and compliance</li>
            <li>Integrated DynamoDB for scalable and reliable metadata storage to support log management workflows</li>
            <li>Collaborated with cross-functional teams to automate CI/CD pipelines and improve release efficiency using Helm and Kubernetes manifests</li>
            <li>Gained hands-on experience with cloud-native monitoring, IaC practices, and container orchestration</li>
          </ul>
        </motion.div>
      </div>
    </div>
  );
}

export default Experience;
