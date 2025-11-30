import React from 'react';
import { motion } from 'framer-motion';
import './Projects.css';

function Projects() {
  const projects = [
    {
      title: 'LeaseOasis',
      description: [
        'Designed and deployed multi-architecture EKS clusters (x86 & ARM) on AWS to optimize resource utilization and support diverse workloads',
        'Implemented cost optimization strategies by leveraging EC2 Spot Instances with Karpenter',
        'Configured Argo Rollouts (Canary deployments) to enable progressive delivery with real-time monitoring',
        'Automated cluster scaling and workload scheduling policies to handle varying traffic demands efficiently',
        'Enhanced deployment pipelines with Kubernetes manifests and Helm'
      ]
    },
    {
      title: 'Property Bulls',
      description: [
        'Designed and deployed multi-architecture EKS clusters (x86 & ARM) on AWS',
        'Implemented cost optimization strategies by leveraging EC2 Spot Instances with Karpenter',
        'Configured Argo Rollouts (Canary deployments) for progressive delivery',
        'Automated cluster scaling and workload scheduling policies',
        'Enhanced deployment pipelines with Kubernetes manifests and Helm'
      ]
    },
    {
      title: 'CI/CD Pipeline with Jenkins',
      description: [
        'Designed and implemented a Jenkins Declarative Pipeline to automate the build, test, analysis, package, and deployment lifecycle',
        'Integrated SonarQube for static code analysis to ensure code quality and security',
        'Used Maven for managing the build lifecycle inside the Jenkins pipeline',
        'Built and containerized the application using Docker, and published Docker images to a private Docker registry',
        'Automated deployment of .war artifacts to a remote Apache Tomcat server hosted on AWS EC2'
      ]
    },
    {
      title: 'AWS Infrastructure Deployment using Terraform',
      description: [
        'Deployed a functional AWS Virtual Private Cloud (VPC) using Terraform',
        'Configured essential components such as public subnets, internet gateway, security group, and EC2 instance',
        'Implemented Infrastructure as Code (IaC) best practices'
      ]
    }
  ];

  return (
    <div className="projects">
      <div className="container">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
        >
          Featured Projects
        </motion.h2>

        <div className="projects-grid">
          {projects.map((project, index) => (
            <motion.div
              key={index}
              className="project-card"
              initial={{ opacity: 0, y: 50 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: index * 0.1 }}
            >
              <h3>{project.title}</h3>
              <ul className="project-list">
                {project.description.map((item, i) => (
                  <li key={i}>{item}</li>
                ))}
              </ul>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Projects;
