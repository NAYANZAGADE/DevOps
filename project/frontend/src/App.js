import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import axios from 'axios';

const API_BASE = 'http://localhost';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(null);
  const [currentTrack, setCurrentTrack] = useState(null);
  const [currentAudio, setCurrentAudio] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);

  useEffect(() => {
    if (token) {
      const userData = JSON.parse(localStorage.getItem('user'));
      setUser(userData);
    }
  }, [token]);

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    if (currentAudio) {
      currentAudio.pause();
      setCurrentAudio(null);
      setCurrentTrack(null);
      setIsPlaying(false);
    }
  };

  const playTrack = (track) => {
    if (currentAudio) {
      currentAudio.pause();
    }

    if (currentTrack?.id === track.id && isPlaying) {
      setIsPlaying(false);
      setCurrentTrack(null);
      setCurrentAudio(null);
      return;
    }

    if (track.preview_url) {
      const audio = new Audio(track.preview_url);
      audio.play().catch(err => console.error('Playback failed:', err));
      audio.onended = () => {
        setIsPlaying(false);
      };
      setCurrentAudio(audio);
      setCurrentTrack(track);
      setIsPlaying(true);
    }
  };

  const togglePlayPause = () => {
    if (currentAudio) {
      if (isPlaying) {
        currentAudio.pause();
        setIsPlaying(false);
      } else {
        currentAudio.play();
        setIsPlaying(true);
      }
    }
  };

  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <h1>🎵 Spotify Clone</h1>
          <nav>
            {token ? (
              <>
                <Link to="/dashboard">Dashboard</Link>
                <Link to="/search">Search</Link>
                <Link to="/playlists">Playlists</Link>
              </>
            ) : (
              <>
                <Link to="/login">Login</Link>
                <Link to="/register">Register</Link>
              </>
            )}
          </nav>
          <div>
            {token ? (
              <>
                <span style={{ marginRight: '1rem' }}>{user?.email}</span>
                <button onClick={logout}>Logout</button>
              </>
            ) : (
              <Link to="/login"><button>Get Started</button></Link>
            )}
          </div>
        </nav>

        <Routes>
          <Route path="/" element={token ? <Navigate to="/dashboard" /> : <Navigate to="/login" />} />
          <Route path="/login" element={<Login setToken={setToken} setUser={setUser} />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={token ? <Dashboard /> : <Navigate to="/login" />} />
          <Route path="/search" element={token ? <Search token={token} playTrack={playTrack} currentTrack={currentTrack} isPlaying={isPlaying} /> : <Navigate to="/login" />} />
          <Route path="/playlists" element={token ? <Playlists token={token} /> : <Navigate to="/login" />} />
        </Routes>

        {currentTrack && (
          <div style={{
            position: 'fixed',
            bottom: 0,
            left: 0,
            right: 0,
            background: '#181818',
            borderTop: '1px solid #282828',
            padding: '1rem',
            display: 'flex',
            alignItems: 'center',
            gap: '1rem',
            zIndex: 1000
          }}>
            <img 
              src={currentTrack.album?.images?.[0]?.url || 'https://via.placeholder.com/60'} 
              alt={currentTrack.name}
              style={{ width: '60px', height: '60px', borderRadius: '4px' }}
            />
            <div style={{ flex: 1 }}>
              <h4 style={{ margin: 0, color: 'white' }}>{currentTrack.name}</h4>
              <p style={{ margin: 0, color: '#b3b3b3', fontSize: '0.9rem' }}>
                {currentTrack.artists?.map(a => a.name).join(', ')}
              </p>
            </div>
            <button 
              onClick={togglePlayPause}
              style={{
                background: '#1db954',
                border: 'none',
                borderRadius: '50%',
                width: '50px',
                height: '50px',
                fontSize: '1.5rem',
                cursor: 'pointer',
                color: 'white'
              }}
            >
              {isPlaying ? '⏸' : '▶'}
            </button>
          </div>
        )}
      </div>
    </Router>
  );
}

function Login({ setToken, setUser }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${API_BASE}:3001/api/auth/login`, { email, password });
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      setToken(response.data.token);
      setUser(response.data.user);
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed');
    }
  };

  return (
    <div className="auth-form">
      <h2>Login to Spotify Clone</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Password</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <button type="submit" className="btn">Login</button>
      </form>
      <p style={{ textAlign: 'center', marginTop: '1rem', color: '#b3b3b3' }}>
        Don't have an account? <Link to="/register" style={{ color: '#1db954' }}>Register</Link>
      </p>
    </div>
  );
}

function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${API_BASE}:3001/api/auth/register`, { email, password });
      setSuccess('Registration successful! Please login.');
      setError('');
    } catch (err) {
      setError(err.response?.data?.error || 'Registration failed');
      setSuccess('');
    }
  };

  return (
    <div className="auth-form">
      <h2>Register for Spotify Clone</h2>
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Password</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <button type="submit" className="btn">Register</button>
      </form>
      <p style={{ textAlign: 'center', marginTop: '1rem', color: '#b3b3b3' }}>
        Already have an account? <Link to="/login" style={{ color: '#1db954' }}>Login</Link>
      </p>
    </div>
  );
}

function Dashboard() {
  return (
    <div className="container">
      <h2>Dashboard</h2>
      <div className="dashboard">
        <div className="stat-card">
          <h3>Services Status</h3>
          <p>✅ All Running</p>
        </div>
        <div className="stat-card">
          <h3>Microservices</h3>
          <p>5</p>
        </div>
        <div className="stat-card">
          <h3>Observability</h3>
          <p>Active</p>
        </div>
      </div>
      <div style={{ marginTop: '2rem', background: '#181818', padding: '1.5rem', borderRadius: '8px' }}>
        <h3>Quick Links</h3>
        <ul style={{ marginTop: '1rem', lineHeight: '2' }}>
          <li><a href="http://localhost:16686" target="_blank" rel="noreferrer" style={{ color: '#1db954' }}>Jaeger - Distributed Tracing</a></li>
          <li><a href="http://localhost:9091" target="_blank" rel="noreferrer" style={{ color: '#1db954' }}>Prometheus - Metrics</a></li>
          <li><a href="http://localhost:3000" target="_blank" rel="noreferrer" style={{ color: '#1db954' }}>Grafana - Dashboards</a></li>
        </ul>
      </div>
    </div>
  );
}

function Search({ token, playTrack, currentTrack, isPlaying }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [selectedTrack, setSelectedTrack] = useState(null);

  // Debug: Log when selectedTrack changes
  useEffect(() => {
    console.log('Selected track changed:', selectedTrack);
    if (selectedTrack) {
      console.log('Modal should be visible now!');
      console.log('Track ID:', selectedTrack.id);
      console.log('Track name:', selectedTrack.name);
    }
  }, [selectedTrack]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query) return;
    
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE}:3005/api/search?q=${query}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      console.log('Search response:', response.data);
      console.log('Total tracks:', response.data?.tracks?.items?.length);
      
      // Log preview URL availability
      if (response.data?.tracks?.items) {
        const withPreview = response.data.tracks.items.filter(t => t.preview_url);
        console.log('Tracks with preview:', withPreview.length);
        console.log('Sample track:', response.data.tracks.items[0]);
      }
      
      setResults(response.data);
    } catch (err) {
      console.error('Search failed:', err);
    }
    setLoading(false);
  };

  const getAllTracks = () => {
    if (!results?.tracks?.items) return [];
    return results.tracks.items;
  };

  const allTracks = getAllTracks();
  const tracksWithPreview = allTracks.filter(t => t.preview_url).length;

  return (
    <div className="container">
      {selectedTrack && (
        <div style={{
          position: 'fixed',
          top: '10px',
          right: '10px',
          background: 'red',
          color: 'white',
          padding: '1rem',
          zIndex: 99999,
          borderRadius: '8px'
        }}>
          MODAL STATE: ACTIVE - Track: {selectedTrack.name}
        </div>
      )}
      <h2>Search Music</h2>
      <div style={{ background: '#1a1a1a', padding: '1rem', borderRadius: '8px', marginBottom: '1rem' }}>
        <p style={{ color: '#b3b3b3', margin: 0, fontSize: '0.9rem' }}>
          💡 <strong style={{ color: '#1db954' }}>Tip:</strong> Due to Spotify licensing, not all tracks have 30s previews. 
          Try searching: <strong>"Dua Lipa"</strong>, <strong>"The Weeknd"</strong>, <strong>"Ariana Grande"</strong>, or <strong>"Post Malone"</strong> for better results!
        </p>
      </div>
      <form onSubmit={handleSearch} className="search-bar">
        <input
          type="text"
          placeholder="Search for songs, artists, or albums..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </form>

      {loading && <div className="loading">Searching...</div>}

      {results && results.tracks && (
        <div style={{ marginBottom: '100px' }}>
          <h3 style={{ marginBottom: '0.5rem' }}>Tracks</h3>
          <p style={{ color: '#b3b3b3', fontSize: '0.9rem', marginBottom: '1rem' }}>
            Found {allTracks.length} tracks • {tracksWithPreview} with direct preview
          </p>
          {allTracks.length === 0 ? (
            <p style={{ color: '#b3b3b3', marginTop: '2rem' }}>No tracks found. Try a different search.</p>
          ) : (
            <div className="track-list">
              {allTracks.map((track) => (
                <div key={track.id} className="track-item">
                  <img src={track.album?.images?.[0]?.url || 'https://via.placeholder.com/60'} alt={track.name} />
                  <div className="track-info">
                    <h3>{track.name}</h3>
                    <p>{track.artists?.map(a => a.name).join(', ')}</p>
                    <p style={{ fontSize: '0.8rem', color: '#888', marginTop: '0.25rem' }}>
                      {track.album?.name}
                    </p>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                    {track.preview_url && (
                      <button 
                        className="play-btn" 
                        onClick={() => playTrack(track)}
                        style={{ 
                          background: currentTrack?.id === track.id && isPlaying ? '#1db954' : '#333',
                          fontSize: '1.2rem',
                          width: '45px',
                          height: '45px',
                          borderRadius: '50%',
                          border: 'none',
                          cursor: 'pointer',
                          color: 'white'
                        }}
                        title="Play 30s preview"
                      >
                        {currentTrack?.id === track.id && isPlaying ? '⏸' : '▶'}
                      </button>
                    )}
                    <button 
                      onClick={(e) => {
                        e.stopPropagation();
                        e.preventDefault();
                        console.log('=== PLAY BUTTON CLICKED ===');
                        console.log('Track:', track);
                        console.log('Setting selectedTrack to:', track);
                        setSelectedTrack(track);
                        setTimeout(() => {
                          console.log('After 100ms, selectedTrack should be:', track.name);
                        }, 100);
                      }}
                      style={{ 
                        background: '#1db954',
                        padding: '0.6rem 1.2rem',
                        borderRadius: '20px',
                        border: 'none',
                        cursor: 'pointer',
                        color: 'white',
                        fontWeight: 'bold',
                        fontSize: '0.9rem'
                      }}
                      title="Play with Spotify"
                    >
                      🎵 Play
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {selectedTrack ? (
        <div 
          className="modal-overlay"
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            width: '100vw',
            height: '100vh',
            background: 'rgba(0,0,0,0.95)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 99999,
            backdropFilter: 'blur(5px)'
          }}
          onClick={() => {
            console.log('Overlay clicked - closing modal');
            setSelectedTrack(null);
          }}
        >
          <div 
            className="modal-content"
            style={{
              background: 'linear-gradient(135deg, #282828 0%, #181818 100%)',
              padding: '2rem',
              borderRadius: '16px',
              maxWidth: '500px',
              width: '90%',
              boxShadow: '0 20px 60px rgba(0,0,0,0.8)',
              border: '2px solid #1db954',
              position: 'relative'
            }}
            onClick={(e) => {
              e.stopPropagation();
              console.log('Modal content clicked - not closing');
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div>
                <h3 style={{ margin: 0, color: 'white', fontSize: '1.5rem' }}>🎵 Now Playing</h3>
                <p style={{ margin: '0.5rem 0 0 0', color: '#b3b3b3', fontSize: '0.9rem' }}>
                  {selectedTrack.name} - {selectedTrack.artists?.map(a => a.name).join(', ')}
                </p>
              </div>
              <button 
                onClick={(e) => {
                  e.stopPropagation();
                  console.log('Close button clicked');
                  setSelectedTrack(null);
                }}
                style={{
                  background: '#1db954',
                  border: 'none',
                  color: 'white',
                  fontSize: '1.5rem',
                  cursor: 'pointer',
                  width: '40px',
                  height: '40px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0,
                  marginLeft: '1rem'
                }}
              >
                ✕
              </button>
            </div>
            <div style={{ background: '#000', borderRadius: '12px', overflow: 'hidden' }}>
              <iframe 
                src={`https://open.spotify.com/embed/track/${selectedTrack.id}?utm_source=generator&theme=0`}
                width="100%" 
                height="352" 
                frameBorder="0" 
                allowFullScreen="" 
                allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture" 
                loading="eager"
                style={{ border: 'none', display: 'block' }}
                title="Spotify Player"
              ></iframe>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

function Playlists({ token }) {
  const [playlists, setPlaylists] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        const response = await axios.get(`${API_BASE}:3003/api/music/playlists`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setPlaylists(response.data);
      } catch (err) {
        console.error('Failed to fetch playlists:', err);
      }
      setLoading(false);
    };
    fetchPlaylists();
  }, [token]);

  return (
    <div className="container">
      <h2>My Playlists</h2>
      {loading ? (
        <div className="loading">Loading playlists...</div>
      ) : playlists.length === 0 ? (
        <p style={{ color: '#b3b3b3', marginTop: '2rem' }}>No playlists yet. Create your first playlist!</p>
      ) : (
        <div className="track-list">
          {playlists.map((playlist) => (
            <div key={playlist.id} className="track-item">
              <div className="track-info">
                <h3>{playlist.name}</h3>
                <p>{playlist.description || 'No description'}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
