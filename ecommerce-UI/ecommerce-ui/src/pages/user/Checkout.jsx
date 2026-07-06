import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createOrder } from "../../api/orderApi";
import { getCart } from "../../api/cartApi";
import { useAuth } from "../../context/AuthContext";
import {
  formatPrice,
  getApiErrorMessage,
  getCartItemId,
  getCartItems,
  getItemPrice,
  getItemQuantity,
  getProductFromCartItem,
} from "../../api/apiUtils";

function Checkout() {
  const [cartItems, setCartItems] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const { user, token } = useAuth();

  useEffect(() => {
    async function loadCart() {
      try {
        const response = await getCart();
        const items = getCartItems(response);
        setCartItems(items);
        setSelectedIds(items.map(getCartItemId).filter(Boolean));
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load your cart. Please try refreshing the page.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }

    loadCart();
  }, [user]);

  const selectedItems = useMemo(
    () => cartItems.filter((item) => selectedIds.includes(getCartItemId(item))),
    [cartItems, selectedIds],
  );
  const orderTotal = useMemo(
    () =>
      selectedItems.reduce(
        (sum, item) => sum + getItemPrice(item) * getItemQuantity(item),
        0,
      ),
    [selectedItems],
  );

  const toggleItem = (cartItemId) => {
    setSelectedIds((current) =>
      current.includes(cartItemId)
        ? current.filter((id) => id !== cartItemId)
        : [...current, cartItemId],
    );
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (selectedIds.length === 0) {
      setError("Select at least one cart item before placing order.");
      return;
    }

    setIsSubmitting(true);
    try {
      const orderPayload = {
        cartItemIds: selectedItems.map((item) => item.id),
      };

      const order = await createOrder(orderPayload);
      navigate(`/orders/${order?.id ?? order?.orderId ?? ""}` || "/orders");
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not place your order. Please check if all items are still in stock and try again.",
        ),
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading)
    return (
      <section className="page">
        <p>Loading checkout information…</p>
      </section>
    );
  return (
    <section className="page">
      <div className="page-header">
        <h1>Checkout</h1>
      </div>
      {error && <p className="error-text">{error}</p>}
      <div className="checkout-grid">
        <form className="form-card wide-form" onSubmit={handleSubmit}>
          <h2>Select cart items</h2>
          {cartItems.length === 0 ? (
            <p>Your cart is empty.</p>
          ) : (
            <div className="order-items-list">
              {cartItems.map((item) => {
                const product = getProductFromCartItem(item);
                const cartItemId = getCartItemId(item);
                const quantity = getItemQuantity(item);
                const price = getItemPrice(item);
                return (
                  <label className="selectable-row" key={cartItemId}>
                    <input
                      type="checkbox"
                      checked={selectedIds.includes(cartItemId)}
                      onChange={() => toggleItem(cartItemId)}
                    />
                    <span>
                      {product?.name ?? "Item"} × {quantity}
                    </span>
                    <strong>{formatPrice(price * quantity)}</strong>
                  </label>
                );
              })}
            </div>
          )}
          <button
            className="button"
            type="submit"
            disabled={isSubmitting || selectedIds.length === 0}
          >
            {isSubmitting ? "Placing Order..." : "Place Order"}
          </button>
        </form>

        <aside className="summary-box card">
          <h2>Order Summary</h2>
          <p>{selectedItems.length} selected item(s)</p>
          <div className="summary-row">
            <strong>Total</strong>
            <strong>{formatPrice(orderTotal)}</strong>
          </div>
        </aside>
      </div>
    </section>
  );
}

export default Checkout;
