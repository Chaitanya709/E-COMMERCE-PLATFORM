import { useEffect, useState } from "react";
import {
  createCategory,
  deleteCategory,
  getCategories,
  updateCategory,
} from "../../api/categoryApi";
import {
  getApiErrorMessage,
  getCategoryId,
  normalizeList,
} from "../../api/apiUtils";

const emptyForm = { name: "", description: "" };

function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const loadCategories = async () => {
    try {
      const response = await getCategories();
      setCategories(normalizeList(response));
    } catch (err) {
      setError(
        getApiErrorMessage(err, "Failed to load categories. Please try again."),
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleChange = (event) =>
    setForm({ ...form, [event.target.name]: event.target.value });

  const handleEdit = (category) => {
    setEditingId(getCategoryId(category));
    setForm({
      name: category.name ?? "",
      description: category.description ?? "",
    });
  };

  const resetForm = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    try {
      if (editingId) {
        await updateCategory(editingId, form);
      } else {
        await createCategory(form);
      }
      resetForm();
      await loadCategories();
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Failed to save category. Please check your input and try again.",
        ),
      );
    }
  };

  const handleDelete = async (category) => {
    const id = getCategoryId(category);
    if (!window.confirm(`Delete ${category.name}?`)) return;
    try {
      await deleteCategory(id);
      setCategories((current) =>
        current.filter((item) => getCategoryId(item) !== id),
      );
    } catch (err) {
      setError(
        getApiErrorMessage(
          err,
          "Could not delete this category. Please try again.",
        ),
      );
    }
  };

  return (
    <section className="page">
      <div className="page-header">
        <h1>Manage Categories</h1>
      </div>
      {error && <p className="error-text">{error}</p>}
      <form className="form-card wide-form" onSubmit={handleSubmit}>
        <h2>{editingId ? "Edit Category" : "Add Category"}</h2>
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
          />
        </label>
        <div className="button-stack horizontal">
          <button className="button" type="submit">
            {editingId ? "Update Category" : "Save Category"}
          </button>
          {editingId && (
            <button
              className="button secondary"
              type="button"
              onClick={resetForm}
            >
              Cancel
            </button>
          )}
        </div>
      </form>

      <div className="table-card card page-section">
        {loading ? (
          <p>Loading categories...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((category) => (
                <tr key={getCategoryId(category)}>
                  <td>{getCategoryId(category)}</td>
                  <td>{category.name}</td>
                  <td>{category.description}</td>
                  <td className="table-actions">
                    <button
                      className="button secondary"
                      type="button"
                      onClick={() => handleEdit(category)}
                    >
                      Edit
                    </button>
                    <button
                      className="button danger"
                      type="button"
                      onClick={() => handleDelete(category)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}

export default AdminCategories;
