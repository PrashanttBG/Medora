// DocumentsPage.jsx - With preview popup and upload success toast
import { useState, useEffect } from 'react';
import axios from 'axios';
import { Download, Trash2, FileText, Eye } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function DocumentsPage() {
  const [documents, setDocuments] = useState([]);
  const [file, setFile] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [reportDate, setReportDate] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [showToast, setShowToast] = useState(false);
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
      setShowToast(true);
      setTimeout(() => setShowToast(false), 3000);
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

  const handlePreview = async (docId) => {
    try {
      const token = localStorage.getItem('token');
      const res = await axios.get(`http://localhost:8080/documents/${docId}/download`, {
        responseType: 'blob',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const file = new Blob([res.data], { type: 'application/pdf' });
      const fileURL = URL.createObjectURL(file);
      setPreviewUrl(fileURL);
    } catch (err) {
      console.error('Preview failed:', err);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <button
        className="absolute top-6 right-6 bg-gradient-to-r from-blue-500 to-green-400 text-white font-semibold px-4 py-2 rounded-full shadow-md hover:scale-105 transition"
        onClick={() => navigate('/dashboard')}
      >
        Dashboard
      </button>

      {showToast && (
        <div className="fixed top-4 right-4 bg-green-100 text-green-800 px-4 py-2 rounded shadow-lg flex items-center gap-2 z-50">
          <span className="text-xl">✅</span>
          <span>Document uploaded successfully</span>
        </div>
      )}

      <h1 className="text-3xl font-extrabold bg-gradient-to-r from-fuchsia-600 via-rose-500 to-amber-400 bg-clip-text text-transparent text-center mb-6">
        Your Documents
      </h1>

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

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {documents.map((doc) => (
          <div key={doc.id} className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm hover:shadow-md transition-all duration-300">
            <div className="flex items-center gap-2 mb-3 text-blue-700">
              <FileText className="w-5 h-5" />
              <h3 className="font-semibold text-lg truncate">{doc.fileName}</h3>
            </div>

            <div className="text-sm text-gray-600 space-y-1 mb-3">
              <p><span className="font-medium">Uploaded:</span> {new Date(doc.uploadDate).toLocaleDateString()}</p>
              {doc.reportDate && (
                <p><span className="font-medium">Report Date:</span> {doc.reportDate}</p>
              )}
              {doc.tags && (
                <p className="italic text-xs text-gray-500">Tags: {doc.tags}</p>
              )}
            </div>

            <div className="flex gap-2 mt-4 flex-wrap">
              <button
                onClick={() => handlePreview(doc.id)}
                className="flex items-center gap-1 px-3 py-1 text-sm text-white bg-indigo-600 hover:bg-indigo-700 rounded-md"
              >
                <Eye size={16} /> Preview
              </button>
              <a
                href={`http://localhost:8080/documents/${doc.id}/download`}
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1 px-3 py-1 text-sm text-white bg-blue-600 hover:bg-blue-700 rounded-md"
              >
                <Download size={16} /> Download
              </a>
              <button
                onClick={() => handleDelete(doc.id)}
                className="flex items-center gap-1 px-3 py-1 text-sm text-white bg-red-600 hover:bg-red-700 rounded-md"
              >
                <Trash2 size={16} /> Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {previewUrl && (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-4 w-[90%] max-w-4xl">
            <div className="flex justify-between items-center mb-2">
              <h2 className="text-lg font-semibold">PDF Preview</h2>
              <button onClick={() => setPreviewUrl(null)} className="text-red-500 font-bold text-xl">×</button>
            </div>
            <iframe
              src={previewUrl}
              title="PDF Preview"
              className="w-full h-[75vh] border"
            ></iframe>
          </div>
        </div>
      )}
    </div>
  );
}