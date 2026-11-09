import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    function logout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    if (!token) return null;

    return (
        <nav style={{ display: "flex", gap: "1rem", padding: "0.75rem 1rem", borderBottom: "1px solid #ddd" }}>
            <Link to="/">Inicio</Link>
            <Link to="/buscar">Buscar</Link>
            <Link to="/biblioteca">Biblioteca</Link>
            <Link to="/asistente">Asistente IA</Link>
            <Link to="/importar">Importar CSV</Link>
            <Link to="/perfil">Perfil</Link>
            <button onClick={logout}>Salir</button>
        </nav>
    );
}