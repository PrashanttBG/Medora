import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../services/api';
import { motion } from 'framer-motion';

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await loginUser(username, password);
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username);
      navigate('/dashboard');
    } catch (err) {
      setError('Invalid username or password');
    }
  };

  return (
    
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500">
     {/* Animated Heading */}
      <motion.h1
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 1 }}
        className="text-6xl font-extrabold mb-4 text-center tracking-widest bg-gradient-to-r from-emerald-600 to-lime-400 bg-clip-text text-transparent"
      >
        <h1 className="text-7xl md:text-8xl font-extrabold text-blue mb-4 ">
            Medora
        </h1>
      </motion.h1>

      {/* Login Card */}
      <form
        onSubmit={handleSubmit}
        className="bg-gradient-to-br from-gray-900 via-purple-900 to-violet-600
        p-10 rounded-3xl shadow-2xl w-full max-w-md mx-auto flex flex-col gap-6 text-white"
      >
        <h2 className="text-2xl font-bold text-white text-center">Login</h2>

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="p-3 rounded-lg bg-white/10 text-white placeholder-white/60 
          focus:outline-none focus:ring-2 focus:ring-violet-400 
          backdrop-blur-sm transition duration-200"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="p-3 rounded-lg bg-white/10 text-white placeholder-white/60 
          focus:outline-none focus:ring-2 focus:ring-violet-400 
          backdrop-blur-sm transition duration-200"
          />

        {error && <p className="text-red-400 text-center">{error}</p>}

        <button
          type="submit"
          className="bg-green-500 p-3 rounded-lg hover:bg-green-600 transition"
        >
          Login
        </button>
      </form>
      <p className="text-center text-bold mt-2">
        New user?{" "}
        <a href="/register" className="text-blue-600 hover:underline">
          Register
        </a>
      </p>
    </div>
  );
}
