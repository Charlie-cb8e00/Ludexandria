import { Link, useNavigate } from "react-router-dom";
import profileIcon from "../assets/profile-icon.png";
import ThemeSelector from "./ThemeSelector";

export default function Navbar() {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    function logout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <nav className="navbar">
            <Link to="/" className="navbar__brand">Ludexandria</Link>
            {token && (
                <>
                    <Link to="/">Inicio</Link>
                    <Link to="/buscar">Buscar</Link>
                    <Link to="/biblioteca">Biblioteca</Link>
                    <Link to="/asistente">Asistente IA</Link>
                    <Link to="/importar">Importar CSV</Link>
                </>
            )}
            <div className="navbar__actions">
                {token && <button onClick={logout}>Salir</button>}
                <ThemeSelector />
                {token && (
                    <Link
                        to="/perfil"
                        className="navbar__profile"
                        aria-label="Perfil"
                        title="Perfil"
                        style={{ "--icon-mask": `url("${profileIcon}")` }}
                    >
                        <span className="navbar__profile-icon" aria-hidden="true" />
                    </Link>
                )}
            </div>
        </nav>
    );
}