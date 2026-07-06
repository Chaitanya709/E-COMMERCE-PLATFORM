import { Link } from "react-router-dom";

function AdminDashboard() {
  return (
    <section className="page">
      <h1>Admin Dashboard</h1>
      <div className="grid admin-grid">
        <Link className="card" to="/admin/products">Manage Products</Link>
        <Link className="card" to="/admin/categories">Manage Categories</Link>
        <Link className="card" to="/admin/orders">Manage Orders</Link>
        <Link className="card" to="/admin/users">Manage Users</Link>
      </div>
    </section>
  );
}

export default AdminDashboard;
