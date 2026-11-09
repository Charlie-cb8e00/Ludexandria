import { Link } from "react-router-dom";

export default function GameCard({ juego }) {
    return (
        <Link to={`/juegos/${juego.id}`} style={{ textDecoration: "none", color: "inherit" }}>
            <article style={{ border: "1px solid #eee", borderRadius: 12, overflow: "hidden", width: 180 }}>
                <img
                    src={juego.portada || "https://placehold.co/180x240?text=Sin+portada"}
                    alt={juego.nombre}
                    style={{ width: "100%", height: 240, objectFit: "cover" }}
                />
                <div style={{ padding: 8 }}>
                    <strong style={{ fontSize: 14 }}>{juego.nombre}</strong>
                    {juego.notaIgdb != null && <p style={{ margin: 0, fontSize: 12 }}>★ {juego.notaIgdb}</p>}
                </div>
            </article>
        </Link>
    );
}