import { NavLink } from "react-router-dom";

function Sidebar() {
  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <NavLink to="/" end>
          Dashboard
        </NavLink>

        <NavLink to="/fraud-cases">
          Fraud Cases
        </NavLink>
        <NavLink to="/statistics">
          Statistics
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;