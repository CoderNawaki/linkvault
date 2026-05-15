const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error("Request failed. Please try again.");
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function getLinks() {
  return request("/api/links");
}

export function createLink(payload) {
  return request("/api/links", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function deleteLink(id) {
  return request(`/api/links/${id}`, {
    method: "DELETE",
  });
}
