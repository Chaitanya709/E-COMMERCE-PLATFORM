import { useEffect, useState } from "react";
import { deleteUser, getUsers } from "../../api/userApi";
import { getApiErrorMessage, normalizeList } from "../../api/apiUtils";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadUsers() {
      try {
        const response = await getUsers();
        setUsers(normalizeList(response));
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load users. Make sure you have admin permissions.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }
    loadUsers();
  }, []);

  const handleDelete = async (user) => {
    const id = user.id ?? user.userId;
    if (!window.confirm(`Delete ${user.email}?`)) return;
    try {
      await deleteUser(id);
      setUsers((current) =>
        current.filter((item) => (item.id ?? item.userId) !== id),
      );
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not delete this user. Please try again.",
        ),
      );
    }
  };

  return (
    <section className="page">
      <h1>Manage Users</h1>
      {error && <p className="error-text">{error}</p>}
      {loading ? (
        <p>Loading users...</p>
      ) : (
        <div className="table-card card">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => {
                const id = user.id ?? user.userId;
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>
                      <button
                        className="button danger"
                        type="button"
                        onClick={() => handleDelete(user)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default AdminUsers;
