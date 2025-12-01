import React, { useRef } from 'react';
import { useFrame } from '@react-three/fiber';
import { Float, Sphere, Box, Torus } from '@react-three/drei';

// Simple DevOps Icon
function SimpleIcon({ position, color, shape = 'sphere' }) {
  const meshRef = useRef();

  useFrame((state) => {
    if (meshRef.current) {
      meshRef.current.rotation.x += 0.01;
      meshRef.current.rotation.y += 0.01;
    }
  });

  return (
    <Float speed={2} rotationIntensity={0.5} floatIntensity={2}>
      <group position={position}>
        {shape === 'sphere' && (
          <Sphere ref={meshRef} args={[0.3, 16, 16]}>
            <meshStandardMaterial color={color} wireframe />
          </Sphere>
        )}
        {shape === 'box' && (
          <Box ref={meshRef} args={[0.5, 0.5, 0.5]}>
            <meshStandardMaterial color={color} wireframe />
          </Box>
        )}
        {shape === 'torus' && (
          <Torus ref={meshRef} args={[0.3, 0.1, 16, 32]}>
            <meshStandardMaterial color={color} wireframe />
          </Torus>
        )}
      </group>
    </Float>
  );
}





function Scene3D() {
  const groupRef = useRef();

  useFrame(() => {
    if (groupRef.current) {
      groupRef.current.rotation.y += 0.001;
    }
  });

  return (
    <>
      {/* Lighting */}
      <ambientLight intensity={0.5} />
      <pointLight position={[10, 10, 10]} intensity={1} color="#00d4ff" />
      <pointLight position={[-10, -10, -10]} intensity={0.5} color="#ff6b6b" />

      <group ref={groupRef}>
        {/* Tech Stack Icons - Simple wireframe shapes */}
        <SimpleIcon position={[-3, 2, -2]} color="#326CE5" shape="sphere" />
        <SimpleIcon position={[3, 2, -2]} color="#2496ED" shape="box" />
        <SimpleIcon position={[-3, -2, -2]} color="#D24939" shape="torus" />
        <SimpleIcon position={[3, -2, -2]} color="#F05032" shape="sphere" />
        <SimpleIcon position={[0, 3, -3]} color="#6e5494" shape="box" />
        <SimpleIcon position={[0, -3, -3]} color="#7B42BC" shape="torus" />
        <SimpleIcon position={[-4, 0, -3]} color="#FCC624" shape="sphere" />
        <SimpleIcon position={[4, 0, -3]} color="#EF7B4D" shape="box" />
      </group>

      {/* Infinity loop shape */}
      <mesh position={[0, 0, -4]} rotation={[0, 0, 0]}>
        <torusGeometry args={[2, 0.05, 16, 100]} />
        <meshStandardMaterial color="#00d4ff" emissive="#00d4ff" emissiveIntensity={0.5} />
      </mesh>

      <mesh position={[0, 0, -4]} rotation={[Math.PI / 2, 0, 0]}>
        <torusGeometry args={[1.5, 0.05, 16, 100]} />
        <meshStandardMaterial color="#4ecdc4" emissive="#4ecdc4" emissiveIntensity={0.5} />
      </mesh>
    </>
  );
}

export default Scene3D;
