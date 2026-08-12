import { NavLink } from "react-router-dom";
import "../styles/layout.css";

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">CMS</div>
      <nav className="sidebar-nav">
        <NavLink
          to="/profile"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Profile
        </NavLink>
        <NavLink
          to="/contacts"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Contacts
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;
