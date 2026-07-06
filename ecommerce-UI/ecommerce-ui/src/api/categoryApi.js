import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const getCategories = async () => {
  const response = await axiosInstance.get("/categories");
  return unwrapApiResponse(response);
};

export const getCategoryById = async (id) => {
  const response = await axiosInstance.get(`/categories/${id}`);
  return unwrapApiResponse(response);
};

export const createCategory = async (payload) => {
  const response = await axiosInstance.post("/categories", payload);
  return unwrapApiResponse(response);
};

export const updateCategory = async (id, payload) => {
  const response = await axiosInstance.put(`/categories/${id}`, payload);
  return unwrapApiResponse(response);
};

export const deleteCategory = async (id) => {
  const response = await axiosInstance.delete(`/categories/${id}`);
  return unwrapApiResponse(response);
};
