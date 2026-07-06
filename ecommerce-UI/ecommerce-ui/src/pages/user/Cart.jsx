import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  clearCart,
  getCart,
  removeCartItem,
  updateCartItem,
} from "../../api/cartApi";
import {
  formatPrice,
  getApiErrorMessage,
  getCartItemId,
  getCartItems,
  getItemPrice,
  getItemQuantity,
  getProductFromCartItem,
} from "../../api/apiUtils";

function Cart() {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const loadCart = async () => {
    try {
      const response = await getCart();
      setCartItems(getCartItems(response));
    } catch (err) {
      setError(getApiErrorMessage(err, "Unable to load cart."));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleQuantityChange = async (item, newQuantity) => {
    const quantity = Math.max(1, newQuantity);
    const cartItemId = getCartItemId(item);

    if (!cartItemId) {
      setError("Unable to identify cart item.");
      return;
    }

    try {
      const response = await updateCartItem(cartItemId, { quantity });
      setCartItems(getCartItems(response));
    } catch (err) {
      setError(getApiErrorMessage(err, "Unable to update quantity."));
    }
  };

  const handleRemoveItem = async (item) => {
    const cartItemId = getCartItemId(item);
    if (!cartItemId) {
      setError("Unable to identify cart item for removal.");
      return;
    }

    try {
      const response = await removeCartItem(cartItemId);
      setCartItems(getCartItems(response));
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Failed to remove item from cart. Please try again.",
        ),
      );
    }
  };

  const handleClearCart = async () => {
    try {
      const response = await clearCart();
      setCartItems(getCartItems(response));
    } catch (err) {
      setError(
        getApiErrorMessage(err, "Failed to clear your cart. Please try again."),
      );
    }
  };

  const totalAmount = useMemo(
    () =>
      cartItems.reduce(
        (sum, item) => sum + getItemPrice(item) * getItemQuantity(item),
        0,
      ),
    [cartItems],
  );

  if (loading)
    return (
      <section className="page">
        <p>Loading cart...</p>
      </section>
    );

  return (
    <section className="page">
      <div className="page-header">
        <h1>Shopping Cart</h1>
        <Link className="button secondary" to="/products">
          Continue Shopping
        </Link>
      </div>

      {error && <p className="error-text">{error}</p>}

      {cartItems.length === 0 ? (
        <div className="card center-page">
          <p>Your cart is empty.</p>
          <Link className="button" to="/products">
            Browse Products
          </Link>
        </div>
      ) : (
        <div className="cart-grid">
          <div className="cart-items">
            {cartItems.map((item) => {
              const product = getProductFromCartItem(item);
              const quantity = getItemQuantity(item);
              const price = getItemPrice(item);
              return (
                <div className="card cart-item" key={getCartItemId(item)}>
                  <div className="cart-product-row">
                    {product?.imageUrl && (
                      <img
                        className="cart-thumb"
                        src={product.imageUrl}
                        alt={product.name}
                      />
                    )}
                    <div>
                      <h2>{product?.name ?? "Product"}</h2>
                      <p>
                        {product?.description ?? "No description available."}
                      </p>
                      <p className="price">{formatPrice(price)}</p>
                      <p>Subtotal: {formatPrice(price * quantity)}</p>
                    </div>
                  </div>
                  <div className="cart-item-actions">
                    <div className="quantity-controls">
                      <button
                        className="button secondary"
                        type="button"
                        onClick={() => handleQuantityChange(item, quantity - 1)}
                      >
                        −
                      </button>
                      <span>{quantity}</span>
                      <button
                        className="button secondary"
                        type="button"
                        onClick={() => handleQuantityChange(item, quantity + 1)}
                      >
                        +
                      </button>
                    </div>
                    <button
                      className="button"
                      type="button"
                      onClick={() => handleRemoveItem(item)}
                    >
                      Remove
                    </button>
                  </div>
                </div>
              );
            })}
          </div>

          <aside className="summary-box card">
            <h2>Order Summary</h2>
            <div className="summary-row">
              <span>Items</span>
              <span>{cartItems.length}</span>
            </div>
            <div className="summary-row">
              <span>Total</span>
              <strong>{formatPrice(totalAmount)}</strong>
            </div>
            <button className="button" onClick={() => navigate("/checkout")}>
              Proceed to Checkout
            </button>
            <button className="button secondary" onClick={handleClearCart}>
              Clear Cart
            </button>
          </aside>
        </div>
      )}
    </section>
  );
}

export default Cart;
