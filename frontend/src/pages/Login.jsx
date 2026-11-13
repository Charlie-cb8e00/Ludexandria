import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/client";

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
        <div className="panel auth-card">
            <h1>Iniciar sesión</h1>
            <p>Ludexandria – Tu biblioteca de videojuegos</p>
            <form onSubmit={handleSubmit}>
                <label>
                    Email
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Contraseña
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </label>
                {error && <p className="alert alert--error" role="alert">{error}</p>}
                <button type="submit">Entrar</button>
            </form>
            <p>
                ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
            </p>
        </div>
    );
}