import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { analyzePrescription,analyzeImagePrescription } from '../services/api';
import { useState } from 'react';

export default function PrescriptionAnalysis() {
  const [file, setFile] = useState(null);
  const [dragOver, setDragOver] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [previewUrl, setPreviewUrl] = useState(null);
  const [result, setResult] = useState('');

  
    const handleAnalyze = async () => {
      if (!file) return;
    
      setLoading(true);
      setError('');
    
      try {
        let data;
        const fileType = file.type;
    
        if (fileType === 'application/pdf') {
          data = await analyzePrescription(file);
        } else if (fileType.startsWith('image/')) {
          data = await analyzeImagePrescription(file);
        } else {
          throw new Error('Unsupported file type');
        }
        // console.log('Response from backend: ', data);
        const responseText = data?.data || data;
        setResult(responseText);
      } catch (err) {
        console.error('Error analyzing prescription:', err);
        setError('Failed to analyze prescription. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    
    const handleRemoveFile = () => {
      setFile(null);
      setPreviewUrl(null);
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
    <div className="min-h-screen bg-gradient-to-r from-indigo-400 via-purple-500 to-pink-500 flex flex-col items-center justify-center p-8 text-white">
      <motion.h1
        className="text-4xl bg-gradient-to-r from-blue-500 to-cyan-400 bg-clip-text text-transparent
 font-bold mb-8 animate-pulse"
        initial={{ y: 1, opacity: 1 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.8 }}
      >
        Prescription Analysis
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
            <label className="bg-gradient-to-r from-blue-500 to-cyan-400 hover:from-purple-600 hover:to-pink-500 text-white px-4 py-2 rounded-lg cursor-pointer transition duration-300">
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
        className="bg-gradient-to-r from-blue-500 to-cyan-400 hover:from-purple-600 hover:to-pink-500 text-white px-4 py-2 rounded-xl cursor-pointer transition duration-300"
      >
        {loading ? 'Analyzing...' : 'Analyze Prescription'}
      </button>

      {error && <p className="text-red-600 mt-4">{error}</p>}

      {result && (
        <div className="bg-white text-gray-800 rounded-xl p-6 mt-6 w-full max-w-4xl shadow-lg">
          <h2 className="text-xl font-bold mb-4 text-indigo-600">Analysis Result</h2>
          <pre className="whitespace-pre-wrap text-sm">{result}</pre>
        </div>
      )}


      <button
        onClick={() => navigate('/dashboard')}
        className="bg-green-500 px-6 mt-6 font-bold py-3 rounded-lg hover:bg-green-600 transition"
      >
        Back to Dashboard
      </button>
    </div>
  );
}
