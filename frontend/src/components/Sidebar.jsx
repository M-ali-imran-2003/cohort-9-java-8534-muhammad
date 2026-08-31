import { NavLink } from "react-router-dom";
import { User, Users } from "lucide-react";
import "../styles/layout.css";

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">CMS</div>
      <nav className="sidebar-nav">
        <NavLink
          to="/contacts"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          <Users size={18} /> Contacts
        </NavLink>
        <NavLink
          to="/profile"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          <User size={18} /> Profile
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;
