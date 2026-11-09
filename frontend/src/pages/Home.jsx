import { Link } from "react-router-dom";

export default function Home() {
    return (
        <div>
            <h1>Ludexandria</h1>
            <p style={{ color: "#555", maxWidth: 560 }}>
                Tu biblioteca personal de videojuegos. Organiza el backlog, guarda estado,
                nota y horas, e elige a qué jugar con ayuda de la IA.
            </p>

            <section style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))", gap: 16, marginTop: 24 }}>
                <Link to="/biblioteca" style={cardStyle}>
                    <strong>Mi biblioteca</strong>
                    <span style={descStyle}>Pendientes, jugando, completados…</span>
                </Link>

                <Link to="/buscar" style={cardStyle}>
                    <strong>Buscar juegos</strong>
                    <span style={descStyle}>Catálogo con datos de IGDB</span>
                </Link>

                <Link to="/asistente" style={cardStyle}>
                    <strong>Asistente IA</strong>
                    <span style={descStyle}>¿A qué juego hoy según tiempo y ánimo?</span>
                </Link>

                <Link to="/importar" style={cardStyle}>
                    <strong>Importar CSV</strong>
                    <span style={descStyle}>Carga tu biblioteca desde un archivo</span>
                </Link>
            </section>
        </div>
    );
}

const cardStyle = {
    display: "flex",
    flexDirection: "column",
    gap: 8,
    padding: "16px 18px",
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    textDecoration: "none",
    color: "inherit",
    background: "#fff",
    boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
};

const descStyle = {
    fontSize: 13,
    color: "#64748b",
};