import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import TorchBackground from "../components/TorchBackground";
import "./Login.css";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        try {
            // Cuando el backend esté listo:
            // const { data } = await api.post("/auth/login", { email, password });
            // localStorage.setItem("token", data.token);

            // Mientras tanto, acceso de prueba:
            localStorage.setItem("token", "demo-token");
            navigate("/");
        } catch (err) {
            setError(err.response?.data?.message || "Error al iniciar sesión");
        }
    }

    return (
        <TorchBackground centered hint="Mueve el cursor para encender la antorcha">
            <form className="login-card" onSubmit={handleSubmit}>
                <div className="login-card__crest" aria-hidden="true">
                    <svg viewBox="0 0 40 64">
                        <ellipse cx="20" cy="16" rx="10" ry="13" fill="none" stroke="currentColor" strokeWidth="5" />
                        <path d="M20 29v33M5 38h30" stroke="currentColor" strokeWidth="5" strokeLinecap="round" fill="none" />
                    </svg>
                </div>
                <h1 className="login-card__title">Ludexandria</h1>
                <p className="login-card__sub">Tu biblioteca de videojuegos</p>

                <label className="login-card__label" htmlFor="login-email">Email</label>
                <input
                    id="login-email"
                    className="login-card__input"
                    type="email"
                    placeholder="tu@correo.com"
                    autoComplete="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <label className="login-card__label" htmlFor="login-password">Contraseña</label>
                <input
                    id="login-password"
                    className="login-card__input"
                    type="password"
                    placeholder="Tu contraseña"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                <p className="login-card__error" role="alert">{error}</p>
                <button className="login-card__btn" type="submit">Entrar</button>
                <p className="login-card__alt">
                    ¿No tienes cuenta? <Link className="login-card__link" to="/register">Regístrate</Link>
                </p>
            </form>
        </TorchBackground>
    );
}
