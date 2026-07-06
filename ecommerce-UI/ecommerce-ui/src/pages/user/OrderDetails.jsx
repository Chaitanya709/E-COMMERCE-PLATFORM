import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getOrderById } from "../../api/orderApi";
import {
  formatPrice,
  getApiErrorMessage,
  normalizeList,
} from "../../api/apiUtils";

function getOrderItems(order) {
  return normalizeList(
    order?.orderItem ?? order?.orderItems ?? order?.items ?? [],
  );
}

function OrderDetails() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadOrder() {
      try {
        const response = await getOrderById(id);
        setOrder(response);
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load order details. Please try again.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }

    loadOrder();
  }, [id]);

  if (loading)
    return (
      <section className="page">
        <p>Loading order details...</p>
      </section>
    );
  if (error || !order)
    return (
      <section className="page">
        <p className="error-text">{error || "Order not found."}</p>
      </section>
    );

  const items = getOrderItems(order);
  const orderId = order.id ?? order.orderId ?? id;
  const status = order.status ?? order.orderStatus ?? "PENDING";
  const paymentStatus = order.payment?.paymentStatus ?? order.paymentStatus;

  return (
    <section className="page">
      <div className="page-header">
        <h1>Order #{orderId}</h1>
        <span className="badge">{status}</span>
      </div>

      <div className="order-details-grid">
        <div className="card">
          <h2>Order Info</h2>
          <p>Order ID: {orderId}</p>
          <p>
            Date:{" "}
            {order.createdAt
              ? new Date(order.createdAt).toLocaleString()
              : "Not available"}
          </p>
          <p>
            Total:{" "}
            {formatPrice(order.totalAmount ?? order.orderTotal ?? order.total)}
          </p>
          <p>Payment: {paymentStatus ?? "Not paid / unavailable"}</p>
        </div>
        <div className="card">
          <h2>Next action</h2>
          <p>After order creation, pay the order to mark it confirmed.</p>
          <Link className="button" to={`/orders/${orderId}/payment`}>
            Pay Order
          </Link>
        </div>
      </div>

      <div className="card">
        <h2>Items</h2>
        {items.length === 0 ? (
          <p>No items in this order.</p>
        ) : (
          <div className="order-items-list">
            {items.map((item) => {
              const product = item.product || item.productResponse || item;
              const quantity = item.quantity ?? 1;
              const price = Number(
                item.priceAtTime ?? item.price ?? product?.price ?? 0,
              );
              return (
                <div
                  className="order-item"
                  key={item.id ?? item.orderItemId ?? product?.id}
                >
                  <div>
                    <strong>
                      {product?.name ?? item.productName ?? "Product"}
                    </strong>
                    <p>{product?.description ?? "No description available."}</p>
                  </div>
                  <div>
                    <p>Qty: {quantity}</p>
                    <p>{formatPrice(price * quantity)}</p>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
}

export default OrderDetails;
