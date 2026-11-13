import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // Spring Boot
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
    if (error.response?.status === 403 && !url.startsWith("/admin")) {
      localStorage.removeItem("token");
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