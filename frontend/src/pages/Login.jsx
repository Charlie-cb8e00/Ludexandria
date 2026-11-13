import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
<<<<<<< HEAD
import TorchBackground from "../components/TorchBackground";
import "./Login.css";
=======
import api from "../api/client";
>>>>>>> parent of 34647ca (frontend)

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        try {
            const { data } = await api.post("/auth/login", { email, password });
            localStorage.setItem("token", data.token);
            navigate("/");
        } catch (err) {
            setError(err.response?.data?.message || "No se pudo iniciar sesión. Comprueba el backend y tus credenciales.");
        }
    }

    return (
        <div style={{ maxWidth: 400, margin: "3rem auto" }}>
            <h1>Iniciar sesión</h1>
            <p>Ludexandria – Tu biblioteca de videojuegos</p>
            <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12 }}>
                <label>
                    Email
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        style={{ width: "100%", padding: 8 }}
                    />
                </label>
                <label>
                    Contraseña
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{ width: "100%", padding: 8 }}
                    />
                </label>
                {error && <p style={{ color: "crimson" }}>{error}</p>}
                <button type="submit">Entrar</button>
            </form>
            <p style={{ marginTop: 12 }}>
                ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
            </p>
        </div>
    );
}