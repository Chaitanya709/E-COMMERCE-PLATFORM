import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const getProducts = async () => {
  const response = await axiosInstance.get("/products");
  return unwrapApiResponse(response);
};

export const getProductById = async (id) => {
  const response = await axiosInstance.get(`/products/${id}`);
  return unwrapApiResponse(response);
};

export const getProductsByCategory = async (categoryId) => {
  const response = await axiosInstance.get(`/products/category/${categoryId}`);
  return unwrapApiResponse(response);
};

export const searchProducts = async (keyword) => {
  const response = await axiosInstance.get("/products/search", {
    params: { keyword },
  });
  return unwrapApiResponse(response);
};

export const createProduct = async (payload) => {
  const response = await axiosInstance.post("/products", payload);
  return unwrapApiResponse(response);
};

export const updateProduct = async (id, payload) => {
  const response = await axiosInstance.put(`/products/${id}`, payload);
  return unwrapApiResponse(response);
};

export const deleteProduct = async (id) => {
  const response = await axiosInstance.delete(`/products/${id}`);
  return unwrapApiResponse(response);
};
