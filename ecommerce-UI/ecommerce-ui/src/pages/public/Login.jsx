import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../api/apiUtils";
import { loginUser } from "../../api/authApi";
import { buildUserFromAuthPayload, useAuth } from "../../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || "/";

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const payload = await loginUser(form);
      const token = payload?.token || payload?.accessToken || payload?.jwt;

      if (!token) {
        setError(
          "Login succeeded but we couldn't process your session. Please try again.",
        );
        return;
      }

      login({ token, user: buildUserFromAuthPayload(payload, form.email) });
      navigate(from, { replace: true });
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Login failed. Please check your email and password are correct.",
        ),
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page auth-page">
      <form className="form-card" onSubmit={handleSubmit}>
        <h1>Login</h1>
        {error && <p className="error-text">{error}</p>}
        <label>
          Email
          <input
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Password
          <input
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            required
          />
        </label>
        <button className="button" type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
        <p>
          New user? <Link to="/register">Register here</Link>
        </p>
      </form>
    </section>
  );
}

export default Login;
