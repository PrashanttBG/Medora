// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import MedicineDetails from './components/MedicineDetails';
import ReportAnalysis from './components/ReportAnalysis';
import Register from './components/Register';
import PrescriptionAnalysis from './components/PrescriptionAnalysis';
import Documents from './components/Documents';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/medicine-details" element={<MedicineDetails />} />
        <Route path="/report-analysis" element ={<ReportAnalysis />} />
        <Route path="/register" element = {<Register />} />
        <Route path="/prescription-analysis" element = {<PrescriptionAnalysis />} />
        <Route path="/documents" element ={<Documents />} />
      </Routes>
    </Router>
  );
}

export default App;
