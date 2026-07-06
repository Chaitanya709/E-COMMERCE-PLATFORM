import { useEffect, useMemo, useState } from "react";
import ProductCard from "../../components/ProductCard";
import { getProducts, searchProducts } from "../../api/productApi";
import { getCategories } from "../../api/categoryApi";
import {
  getApiErrorMessage,
  getCategoryId,
  getCategoryName,
  normalizeList,
} from "../../api/apiUtils";

function Products() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [selectedCategoryId, setSelectedCategoryId] = useState("");

  useEffect(() => {
    async function fetchData() {
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
            "Failed to load products. Please refresh the page and try again.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  useEffect(() => {
    const timer = setTimeout(async () => {
      if (!search.trim()) return;
      try {
        const response = await searchProducts(search.trim());
        setProducts(normalizeList(response));
      } catch {
        // Keep local filter fallback if search endpoint name differs.
      }
    }, 400);

    return () => clearTimeout(timer);
  }, [search]);

  const filteredProducts = useMemo(() => {
    const term = search.trim().toLowerCase();
    return products.filter((product) => {
      const productCategoryId = product?.category?.id ?? product?.categoryId;
      const matchesCategory = selectedCategoryId
        ? String(productCategoryId) === String(selectedCategoryId)
        : true;
      const matchesSearch = term
        ? product?.name?.toLowerCase().includes(term) ||
          product?.description?.toLowerCase().includes(term)
        : true;
      return matchesCategory && matchesSearch;
    });
  }, [products, search, selectedCategoryId]);

  if (loading)
    return (
      <section className="page">
        <p>Loading products...</p>
      </section>
    );

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Products</h1>
          <p>{filteredProducts.length} product(s) found.</p>
        </div>
      </div>

      <div className="search-panel card">
        <input
          className="search-input"
          type="search"
          placeholder="Search products"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
        <div className="category-filters">
          <button
            type="button"
            className={`category-pill ${selectedCategoryId === "" ? "active" : ""}`}
            onClick={() => setSelectedCategoryId("")}
          >
            All
          </button>
          {categories.map((category) => (
            <button
              key={getCategoryId(category)}
              type="button"
              className={`category-pill ${String(selectedCategoryId) === String(getCategoryId(category)) ? "active" : ""}`}
              onClick={() => setSelectedCategoryId(getCategoryId(category))}
            >
              {getCategoryName(category)}
            </button>
          ))}
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}
      {!error && filteredProducts.length === 0 && (
        <p>No products match your search.</p>
      )}
      <div className="grid">
        {filteredProducts.map((product) => (
          <ProductCard
            key={product.id ?? product.productId}
            product={product}
            onError={(err) =>
              setError(getApiErrorMessage(err, "Unable to add to cart."))
            }
          />
        ))}
      </div>
    </section>
  );
}

export default Products;
