import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteProduct, getProducts } from "../../api/productApi";
import {
  formatPrice,
  getApiErrorMessage,
  getProductId,
  normalizeList,
} from "../../api/apiUtils";

function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadProducts = async () => {
    try {
      const response = await getProducts();
      setProducts(normalizeList(response));
    } catch (err) {
      setError(
        getApiErrorMessage(err, "Failed to load products. Please try again."),
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleDelete = async (product) => {
    const id = getProductId(product);
    if (!window.confirm(`Delete ${product.name}?`)) return;

    try {
      await deleteProduct(id);
      setProducts((current) =>
        current.filter((item) => getProductId(item) !== id),
      );
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not delete this product. Please try again.",
        ),
      );
    }
  };

  if (loading)
    return (
      <section className="page">
        <p>Loading products...</p>
      </section>
    );

  return (
    <section className="page">
      <div className="page-header">
        <h1>Manage Products</h1>
        <Link className="button" to="/admin/products/add">
          Add Product
        </Link>
      </div>
      {error && <p className="error-text">{error}</p>}
      <div className="table-card card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Category</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => {
              const id = getProductId(product);
              return (
                <tr key={id}>
                  <td>{id}</td>
                  <td>{product.name}</td>
                  <td>
                    {product.category?.name ??
                      product.categoryName ??
                      product.categoryId ??
                      "-"}
                  </td>
                  <td>{formatPrice(product.price)}</td>
                  <td>{product.stockQuantity ?? 0}</td>
                  <td className="table-actions">
                    <Link
                      className="button secondary"
                      to={`/admin/products/edit/${id}`}
                    >
                      Edit
                    </Link>
                    <button
                      className="button danger"
                      type="button"
                      onClick={() => handleDelete(product)}
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
    </section>
  );
}

export default AdminProducts;
