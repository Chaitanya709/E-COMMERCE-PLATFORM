import { Link } from "react-router-dom";

function NotFound() {
  return (
    <section className="page center-page">
      <h1>404</h1>
      <p>Page not found.</p>
      <Link className="button" to="/">Go Home</Link>
    </section>
  );
}

export default NotFound;
