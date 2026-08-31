import { Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth.js";
import Spinner from "../components/Spinner.jsx";
function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated === null) {
    return <Spinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
