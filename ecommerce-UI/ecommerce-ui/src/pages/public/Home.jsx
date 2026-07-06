import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import ProductCard from "../../components/ProductCard";
import { getProducts } from "../../api/productApi";
import { getCategories } from "../../api/categoryApi";
import {
  getApiErrorMessage,
  getCategoryId,
  normalizeList,
} from "../../api/apiUtils";

function Home() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    async function loadHomeData() {
      try {
        const [productResponse, categoryResponse] = await Promise.all([
          getProducts(),
          getCategories(),
        ]);
        setProducts(normalizeList(productResponse));
        setCategories(normalizeList(categoryResponse));
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Unable to load home data. Check backend connectivity.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }

    loadHomeData();
  }, []);

  const featuredProducts = products.slice(0, 8);

  return (
    <section className="page">
      <div className="hero">
        <div>
          <p>
            Browse seeded products, add them to cart, checkout, place orders.
          </p>
          <div className="hero-actions">
            <button
              className="button"
              type="button"
              onClick={() => navigate("/products")}
            >
              Browse Products
            </button>
          </div>
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}

      <section className="page-section">
        <div className="page-header">
          <h2>Categories</h2>
          <Link className="button secondary" to="/products">
            View all products
          </Link>
        </div>
        {loading ? (
          <p>Loading categories...</p>
        ) : (
          <div className="category-filters">
            {categories.map((category) => (
              <Link
                key={getCategoryId(category)}
                className="category-pill"
                to={`/categories/${getCategoryId(category)}/products`}
              >
                {category.name}
              </Link>
            ))}
          </div>
        )}
      </section>

      <section className="page-section">
        <div className="page-header">
          <h2>Featured products</h2>
          <p>{featuredProducts.length} products</p>
        </div>
        {loading ? (
          <p>Loading products...</p>
        ) : (
          <div className="grid">
            {featuredProducts.map((product) => (
              <ProductCard
                key={product.id ?? product.productId}
                product={product}
                onError={(err) =>
                  setError(getApiErrorMessage(err, "Unable to add to cart."))
                }
              />
            ))}
          </div>
        )}
      </section>
    </section>
  );
}

export default Home;
