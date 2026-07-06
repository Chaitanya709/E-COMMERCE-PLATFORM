import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const createOrder = async (payload) => {
  const response = await axiosInstance.post("/orders", payload);
  return unwrapApiResponse(response);
};

export const getMyOrders = async () => {
  const response = await axiosInstance.get("/orders/me");
  return unwrapApiResponse(response);
};

export const getOrderById = async (id) => {
  const response = await axiosInstance.get(`/orders/${id}`);
  return unwrapApiResponse(response);
};

export const updateOrderStatus = async (id, payload) => {
  const response = await axiosInstance.put(`/orders/${id}/status`, payload);
  return unwrapApiResponse(response);
};

export const getAllOrders = async () => {
  const response = await axiosInstance.get("/orders/admin/all");
  return unwrapApiResponse(response);
};
