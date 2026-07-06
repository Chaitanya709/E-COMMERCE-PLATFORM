import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const getUsers = async () => {
  const response = await axiosInstance.get(`/users`);
  return unwrapApiResponse(response);
};

export const getCurrentUser = async () => {
  const response = await axiosInstance.get(`/users/me`);
  return unwrapApiResponse(response);
};

export const getUserById = async (id) => {
  const response = await axiosInstance.get(`/users/${id}`);
  return unwrapApiResponse(response);
};

export const updateUser = async (id, payload) => {
  const response = await axiosInstance.put(`/users/${id}`, payload);
  return unwrapApiResponse(response);
};

export const deleteUser = async (id) => {
  const response = await axiosInstance.delete(`/users/${id}`);
  return unwrapApiResponse(response);
};
