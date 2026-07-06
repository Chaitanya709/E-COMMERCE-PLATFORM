import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getProductById, updateProduct } from "../../api/productApi";
import { getCategories } from "../../api/categoryApi";
import {
  getApiErrorMessage,
  getCategoryId,
  normalizeList,
} from "../../api/apiUtils";

function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stockQuantity: "",
    imageUrl: "",
    categoryId: "",
  });
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    async function load() {
      try {
        const [product, categoryResponse] = await Promise.all([
          getProductById(id),
          getCategories(),
        ]);
        setCategories(normalizeList(categoryResponse));
        setForm({
          name: product?.name ?? "",
          description: product?.description ?? "",
          price: product?.price ?? "",
          stockQuantity: product?.stockQuantity ?? "",
          imageUrl: product?.imageUrl ?? "",
          categoryId: product?.category?.id ?? product?.categoryId ?? "",
        });
      } catch (err) {
        setError(
          getApiErrorMessage(err, "Could not load product. Please try again."),
        );
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id]);

  const handleChange = (event) =>
    setForm({ ...form, [event.target.name]: event.target.value });

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSaving(true);
    try {
      await updateProduct(id, {
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
          "Failed to update product. Please check all fields and try again.",
        ),
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading)
    return (
      <section className="page">
        <p>Loading product...</p>
      </section>
    );

  return (
    <section className="page">
      <h1>Edit Product #{id}</h1>
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
        <button className="button" type="submit" disabled={saving}>
          {saving ? "Updating..." : "Update Product"}
        </button>
      </form>
    </section>
  );
}

export default EditProduct;
