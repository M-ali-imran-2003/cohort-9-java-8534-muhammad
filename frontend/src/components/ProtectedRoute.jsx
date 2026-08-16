import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {
  // eslint-disable-next-line no-useless-assignment
  let token = null;
  try {
    token = localStorage.getItem("token");
  } catch {
    return <Navigate to="/login" replace />;
  }

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
