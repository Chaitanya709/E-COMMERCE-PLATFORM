import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllOrders, updateOrderStatus } from "../../api/orderApi";
import {
  formatPrice,
  getApiErrorMessage,
  normalizeList,
} from "../../api/apiUtils";

const statuses = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadOrders() {
      try {
        const response = await getAllOrders();
        setOrders(normalizeList(response));
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Failed to load orders. Please ensure you have admin permissions.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }
    loadOrders();
  }, []);

  const handleStatusChange = async (order, orderStatus) => {
    const id = order.id ?? order.orderId;
    try {
      const updated = await updateOrderStatus(id, { orderStatus });
      setOrders((current) =>
        current.map((item) =>
          (item.id ?? item.orderId) === id
            ? { ...item, ...updated, orderStatus }
            : item,
        ),
      );
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not update order status. Please try again.",
        ),
      );
    }
  };

  return (
    <section className="page">
      <h1>Manage Orders</h1>
      {error && <p className="error-text">{error}</p>}
      {loading ? (
        <p>Loading orders...</p>
      ) : (
        <div className="table-card card">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Total</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => {
                const id = order.id ?? order.orderId;
                const currentStatus =
                  order.orderStatus ?? order.status ?? "PENDING";
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>{order.user?.email ?? order.userEmail ?? "-"}</td>
                    <td>{formatPrice(order.totalAmount ?? order.total)}</td>
                    <td>
                      <select
                        value={currentStatus}
                        onChange={(event) =>
                          handleStatusChange(order, event.target.value)
                        }
                      >
                        {statuses.map((status) => (
                          <option key={status} value={status}>
                            {status}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td>
                      <Link className="button secondary" to={`/orders/${id}`}>
                        View
                      </Link>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default AdminOrders;
