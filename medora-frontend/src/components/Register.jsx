import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { registerUser } from '../services/api';

export default function Register() {
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.username || !form.email || !form.password || !form.confirmPassword) {
      setError('All fields are required');
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
        await registerUser(form.username, form.email, form.password);
        navigate('/');
      } catch (err) {
        const message = err.response?.data?.message || 'Registration failed';
        setError(message);
      }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500">
      {/* Animated Heading */}
      <motion.h1
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 1 }}
        className="text-6xl font-extrabold mb-12 text-center tracking-widest text-green-400"
      >
        <h1 className="text-7xl md:text-8xl font-extrabold text-blue mb-12">
          Medora
        </h1>
      </motion.h1>

      {/* Register Card */}
      <form
        onSubmit={handleSubmit}
        className="bg-gray-800 p-8 rounded-2xl shadow-lg w-96 flex flex-col gap-6"
      >
        <h2 className="text-2xl font-semibold text-center text-white">Register</h2>

        <input
          type="text"
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          className="p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          className="p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          className="p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
        />

        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          value={form.confirmPassword}
          onChange={handleChange}
          className="p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
        />

        {error && <p className="text-red-400 text-center">{error}</p>}

        <button
          type="submit"
          className="bg-green-500 p-3 rounded-lg hover:bg-green-600 transition"
        >
          Register
        </button>
      </form>

      <p className="text-center text-bold mt-2 text-white">
        Already have an account?{" "}
        <a href="/" className="text-blue-600 hover:underline">
          Login
        </a>
      </p>
    </div>
  );
}
