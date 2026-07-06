import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../api/apiUtils";
import { registerUser } from "../../api/authApi";
import { buildUserFromAuthPayload, useAuth } from "../../context/AuthContext";

function Register() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Password and confirm password do not match.");
      return;
    }

    setLoading(true);
    try {
      const payload = await registerUser({
        name: form.name,
        email: form.email,
        password: form.password,
      });
      const token = payload?.token || payload?.accessToken || payload?.jwt;

      if (token) {
        login({ token, user: buildUserFromAuthPayload(payload, form.email) });
        navigate("/products");
      } else {
        navigate("/login");
      }
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Registration failed. This email might already be in use. Please try another email.",
        ),
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page auth-page">
      <form className="form-card" onSubmit={handleSubmit}>
        <h1>Register</h1>
        {error && <p className="error-text">{error}</p>}
        <label>
          Name
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            required
          />
        </label>
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
        <label>
          Confirm Password
          <input
            name="confirmPassword"
            type="password"
            value={form.confirmPassword}
            onChange={handleChange}
            required
          />
        </label>
        <button className="button" type="submit" disabled={loading}>
          {loading ? "Registering..." : "Register"}
        </button>
        <p>
          Already registered? <Link to="/login">Login here</Link>
        </p>
      </form>
    </section>
  );
}

export default Register;
