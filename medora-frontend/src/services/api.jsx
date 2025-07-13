// src/services/api.js
import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

export const loginUser = async (username, password) => {
  const response = await axios.post(`${BASE_URL}/auth/login`, { 
    username, 
    password
    });
  return response.data;
};

export const registerUser = async (username, email, password) => {
  const response = await axios.post(`${BASE_URL}/auth/register`, {
    username,
    email,
    password
  });
  return response.data;
};

export const getMedicineDetails = async (medicineName, language) => {
  const token = localStorage.getItem('token');
  const response = await axios.post(
    `${BASE_URL}/medicine/decode`,
    { medicineName, language },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const analyzeReport = async (file) => {
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username'); // Make sure this is stored after login
  console.log('File: ', file);
  const formData = new FormData();
  formData.append('file', file);
  formData.append('username', username);

  const response = await axios.post(`${BASE_URL}/report/analyze`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data; // expected { summary, specialist } is returned
};

export const analyzeImageReport = async (file) => {
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username');
  const formData = new FormData();
  formData.append('file', file);
  formData.append('username', username);

  const response = await axios.post(`${BASE_URL}/report/image-analyze`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};


export const analyzePrescription = async (file) => {
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username'); // Make sure this is stored after login
  console.log('File: ', file);
  const formData = new FormData();
  formData.append('file', file);
  formData.append('username', username);

  const response = await axios.post(`${BASE_URL}/prescription/analyze`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};


export const analyzeImagePrescription = async (file) => {
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username');
  const formData = new FormData();
  formData.append('file', file);
  formData.append('username', username);

  const response = await axios.post(`${BASE_URL}/prescription/image-analyze`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};
