import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import './InfinityLogo.css';

function InfinityLogo({ size = 200, animated = true, showLabels = true }) {
  const [isHovered, setIsHovered] = useState(false);

  const pathVariants = {
    hidden: { pathLength: 0, opacity: 0 },
    visible: {
      pathLength: 1,
      opacity: 1,
      transition: { duration: 2, ease: "easeInOut" }
    }
  };

  const textVariants = {
    hidden: { opacity: 0 },
    visible: (i) => ({
      opacity: 1,
      transition: { delay: 1.5 + i * 0.1, duration: 0.5 }
    })
  };

  return (
    <motion.div 
      className="infinity-logo-container"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <svg 
        width={size} 
        height={size * 0.5} 
        viewBox="0 0 400 200" 
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Infinity Loop Path - Perfect Figure 8 */}
        <motion.path
          d="M 50,100 C 50,40 80,20 110,50 C 140,80 160,100 200,100 C 240,100 260,80 290,50 C 320,20 350,40 350,100 C 350,160 320,180 290,150 C 260,120 240,100 200,100 C 160,100 140,120 110,150 C 80,180 50,160 50,100 Z"
          stroke="var(--accent-primary)"
          strokeWidth="20"
          fill="none"
          strokeLinecap="round"
          strokeLinejoin="round"
          variants={animated ? pathVariants : {}}
          initial="hidden"
          animate="visible"
        />
        
        {/* White circles for text background */}
        <circle cx="100" cy="100" r="45" fill="var(--bg-primary)" />
        <circle cx="300" cy="100" r="45" fill="var(--bg-primary)" />
        
        {/* DEV Text */}
        <motion.text
          x="100"
          y="112"
          fontSize="30"
          fontWeight="900"
          fill="var(--text-primary)"
          textAnchor="middle"
          custom={0}
          variants={animated ? textVariants : {}}
          initial="hidden"
          animate="visible"
        >
          DEV
        </motion.text>
        
        {/* OPS Text */}
        <motion.text
          x="300"
          y="112"
          fontSize="30"
          fontWeight="900"
          fill="var(--text-primary)"
          textAnchor="middle"
          custom={1}
          variants={animated ? textVariants : {}}
          initial="hidden"
          animate="visible"
        >
          OPS
        </motion.text>
        
        {showLabels && (
          <>
            {/* Center Arrows */}
            <motion.path
              d="M 175,100 L 195,100 M 190,95 L 195,100 L 190,105"
              stroke="var(--text-secondary)"
              strokeWidth="3"
              fill="none"
              strokeLinecap="round"
              strokeLinejoin="round"
              custom={2}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            />
            
            <motion.path
              d="M 225,100 L 205,100 M 210,95 L 205,100 L 210,105"
              stroke="var(--text-secondary)"
              strokeWidth="3"
              fill="none"
              strokeLinecap="round"
              strokeLinejoin="round"
              custom={3}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            />
            
            {/* Labels */}
            <motion.text
              x="100"
              y="30"
              fontSize="11"
              fontWeight="600"
              fill="var(--text-secondary)"
              textAnchor="middle"
              custom={4}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            >
              Plan / Code
            </motion.text>
            
            <motion.text
              x="100"
              y="185"
              fontSize="11"
              fontWeight="600"
              fill="var(--text-secondary)"
              textAnchor="middle"
              custom={5}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            >
              Build / Integrate
            </motion.text>
            
            <motion.text
              x="300"
              y="30"
              fontSize="11"
              fontWeight="600"
              fill="var(--text-secondary)"
              textAnchor="middle"
              custom={6}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            >
              Test / Release
            </motion.text>
            
            <motion.text
              x="300"
              y="185"
              fontSize="11"
              fontWeight="600"
              fill="var(--text-secondary)"
              textAnchor="middle"
              custom={7}
              variants={animated ? textVariants : {}}
              initial="hidden"
              animate="visible"
            >
              Deploy / Monitor
            </motion.text>
          </>
        )}
      </svg>
    </motion.div>
  );
}

export default InfinityLogo;
