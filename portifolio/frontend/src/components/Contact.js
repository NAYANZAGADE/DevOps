import React from 'react';
import { motion } from 'framer-motion';
import './Contact.css';

function Contact() {
  return (
    <div className="contact">
      <div className="container">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
        >
          Get In Touch
        </motion.h2>

        <motion.div
          className="contact-card"
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <p className="contact-intro">
            I'm currently looking for new opportunities in Cloud & DevOps Engineering. 
            Whether you have a question or just want to say hi, feel free to reach out!
          </p>

          <div className="contact-info">
            <div className="contact-item">
              <a href="mailto:nayanzagade7@gmail.com">nayanzagade7@gmail.com</a>
            </div>
            <div className="contact-item">
              <a href="tel:9960936078">9960936078</a>
            </div>
            <div className="contact-item">
              <a href="https://linkedin.com/in/nayan-zagade-9152b1271" target="_blank" rel="noopener noreferrer">
                LinkedIn
              </a>
            </div>
            <div className="contact-item">
              <a href="https://github.com/NAYANZAGADE" target="_blank" rel="noopener noreferrer">
                GitHub
              </a>
            </div>
            <div className="contact-item">
              <span>Pune, India</span>
            </div>
          </div>

          <div className="certifications">
            <h3>Certifications</h3>
            <ul>
              <li>Advance Certification in Cloud Computing & DevOps In Association With IIT Guwahati - Ethans Tech</li>
              <li>AWS Cloud Technical Essentials - Coursera</li>
              <li>Machine Learning Using Python - D.Y Patil University</li>
            </ul>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

export default Contact;
