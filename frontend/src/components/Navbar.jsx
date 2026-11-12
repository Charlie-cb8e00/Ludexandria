import { Link, useNavigate } from "react-router-dom";
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
            {token && (
                <>
                    <Link to="/">Inicio</Link>
                    <Link to="/buscar">Buscar</Link>
                    <Link to="/biblioteca">Biblioteca</Link>
                    <Link to="/asistente">Asistente IA</Link>
                    <Link to="/importar">Importar CSV</Link>
                    <Link to="/perfil">Perfil</Link>
                </>
            )}
            <div className="navbar__actions">
                {token && <button onClick={logout}>Salir</button>}
                <ThemeSelector />
            </div>
        </nav>
    );
}