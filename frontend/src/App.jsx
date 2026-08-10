import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Navigate to="/login" replace />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
}

export default App;
