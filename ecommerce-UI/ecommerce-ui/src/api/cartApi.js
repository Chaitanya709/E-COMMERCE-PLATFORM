import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const getCart = async () => {
  const response = await axiosInstance.get("/cart");
  return unwrapApiResponse(response);
};

export const addToCart = async (payload) => {
  const response = await axiosInstance.post("/cart/items", payload);
  return unwrapApiResponse(response);
};

export const updateCartItem = async (cartItemId, payload) => {
  const response = await axiosInstance.put(`/cart/items/${cartItemId}`, payload);
  return unwrapApiResponse(response);
};

export const removeCartItem = async (cartItemId) => {
  const response = await axiosInstance.delete(`/cart/items/${cartItemId}`);
  return unwrapApiResponse(response);
};

export const clearCart = async () => {
  const response = await axiosInstance.delete("/cart");
  return unwrapApiResponse(response);
};
