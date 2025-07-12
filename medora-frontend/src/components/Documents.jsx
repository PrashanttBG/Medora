// DocumentsPage.jsx - With toggleable upload form
import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function DocumentsPage() {
  const [documents, setDocuments] = useState([]);
  const [file, setFile] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [reportDate, setReportDate] = useState('');
  const [showForm, setShowForm] = useState(false);
  const navigate = useNavigate();

  const fetchDocuments = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await axios.get('http://localhost:8080/documents/all', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setDocuments(res.data);
    } catch (err) {
      console.error('Error fetching documents:', err);
    }
  };

  const handleUpload = async () => {
    if (!file) return alert('Please select a file');

    const formData = new FormData();
    formData.append('file', file);
    if (reportDate) formData.append('reportDate', reportDate);
    if (description) formData.append('tags', description);

    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8080/documents/upload', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });
      setShowForm(false);
      setFile(null);
      setTitle('');
      setDescription('');
      setReportDate('');
      fetchDocuments();
    } catch (err) {
      console.error('Upload failed:', err);
    }
  };

  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(`http://localhost:8080/documents/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchDocuments();
    } catch (err) {
      console.error('Delete failed:', err);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <button
          className=" absolute top-6 right-6 bg-gradient-to-r from-blue-500 to-green-400 text-white-700 font-bold px-4 py-2 rounded-full shadow-md hover:bg-gray-100 transition"
          onClick={() => navigate("/dashboard")}
        >
          Dashboard
        </button>
      <h1 className="text-3xl font-bold mb-6 mt-8 text-center">Your Documents</h1>

      <div className="mb-6 text-center">
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded shadow"
        >
          {showForm ? 'Cancel Upload' : 'Upload New Document'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white p-6 rounded-xl shadow-md mb-8 max-w-2xl mx-auto">
          <h2 className="text-xl font-semibold mb-4">Upload Document</h2>
          <div className="space-y-3">
            <input
              type="file"
              onChange={(e) => setFile(e.target.files[0])}
              className="block w-full text-sm border rounded px-2 py-1"
            />
            <input
              type="text"
              placeholder="Title (optional)"
              className="w-full border rounded px-2 py-1"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
            <textarea
              placeholder="Tags / Description"
              className="w-full border rounded px-2 py-1"
              rows="2"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            <input
              type="date"
              className="w-full border rounded px-2 py-1"
              value={reportDate}
              onChange={(e) => setReportDate(e.target.value)}
            />
            <button
              onClick={handleUpload}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
            >
              Upload
            </button>
          </div>
        </div>
      )}

      {/* Documents Display */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {documents.map((doc) => (
          <div key={doc.id} className="bg-white p-4 rounded shadow">
            <h3 className="font-bold text-lg mb-1 truncate">{doc.fileName}</h3>
            <p className="text-sm text-gray-600">Uploaded: {new Date(doc.uploadDate).toLocaleDateString()}</p>
            {doc.reportDate && (
              <p className="text-sm text-gray-600">Report Date: {doc.reportDate}</p>
            )}
            {doc.tags && (
              <p className="text-xs text-gray-500 italic mt-1">Tags: {doc.tags}</p>
            )}
            <div className="flex gap-2 mt-4">
              <a
                href={`http://localhost:8080/documents/${doc.id}/download`}
                target="_blank"
                rel="noreferrer"
                className="text-blue-600 underline text-sm"
              >
                Download
              </a>
              <button
                onClick={() => handleDelete(doc.id)}
                className="text-red-600 underline text-sm"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
