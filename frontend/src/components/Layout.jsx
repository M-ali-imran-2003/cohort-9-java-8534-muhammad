import Sidebar from "./Sidebar.jsx";
import "../styles/layout.css";

function Layout({ children }) {
  return (
    <div className="app-layout">
      <Sidebar />
      <main className="app-content">{children}</main>
    </div>
  );
}

export default Layout;
