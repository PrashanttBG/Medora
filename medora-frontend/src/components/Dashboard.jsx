import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';

export default function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-green-400 via-blue-500 to-purple-600 flex flex-col items-center justify-center p-6 text-white">

      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        className="absolute top-5 right-6 bg-gradient-to-r from-blue-500 to-green-400 text-white-700 font-bold px-4 py-2 rounded-full shadow-md hover:bg-gray-100 transition"
        onClick={() => navigate('/documents')}
      >
        Documents
      </motion.button>
      
      <motion.h1
        className="text-5xl font-extrabold bg-gradient-to-r from-purple-600 via-pink-500 to-red-400 bg-clip-text text-transparent mb-20 tracking-wide"
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 1 }}
      >
        Welcome To Medora
      </motion.h1>

      {/* Buttons Section */}
      <div className="flex font-extrabold sm:flex-row gap-9 mb-8">
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="relative bg-white text-blue-800 px-10 py-9 rounded-2xl shadow-lg hover:bg-gray-100 transition"
          onClick={() => navigate('/medicine-details')}
        >
          Medicine Analysis
        </motion.button>

        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className=" relative bg-white text-blue-800 px-10 py-9 rounded-2xl shadow-lg hover:bg-gray-100 transition"
          onClick={ () => navigate('/report-analysis')}
        >
          Report Analysis
        </motion.button>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          
          className=" relative bg-white text-blue-800 px-10 py-9 rounded-2xl shadow-lg hover:bg-gray-100 transition"
          onClick={ () => navigate('/prescription-analysis')}
        >
          Prescription Analysis
          {/* <span className="absolute top-2 right-2 text-xs text-yellow-300 bg-black px-1 rounded">
            Coming Soon
          </span> */}
        </motion.button>

      </div>

      {/* Logout Button */}
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={handleLogout}
        className="mt-6 font-bold bg-red-500 px-6 py-2 rounded-lg hover:bg-red-600 transition"
      >
        Logout
      </motion.button>
    </div>
  );
}
