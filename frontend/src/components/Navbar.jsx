import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import profileIcon from "../assets/profile-icon.png";
import ThemeSelector from "./ThemeSelector";

export default function Navbar() {
    const [menuOpen, setMenuOpen] = useState(false);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    function logout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    function closeMenu() {
        setMenuOpen(false);
    }

    return (
        <nav className="navbar">
            <Link to="/" className="navbar__brand">Ludexandria</Link>
            {token && (
                <>
                    <div className={`navbar__links${menuOpen ? " navbar__links--open" : ""}`} id="main-navigation">
                        <NavLink end to="/" onClick={closeMenu}>Inicio</NavLink>
                        <NavLink to="/buscar" onClick={closeMenu}>Buscar</NavLink>
                        <NavLink to="/biblioteca" onClick={closeMenu}>Biblioteca</NavLink>
                        <NavLink to="/asistente" onClick={closeMenu}>Asistente IA</NavLink>
                        <NavLink to="/importar" onClick={closeMenu}>Importar CSV</NavLink>
                    </div>
                    <button
                        type="button"
                        className="navbar__menu-toggle"
                        aria-label={menuOpen ? "Cerrar menú de navegación" : "Abrir menú de navegación"}
                        aria-expanded={menuOpen}
                        aria-controls="main-navigation"
                        onClick={() => setMenuOpen((isOpen) => !isOpen)}
                    >
                        <span aria-hidden="true" />
                        <span aria-hidden="true" />
                        <span aria-hidden="true" />
                    </button>
                </>
            )}
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