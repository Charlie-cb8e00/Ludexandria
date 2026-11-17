import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL?.trim() || "http://localhost:8080/api";

const api = axios.create({
  baseURL,
});

// Añade el JWT en cada petición
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error.config?.url || "";
    const isAuthRequest = url.startsWith("/auth/login") || url.startsWith("/auth/registro");
    if ([401, 403].includes(error.response?.status) && !isAuthRequest && !url.startsWith("/admin")) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.assign("/login");
    }
    return Promise.reject(error);
  },
);

export function getApiErrorMessage(error, fallback) {
  if (!error.response) {
    return "No se pudo conectar con el servidor. Comprueba que el backend esté en marcha.";
  }
  if (error.response.status === 403) {
    return "La sesión no es válida. Inicia sesión de nuevo.";
  }
  return error.response.data?.message || fallback;
}

export default api;