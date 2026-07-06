import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const payOrder = async (orderId, payload) => {
  const response = await axiosInstance.post(`/payments/orders/${orderId}`, payload);
  return unwrapApiResponse(response);
};

export const getPaymentByOrderId = async (orderId) => {
  const response = await axiosInstance.get(`/payments/orders/${orderId}`);
  return unwrapApiResponse(response);
};
