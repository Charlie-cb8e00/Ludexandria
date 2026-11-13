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
        <div className="panel auth-card">
            <h1>Crear cuenta</h1>
            <p>Regístrate en Ludexandria</p>
            <form onSubmit={handleSubmit}>
                <label>
                    Nombre
                    <input
                        type="text"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                        required
                    />
                </label>
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
                        minLength={6}
                    />
                </label>
                {error && <p className="alert alert--error" role="alert">{error}</p>}
                {ok && <p className="alert alert--success" role="status">Cuenta creada. Redirigiendo…</p>}
                <button type="submit">Registrarme</button>
            </form>
            <p>
                ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
            </p>
        </div>
    );
}