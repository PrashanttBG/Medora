import { useState } from 'react';
import { motion } from 'framer-motion';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const BASE_URL = "http://localhost:8080";

export default function MedicineDetails() {
  const [medicineName, setMedicineName] = useState('');
  const [file, setFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [dragOver, setDragOver] = useState(false);
  const [language, setLanguage] = useState('English');
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    if (selected) {
      setFile(selected);
      setPreviewUrl(URL.createObjectURL(selected));
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const dropped = e.dataTransfer.files[0];
    if (dropped) {
      setFile(dropped);
      setPreviewUrl(URL.createObjectURL(dropped));
    }
  };

  const handleRemoveFile = () => {
    setFile(null);
    setPreviewUrl('');
  };

  const handleSubmit = async () => {
    if (!medicineName && !file) {
      alert("Please provide a medicine name or upload an image.");
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      if (medicineName.trim()) formData.append("medicineName", medicineName.trim());
      if (file) formData.append("image", file);
      formData.append("language", language);

      const token = localStorage.getItem("token");
      const res = await axios.post(`${BASE_URL}/api/medicine/decode`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      setDetails(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to analyze medicine.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-indigo-400 via-purple-500 to-pink-500 p-8 flex flex-col items-center text-white">
      <motion.h1
        className="text-4xl text-blue-600 font-bold mb-8 animate-pulse"
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 1 }}
      >
        Medicine Details
      </motion.h1>

      <div className="flex flex-col sm:flex-row gap-4 mb-6 w-full max-w-xl">
        <input
          type="text"
          placeholder="Enter medicine name"
          value={medicineName}
          onChange={(e) => setMedicineName(e.target.value)}
          className="p-4 rounded-lg text-black w-full"
        />
        <select
          value={language}
          onChange={(e) => setLanguage(e.target.value)}
          className="p-4 rounded-lg text-black"
        >
          <option>English</option>
          <option>Hindi</option>
          <option>Spanish</option>
          <option>French</option>
          <option>German</option>
          {/* add more languages as needed */}
        </select>
      </div>

      {/* File Upload Box */}
      <div
        onDragOver={(e) => {
          e.preventDefault();
          setDragOver(true);
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
        className={`relative w-full max-w-4xl h-80 p-6 mb-8 border-4 border-dashed rounded-2xl transition flex flex-col items-center justify-center ${
          dragOver ? 'border-purple-500 bg-purple-100' : 'border-gray-300 bg-white'
        }`}
      >
        {!file ? (
          <>
            <p className="text-center text-gray-600 mb-4">
              Drag and drop your image file here
            </p>
            <label className="bg-purple-500 text-white px-4 py-2 rounded cursor-pointer hover:bg-purple-600 transition">
              Browse File
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          </>
        ) : (
          <div className="w-full h-full flex items-center justify-center relative">
            <button
              onClick={handleRemoveFile}
              className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 text-sm shadow"
              title="Remove File"
            >
              🗑️
            </button>
            <img
              src={previewUrl}
              alt="Image Preview"
              className="max-h-full max-w-full rounded shadow"
            />
          </div>
        )}
      </div>

      <button
        onClick={handleSubmit}
        disabled={loading}
        className="bg-white text-blue-600 px-6 py-3 rounded-lg hover:bg-gray-100 transition font-semibold mb-6"
      >
        {loading ? "Analyzing..." : "Analyze Medicine"}
      </button>

      {/* Results */}
      {details && (
        <motion.div
          className="bg-white text-black p-6 rounded-lg shadow-lg w-full max-w-3xl space-y-4"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {Object.entries(details).map(([key, value]) => (
            <div key={key}>
              <h2 className="text-xl font-bold mb-2">{key}</h2>
              <p className="text-gray-700 whitespace-pre-line">{value}</p>
            </div>
          ))}
        </motion.div>
      )}

      <button
        onClick={() => navigate('/dashboard')}
        className="mt-8 bg-green-500 px-6 py-3 rounded-lg hover:bg-green-600 transition"
      >
        Back to Dashboard
      </button>
    </div>
  );
}
