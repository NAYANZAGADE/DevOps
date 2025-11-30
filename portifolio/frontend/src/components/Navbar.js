import React, { useState, useEffect } from 'react';
import { useTheme } from '../context/ThemeContext';
import './Navbar.css';

function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const { isDark, toggleTheme } = useTheme();

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 50);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToSection = (id) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="navbar-container">
        <div className="navbar-logo" onClick={() => scrollToSection('hero')}>
          <div className="logo-text-wrapper">
            <span className="logo-text">Nayan Zagade</span>
            <span className="logo-subtitle">Cloud & DevOps Engineer</span>
          </div>
        </div>
        <ul className="navbar-menu">
          <li onClick={() => scrollToSection('hero')}>Home</li>
          <li onClick={() => scrollToSection('skills')}>Skills</li>
          <li onClick={() => scrollToSection('experience')}>Experience</li>
          <li onClick={() => scrollToSection('projects')}>Projects</li>
          <li onClick={() => scrollToSection('contact')}>Contact</li>
          <li>
            <button className="theme-toggle" onClick={toggleTheme} aria-label="Toggle theme">
              {isDark ? '☀' : '☾'}
            </button>
          </li>
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;
