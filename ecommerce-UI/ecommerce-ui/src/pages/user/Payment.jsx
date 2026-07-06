import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getOrderById } from "../../api/orderApi";
import { payOrder } from "../../api/paymentApi";
import { formatPrice, getApiErrorMessage } from "../../api/apiUtils";

function Payment() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    async function loadOrder() {
      try {
        const response = await getOrderById(id);
        setOrder(response);
      } catch (err) {
        setError(
          getApiErrorMessage(
            err,
            "Could not load order details. Please check the order and try again.",
          ),
        );
      } finally {
        setLoading(false);
      }
    }
    loadOrder();
  }, [id]);

  const handlePay = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);

    try {
      await payOrder(id, { paymentMethod });
      setSuccess("Payment successful. Order confirmed.");
      setTimeout(() => navigate(`/orders/${id}`), 700);
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Payment failed. Please verify your payment method and try again.",
        ),
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading)
    return (
      <section className="page">
        <p>Loading payment...</p>
      </section>
    );

  return (
    <section className="page auth-page">
      <form className="form-card" onSubmit={handlePay}>
        <h1>Pay Order #{id}</h1>
        {error && <p className="error-text">{error}</p>}
        {success && <p className="success-text">{success}</p>}
        {order && (
          <p>
            Total:{" "}
            <strong>
              {formatPrice(
                order.totalAmount ?? order.orderTotal ?? order.total,
              )}
            </strong>
          </p>
        )}
        <label>
          Payment Method
          <select
            value={paymentMethod}
            onChange={(event) => setPaymentMethod(event.target.value)}
          >
            <option value="UPI">UPI</option>
            <option value="CARD">Card</option>
            <option value="CASH_ON_DELIVERY">Cash on Delivery</option>
            <option value="NET_BANKING">Net Banking</option>
          </select>
        </label>
        <button className="button" type="submit" disabled={submitting}>
          {submitting ? "Processing..." : "Pay Now"}
        </button>
        <Link className="button secondary" to={`/orders/${id}`}>
          Back to Order
        </Link>
      </form>
    </section>
  );
}

export default Payment;
