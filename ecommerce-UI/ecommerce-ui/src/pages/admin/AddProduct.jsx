import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProduct } from "../../api/productApi";
import { getCategories } from "../../api/categoryApi";
import {
  getApiErrorMessage,
  getCategoryId,
  normalizeList,
} from "../../api/apiUtils";

const initialForm = {
  name: "",
  description: "",
  price: "",
  stockQuantity: "",
  imageUrl: "",
  categoryId: "",
};

function AddProduct() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getCategories()
      .then((response) => setCategories(normalizeList(response)))
      .catch(() => setCategories([]));
  }, []);

  const handleChange = (event) =>
    setForm({ ...form, [event.target.name]: event.target.value });

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      await createProduct({
        ...form,
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        categoryId: Number(form.categoryId),
      });
      navigate("/admin/products");
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Failed to create product. Please check all fields are filled correctly and try again.",
        ),
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page">
      <h1>Add Product</h1>
      <form className="form-card wide-form" onSubmit={handleSubmit}>
        {error && <p className="error-text">{error}</p>}
        <label>
          Name
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Description
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Price
          <input
            name="price"
            type="number"
            min="0"
            step="0.01"
            value={form.price}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Stock Quantity
          <input
            name="stockQuantity"
            type="number"
            min="0"
            value={form.stockQuantity}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Image URL
          <input
            name="imageUrl"
            value={form.imageUrl}
            onChange={handleChange}
          />
        </label>
        <label>
          Category
          <select
            name="categoryId"
            value={form.categoryId}
            onChange={handleChange}
            required
          >
            <option value="">Select category</option>
            {categories.map((category) => (
              <option
                key={getCategoryId(category)}
                value={getCategoryId(category)}
              >
                {category.name}
              </option>
            ))}
          </select>
        </label>
        <button className="button" type="submit" disabled={loading}>
          {loading ? "Saving..." : "Save Product"}
        </button>
      </form>
    </section>
  );
}

export default AddProduct;
