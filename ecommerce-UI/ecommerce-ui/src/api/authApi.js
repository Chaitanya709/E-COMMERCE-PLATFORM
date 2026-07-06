import axiosInstance from "./axiosInstance";
import { unwrapApiResponse } from "./apiUtils";

export const loginUser = async (payload) => {
  const response = await axiosInstance.post("/auth/login", payload);
  return unwrapApiResponse(response);
};

export const registerUser = async (payload) => {
  const response = await axiosInstance.post("/auth/register", payload);
  return unwrapApiResponse(response);
};
