import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getProductById } from "../../api/productApi";
import { addToCart } from "../../api/cartApi";
import {
  formatPrice,
  getApiErrorMessage,
  getCategoryName,
  getProductId,
} from "../../api/apiUtils";
import { useAuth } from "../../context/AuthContext";

function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    async function fetchProduct() {
      try {
        const response = await getProductById(id);
        setProduct(response);
      } catch (err) {
        setError(
          getApiErrorMessage(err, "Failed to load product. Please try again."),
        );
      } finally {
        setLoading(false);
      }
    }

    fetchProduct();
  }, [id]);

  const productId = getProductId(product) ?? id;
  const stockQuantity = Number(product?.stockQuantity ?? product?.stock ?? 0);
  const category =
    product?.category?.name || product?.categoryName || product?.category;

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: { pathname: `/products/${id}` } } });
      return;
    }

    setSubmitting(true);
    setError("");
    try {
      await addToCart({
        productId: Number(productId),
        quantity: Number(quantity),
      });
      navigate("/cart");
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not add product to cart. Please try again.",
        ),
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading)
    return (
      <section className="page">
        <p>Loading product...</p>
      </section>
    );
  if (error && !product)
    return (
      <section className="page">
        <p className="error-text">{error}</p>
      </section>
    );

  return (
    <section className="page product-details">
      <div className="details-image">
        {product?.imageUrl ? (
          <img src={product.imageUrl} alt={product.name} />
        ) : (
          "Product Image"
        )}
      </div>
      <div className="card">
        <h1>{product?.name}</h1>
        {category && (
          <p className="product-category">
            Category: {getCategoryName({ name: category })}
          </p>
        )}
        <p>
          {product?.description || "No description available for this product."}
        </p>
        <p className="price">{formatPrice(product?.price)}</p>
        <p className={stockQuantity > 0 ? "success-text" : "error-text"}>
          {stockQuantity > 0
            ? `${stockQuantity} items in stock`
            : "Out of stock"}
        </p>
        <div className="product-actions">
          <div className="quantity-controls">
            <button
              className="button secondary"
              type="button"
              onClick={() => setQuantity((value) => Math.max(1, value - 1))}
              disabled={quantity <= 1}
            >
              −
            </button>
            <span>{quantity}</span>
            <button
              className="button secondary"
              type="button"
              onClick={() =>
                setQuantity((value) =>
                  Math.min(stockQuantity || value + 1, value + 1),
                )
              }
              disabled={stockQuantity > 0 && quantity >= stockQuantity}
            >
              +
            </button>
          </div>
          <button
            className="button"
            type="button"
            onClick={handleAddToCart}
            disabled={stockQuantity === 0 || submitting}
          >
            {submitting
              ? "Adding..."
              : stockQuantity === 0
                ? "Out of stock"
                : "Add to Cart"}
          </button>
        </div>
        {error && <p className="error-text">{error}</p>}
      </div>
    </section>
  );
}

export default ProductDetails;
