import { Link, NavLink, useNavigate } from "react-router-dom";
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
            {token && <div className="navbar__links">
                <NavLink end to="/">Inicio</NavLink>
                <NavLink to="/buscar">Buscar</NavLink>
                <NavLink to="/biblioteca">Biblioteca</NavLink>
                <NavLink to="/asistente">Asistente IA</NavLink>
                <NavLink to="/importar">Importar CSV</NavLink>
            </div>}
            <div className="navbar__actions">
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
                {token && <button className="navbar__logout" onClick={logout}>Salir</button>}
            </div>
        </nav>
    );
}