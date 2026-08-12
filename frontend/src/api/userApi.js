import axiosClient from "./axiosClient";

export async function getProfile() {
  try {
    const response = await axiosClient.get("/api/user/get-profile");
    return response.data;
  } catch (error) {
    const message = error.response?.data?.message || "Failed to load profile";
    throw new Error(message, { cause: error });
  }
}

export async function changePassword(currentPassword, newPassword) {
  try {
    const response = await axiosClient.post("/api/user/change-password", {
      currentPassword,
      newPassword,
    });
    return response.data;
  } catch (error) {
    const data = error.response?.data || {};
    const err = new Error(data.message || "Failed to change password");
    err.fieldErrors = data.fieldErrors;
    throw err;
  }
}
