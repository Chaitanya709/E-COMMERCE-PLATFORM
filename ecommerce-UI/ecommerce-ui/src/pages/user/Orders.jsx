import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyOrders } from "../../api/orderApi";
import {
  formatPrice,
  getApiErrorMessage,
  normalizeList,
} from "../../api/apiUtils";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadOrders() {
      try {
        const response = await getMyOrders();
        setOrders(normalizeList(response));
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load your orders. Please try again in a few moments.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }

    loadOrders();
  }, []);

  if (loading)
    return (
      <section className="page">
        <p>Loading orders...</p>
      </section>
    );

  return (
    <section className="page">
      <div className="page-header">
        <h1>My Orders</h1>
      </div>
      {error && <p className="error-text">{error}</p>}
      {orders.length === 0 ? (
        <div className="card center-page">
          <p>You have not placed any orders yet.</p>
          <Link className="button" to="/products">
            Shop Products
          </Link>
        </div>
      ) : (
        <div className="grid">
          {orders.map((order) => {
            const id = order.id ?? order.orderId ?? order.orderNumber;
            return (
              <article className="card order-card" key={id}>
                <div className="order-card-header">
                  <h2>Order #{id}</h2>
                  <span className="badge">
                    {order.status ?? order.orderStatus ?? "PENDING"}
                  </span>
                </div>
                <p>
                  {order.createdAt
                    ? new Date(order.createdAt).toLocaleString()
                    : "Date not available"}
                </p>
                <p>
                  Total:{" "}
                  {formatPrice(
                    order.totalAmount ?? order.orderTotal ?? order.total,
                  )}
                </p>
                <Link className="button secondary" to={`/orders/${id}`}>
                  View Details
                </Link>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default Orders;
