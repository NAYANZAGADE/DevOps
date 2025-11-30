import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { AnimatePresence, motion } from 'framer-motion';
import './App.css';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import TechShowcase from './components/TechShowcase';
import Skills from './components/Skills';
import Experience from './components/Experience';
import Projects from './components/Projects';
import Contact from './components/Contact';

function App() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:5000';
    axios.get(`${apiUrl}/api/portfolio`)
      .then(res => console.log('Portfolio data loaded:', res.data))
      .catch(err => console.log('API not available:', err))
      .finally(() => {
        setTimeout(() => setLoading(false), 1000);
      });
  }, []);

  return (
    <div className="App">
      <AnimatePresence>
        {loading && (
          <motion.div
            className="loading-screen"
            initial={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.5 }}
          >
            <motion.div
              className="loading-content"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ duration: 0.5 }}
            >
              <motion.div
                className="loading-bar"
                initial={{ width: 0 }}
                animate={{ width: "100%" }}
                transition={{ duration: 1, ease: "easeInOut" }}
              />
              <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.3 }}
              >
                Nayan Zagade
              </motion.p>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {!loading && (
        <>
          <Navbar />
          
          <div className="content">
            <section id="hero"><Hero /></section>
            <TechShowcase />
            <section id="skills"><Skills /></section>
            <section id="experience"><Experience /></section>
            <section id="projects"><Projects /></section>
            <section id="contact"><Contact /></section>
          </div>
        </>
      )}
    </div>
  );
}

export default App;
