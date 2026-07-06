import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import ProductCard from "../../components/ProductCard";
import { getProductsByCategory } from "../../api/productApi";
import { getCategoryById } from "../../api/categoryApi";
import { getApiErrorMessage, normalizeList } from "../../api/apiUtils";

function ProductsByCategory() {
  const { categoryId } = useParams();
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const [productsResponse, categoryResponse] = await Promise.all([
          getProductsByCategory(categoryId),
          getCategoryById(categoryId).catch(() => null),
        ]);
        setProducts(normalizeList(productsResponse));
        setCategory(categoryResponse);
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load products in this category. Please try again.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [categoryId]);

  if (loading)
    return (
      <section className="page">
        <p>Loading category products...</p>
      </section>
    );

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>{category?.name || `Category ${categoryId}`}</h1>
          <p>{products.length} product(s)</p>
        </div>
        <Link className="button secondary" to="/products">
          All Products
        </Link>
      </div>
      {error && <p className="error-text">{error}</p>}
      <div className="grid">
        {products.map((product) => (
          <ProductCard
            key={product.id ?? product.productId}
            product={product}
            onError={(err) =>
              setError(
                getApiErrorMessage(
                  err,
                  "Could not add product to cart. Please try again.",
                ),
              )
            }
          />
        ))}
      </div>
    </section>
  );
}

export default ProductsByCategory;
