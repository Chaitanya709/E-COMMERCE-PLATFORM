import { Link, NavLink, useNavigate } from "react-router-dom";
import { FaShoppingCart, FaUserCircle } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        Ecommerce
      </Link>

      <nav className="nav-links">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/products">Products</NavLink>

        {isAuthenticated && (
          <>
            <NavLink to="/cart" className="nav-icon-link">
              <FaShoppingCart /> Cart
            </NavLink>
            <NavLink to="/orders">Orders</NavLink>
            <NavLink to="/profile" className="nav-icon-link">
              <FaUserCircle /> Profile
            </NavLink>
          </>
        )}

        {isAdmin && <NavLink to="/admin">Admin</NavLink>}

        {!isAuthenticated ? (
          <>
            <NavLink to="/login">Login</NavLink>
            <NavLink to="/register">Register</NavLink>
          </>
        ) : (
          <button className="link-button" onClick={handleLogout}>
            Logout
          </button>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
