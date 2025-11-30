import React, { useEffect, useRef } from 'react';
import { useTheme } from '../context/ThemeContext';
import './DevOpsAnimation.css';

function DevOpsAnimation() {
  const canvasRef = useRef(null);
  const { isDark } = useTheme();

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    // Tech stack items
    const techStack = [
      { name: 'Kubernetes', color: '#326CE5', icon: '☸', angle: 0 },
      { name: 'Docker', color: '#2496ED', icon: '🐳', angle: Math.PI / 4 },
      { name: 'Jenkins', color: '#D24939', icon: '⚙️', angle: Math.PI / 2 },
      { name: 'Git', color: '#F05032', icon: '⎇', angle: (3 * Math.PI) / 4 },
      { name: 'GitHub', color: '#6e5494', icon: '⚡', angle: Math.PI },
      { name: 'Terraform', color: '#7B42BC', icon: '▲', angle: (5 * Math.PI) / 4 },
      { name: 'Linux', color: '#FCC624', icon: '🐧', angle: (3 * Math.PI) / 2 },
      { name: 'ArgoCD', color: '#EF7B4D', icon: '🔄', angle: (7 * Math.PI) / 4 },
    ];

    let animationFrame;
    let time = 0;

    function drawInfinityLoop(centerX, centerY, scale, rotation) {
      ctx.save();
      ctx.translate(centerX, centerY);
      ctx.rotate(rotation);
      
      ctx.beginPath();
      const loopColor = isDark ? '#00d4ff' : '#0066cc';
      ctx.strokeStyle = loopColor;
      ctx.lineWidth = 3;
      ctx.shadowBlur = 20;
      ctx.shadowColor = loopColor;

      for (let t = 0; t <= 2 * Math.PI; t += 0.01) {
        const x = scale * Math.cos(t) / (1 + Math.sin(t) * Math.sin(t));
        const y = scale * Math.sin(t) * Math.cos(t) / (1 + Math.sin(t) * Math.sin(t));
        
        if (t === 0) {
          ctx.moveTo(x, y);
        } else {
          ctx.lineTo(x, y);
        }
      }
      
      ctx.stroke();
      ctx.restore();
    }

    function drawTechIcon(x, y, tech, scale) {
      // Glow effect
      ctx.shadowBlur = 30;
      ctx.shadowColor = tech.color;

      // Circle background
      ctx.beginPath();
      ctx.arc(x, y, 40 * scale, 0, Math.PI * 2);
      ctx.fillStyle = tech.color + '20';
      ctx.fill();

      // Circle border
      ctx.beginPath();
      ctx.arc(x, y, 40 * scale, 0, Math.PI * 2);
      ctx.strokeStyle = tech.color;
      ctx.lineWidth = 3;
      ctx.stroke();

      // Icon
      ctx.shadowBlur = 0;
      ctx.font = `${30 * scale}px Arial`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillStyle = tech.color;
      ctx.fillText(tech.icon, x, y);

      // Label
      ctx.font = `${14 * scale}px Arial`;
      ctx.fillStyle = isDark ? '#ffffff' : '#1a1a1a';
      ctx.fillText(tech.name, x, y + 60 * scale);
    }

    function animate() {
      ctx.fillStyle = isDark ? 'rgba(10, 14, 39, 0.1)' : 'rgba(255, 255, 255, 0.1)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      const centerX = canvas.width / 2;
      const centerY = canvas.height / 2;
      const radius = Math.min(canvas.width, canvas.height) * 0.3;

      // Draw infinity loop
      drawInfinityLoop(centerX, centerY, 150, time * 0.001);

      // Draw tech icons in circular arrangement
      techStack.forEach((tech, index) => {
        const angle = tech.angle + time * 0.0005;
        const x = centerX + Math.cos(angle) * radius;
        const y = centerY + Math.sin(angle) * radius;
        const scale = 1 + Math.sin(time * 0.002 + index) * 0.1;
        
        drawTechIcon(x, y, tech, scale);

        // Draw connecting lines
        const nextIndex = (index + 1) % techStack.length;
        const nextAngle = techStack[nextIndex].angle + time * 0.0005;
        const nextX = centerX + Math.cos(nextAngle) * radius;
        const nextY = centerY + Math.sin(nextAngle) * radius;

        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(nextX, nextY);
        ctx.strokeStyle = isDark ? '#00d4ff40' : '#0066cc40';
        ctx.lineWidth = 2;
        ctx.setLineDash([5, 5]);
        ctx.stroke();
        ctx.setLineDash([]);
      });

      // Draw particles
      for (let i = 0; i < 50; i++) {
        const px = centerX + Math.cos(time * 0.001 + i) * (radius + 100);
        const py = centerY + Math.sin(time * 0.001 + i) * (radius + 100);
        
        ctx.beginPath();
        ctx.arc(px, py, 2, 0, Math.PI * 2);
        ctx.fillStyle = isDark ? '#00d4ff80' : '#0066cc80';
        ctx.fill();
      }

      time++;
      animationFrame = requestAnimationFrame(animate);
    }

    animate();

    const handleResize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    window.addEventListener('resize', handleResize);

    return () => {
      cancelAnimationFrame(animationFrame);
      window.removeEventListener('resize', handleResize);
    };
  }, [isDark]);

  return <canvas ref={canvasRef} className="devops-animation-canvas" />;
}

export default DevOpsAnimation;
