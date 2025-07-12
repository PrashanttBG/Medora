import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

export default function PrescriptionAnalysis() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-r from-indigo-400 via-purple-500 to-pink-500 flex flex-col items-center justify-center p-8 text-white">
      <motion.h1
        className="text-4xl text-green-600 font-bold mb-8 animate-pulse"
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.8 }}
      >
        Prescription Analysis
      </motion.h1>

      <p className="mb-8 text-lg text-center">
        This feature is under development. Stay tuned!
      </p>

      <button
        onClick={() => navigate('/dashboard')}
        className="bg-green-500 px-6 py-3 rounded-lg hover:bg-green-600 transition"
      >
        Back to Dashboard
      </button>
    </div>
  );
}
