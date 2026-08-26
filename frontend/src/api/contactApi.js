import axiosClient from "./axiosClient";

export async function getAllContacts(page = 0, search = "") {
  try {
    const response = await axiosClient.get("/api/contacts/get-all-contacts", {
      params: { page: page, search: search || undefined },
    });
    return response.data;
  } catch (error) {
    const message = error.response?.data?.message || "Failed to load contacts";
    throw new Error(message, { cause: error });
  }
}

export async function addContact(data) {
  try {
    const response = await axiosClient.post("/api/contacts/add-contact", data);
    return response.data;
  } catch (error) {
    const data = error.response?.data || {};
    const err = new Error(data.message || "Failed to add contact");
    err.fieldErrors = data.fieldErrors;
    throw err;
  }
}

export async function getContact(id) {
  try {
    const response = await axiosClient.get(`/api/contacts/get-contact/${id}`);
    return response.data;
  } catch (error) {
    const message = error.response?.data?.message || "unable to find contact";
    throw new Error(message, { cause: error });
  }
}

export async function deleteContact(id) {
  try {
    const response = await axiosClient.delete(
      `/api/contacts/delete-contact/${id}`,
    );
    return response.data;
  } catch (error) {
    const message = error.response?.data?.message || "Failed to delete contact";
    throw new Error(message, { cause: error });
  }
}

export async function updateContact(id, data) {
  try {
    const response = await axiosClient.put(
      `/api/contacts/update-contact/${id}`,
      data,
    );
    return response.data;
  } catch (error) {
    const data = error.response?.data || {};
    const err = new Error(data.message || "Failed to update contact");
    err.fieldErrors = data.fieldErrors;
    throw err;
  }
}

export async function exportContacts() {
  try {
    const response = await axiosClient.get("api/contacts/export-contacts", {
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "contacts.csv");
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    const message =
      error.response?.data?.message || "Failed to export contacts";
    throw new Error(message, { cause: error });
  }
}

export async function importContacts(file) {
  try {
    const formData = new FormData();
    formData.append("file", file);
    const response = await axiosClient.post(
      "api/contacts/import-contacts",
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      },
    );
    return response.data;
  } catch (error) {
    const message =
      error.response?.data?.message || "Failed to import contacts";
    throw new Error(message, { cause: error });
  }
}
