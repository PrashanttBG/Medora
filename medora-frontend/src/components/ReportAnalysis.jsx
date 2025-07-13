import { useState } from 'react';
import { analyzeImageReport, analyzeReport } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';

export default function Reportsummary() {
  const [file, setFile] = useState(null);
  const [summary, setSummary] = useState(null);
  const [dragOver, setDragOver] = useState(false);
  const [recommendedDoctor, setRecommendedDoctor] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [previewUrl, setPreviewUrl] = useState(null);

  const handleAnalyze = async () => {
    if (!file) return;
  
    setLoading(true);
    setError('');
    setSummary('');
    setRecommendedDoctor('');
  
    try {
      let data;
      const fileType = file.type;
  
      if (fileType === 'application/pdf') {
        data = await analyzeReport(file);
      } else if (fileType.startsWith('image/')) {
        data = await analyzeImageReport(file);
      } else {
        throw new Error('Unsupported file type');
      }
  
      setSummary(data.summary);
      setRecommendedDoctor(data.specialist || data.recommendedDoctor);
      console.log('Response from backend: ', data);
    } catch (err) {
      console.error('Error analyzing report:', err);
      setError('Failed to analyze report. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  
  const handleRemoveFile = () => {
    setFile(null);
    setPreviewUrl(null);
    setSummary('');
    setRecommendedDoctor('');
    setError('');
  };
  

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const droppedFile = e.dataTransfer.files[0];
    const isPdf = droppedFile?.type === 'application/pdf';
    const isImage = droppedFile?.type?.startsWith('image/');
  
    if (droppedFile && (isPdf || isImage)) {
      setFile(droppedFile);
      setError('');
      if (isImage) {
        const imageUrl = URL.createObjectURL(droppedFile);
        setPreviewUrl(imageUrl);
      } else {
        setPreviewUrl(null);
      }
    } else {
      setFile(null);
      setPreviewUrl(null);
      setError('Only PDF or image files (JPG/PNG) are supported.');
    }
  };
  
  

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    const isPdf = selectedFile?.type === 'application/pdf';
    const isImage = selectedFile?.type?.startsWith('image/');
  
    if (selectedFile && (isPdf || isImage)) {
      setFile(selectedFile);
      setError('');
      if (isImage) {
        const imageUrl = URL.createObjectURL(selectedFile);
        setPreviewUrl(imageUrl);
      } else {
        setPreviewUrl(null);
      }
    } else {
      setFile(null);
      setPreviewUrl(null);
      setError('Only PDF or image files (JPG/PNG) are supported.');
    }
  };
  
  

  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-500 via-indigo-500 to-blue-500 flex flex-col items-center justify-start p-8">
      <motion.h1 className="text-4xl font-bold mb-6 text-purple-700 animate-pulse"
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.8 }}
      >
        Report Analysis
      </motion.h1>

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
        {!file && (
          <>
            <p className="text-center text-gray-600 mb-4">
              Drag and drop your PDF or image file here
            </p>
            <label className="bg-gradient-to-r from-purple-500 via-indigo-500 to-blue-500 hover:from-pink-500 hover:via-purple-500 hover:to-indigo-500 text-white px-4 py-2 rounded-xl transition duration-300 shadow-md cursor-pointer">
              Browse File
              <input
                type="file"
                accept="application/pdf,image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          </>
        )}

        {file && (
          <div className="w-full h-full flex items-center justify-center">
            <button
              onClick={handleRemoveFile}
              className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 text-sm shadow"
              title="Remove File"
            >
              🗑️
            </button>
            {file.type === 'application/pdf' ? (
              <iframe
                src={URL.createObjectURL(file)}
                title="PDF Preview"
                className="w-full h-full rounded shadow"
              />
            ) : (
              <img
                src={previewUrl}
                alt="Image Preview"
                className="max-h-full max-w-full rounded shadow"
              />
            )}
          </div>
        )}
      </div>


      <button
        onClick={handleAnalyze}
        disabled={!file || loading}
        className="bg-gradient-to-r from-purple-500 via-indigo-500 to-blue-500 hover:from-pink-500 hover:via-purple-500 hover:to-indigo-500 text-white px-6 py-3 rounded-xl transition duration-300 shadow-md cursor-pointer"
      >
        {loading ? 'Analyzing...' : 'Analyze Report'}
      </button>

      {error && <p className="text-red-600 mt-4">{error}</p>}

      {(summary || recommendedDoctor) && (
        <motion.div
          className="bg-white p-6 rounded-2xl shadow-lg max-w-2xl w-full space-y-4 mt-8"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {summary && (
            <div>
              <h2 className="text-2xl font-semibold text-purple-700 mb-2">Summary</h2>
              <p className="text-gray-800 whitespace-pre-line">{summary}</p>
            </div>
          )}
          {recommendedDoctor && (
            <div>
              <h2 className="text-2xl font-semibold text-purple-700 mb-2">Suggested recommendedDoctor</h2>
              <p className="text-green-700 font-bold">{recommendedDoctor}</p>
            </div>
          )}
        </motion.div>
      )}

      <button
        onClick={() => navigate('/dashboard')}
        className="mt-8 font-bold bg-green-500 text-white px-8 py-3 rounded-lg hover:bg-green-600 transition"
      >
        Back to Dashboard
      </button>
    </div>
  );
}
