import { Link, useNavigate } from "react-router-dom";
import { addToCart } from "../api/cartApi";
import { formatPrice, getCategoryName, getProductId } from "../api/apiUtils";
import { useAuth } from "../context/AuthContext";

function ProductCard({ product, onError }) {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const productId = getProductId(product);
  const category = product?.category?.name || product?.categoryName || product?.category;
  const stockQuantity = product?.stockQuantity ?? product?.stock ?? 0;

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: { pathname: `/products/${productId}` } } });
      return;
    }

    try {
      await addToCart({ productId: Number(productId), quantity: 1 });
      navigate("/cart");
    } catch (error) {
      onError?.(error);
    }
  };

  return (
    <article className="card product-card">
      <Link to={`/products/${productId}`} className="product-image-placeholder">
        {product?.imageUrl ? <img src={product.imageUrl} alt={product.name} /> : "Product Image"}
      </Link>
      {category && <span className="product-category">{getCategoryName({ name: category })}</span>}
      <h3>{product?.name || "Product Name"}</h3>
      <p className="product-description">{product?.description || "No product description available."}</p>
      <div className="card-footer">
        <div>
          <p className="price">{formatPrice(product?.price)}</p>
          <p className={stockQuantity > 0 ? "muted-text" : "error-text"}>
            {stockQuantity > 0 ? `${stockQuantity} in stock` : "Out of stock"}
          </p>
        </div>
        <div className="button-stack">
          <Link className="button secondary" to={`/products/${productId}`}>View</Link>
          <button className="button" type="button" onClick={handleAddToCart} disabled={!productId || stockQuantity === 0}>
            Add
          </button>
        </div>
      </div>
    </article>
  );
}

export default ProductCard;
