import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/client";

export default function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [nombre, setNombre] = useState("");
    const [error, setError] = useState("");
    const [ok, setOk] = useState(false);
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setOk(false);

        if (password.length < 6) {
            setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        try {
            const { data } = await api.post("/auth/registro", { email, password, nombre });
            localStorage.setItem("token", data.token);
            setOk(true);
            setTimeout(() => navigate("/"), 800);
        } catch (err) {
            setError(err.response?.data?.message || "No se pudo crear la cuenta. Comprueba el backend y los datos.");
        }
    }

    return (
        <div style={{ maxWidth: 400, margin: "3rem auto" }}>
            <h1>Crear cuenta</h1>
            <p>Regístrate en Ludexandria</p>
            <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12 }}>
                <label>
                    Nombre
                    <input
                        type="text"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                        required
                        style={{ width: "100%", padding: 8 }}
                    />
                </label>
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
                        minLength={6}
                        style={{ width: "100%", padding: 8 }}
                    />
                </label>
                {error && <p style={{ color: "crimson" }}>{error}</p>}
                {ok && <p style={{ color: "green" }}>Cuenta creada. Redirigiendo…</p>}
                <button type="submit">Registrarme</button>
            </form>
            <p style={{ marginTop: 12 }}>
                ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
            </p>
        </div>
    );
}