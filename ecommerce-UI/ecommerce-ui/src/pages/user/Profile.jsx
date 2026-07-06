import { useAuth } from "../../context/AuthContext";

function Profile() {
  const { user } = useAuth();

  return (
    <section className="page">
      <h1>Profile</h1>
      <div className="card">
        <p>Email: {user?.email || "Not available"}</p>
        <p>Role: {user?.role || "USER"}</p>
      </div>
    </section>
  );
}

export default Profile;
