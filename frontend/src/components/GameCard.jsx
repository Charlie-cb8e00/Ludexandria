import { Link } from "react-router-dom";

export default function GameCard({ juego }) {
    return (
        <Link to={`/juegos/${juego.id}`} style={{ textDecoration: "none", color: "inherit" }}>
            <article className="game-card" style={{ width: 180 }}>
                <img
                    src={juego.urlPortada || "https://placehold.co/180x240?text=Sin+portada"}
                    alt={juego.nombre}
                    className="game-card__cover"
                />
                <div className="game-card__body">
                    <strong className="game-card__title">{juego.nombre}</strong>
                    {juego.notaIgdb != null && <p className="game-card__score">★ {juego.notaIgdb}</p>}
                </div>
            </article>
        </Link>
    );
}