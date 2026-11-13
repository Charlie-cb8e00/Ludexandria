import { Link } from "react-router-dom";

const COVER_PLACEHOLDER = "/cover-placeholder.svg";

export default function GameCard({ juego, bibliotecaItem }) {
    const portadaTarjeta = juego.urlPortada?.replace("/t_cover_big/", "/t_cover_small/");
    const steamCoverPath = portadaTarjeta?.match(
        /^(https:\/\/[^/]+\/steam\/apps\/\d+)\/library_600x900\.jpg$/,
    )?.[1];
    const portadaAlternativa = steamCoverPath ? `${steamCoverPath}/header.jpg` : null;

    return (
        <Link to={`/juegos/${juego.id}`} style={{ textDecoration: "none", color: "inherit" }}>
            <article className="game-card" data-status={bibliotecaItem?.estado} style={{ width: 180 }}>
                <img
                    key={portadaTarjeta || COVER_PLACEHOLDER}
                    src={portadaTarjeta || COVER_PLACEHOLDER}
                    alt={juego.nombre}
                    className="game-card__cover"
                    width="180"
                    height="240"
                    loading="lazy"
                    decoding="async"
                    fetchPriority="low"
                    referrerPolicy="no-referrer"
                    onError={(event) => {
                        const image = event.currentTarget;
                        if (portadaAlternativa && image.dataset.alternate !== "used") {
                            image.dataset.alternate = "used";
                            image.src = portadaAlternativa;
                            return;
                        }
                        image.onerror = null;
                        image.src = COVER_PLACEHOLDER;
                    }}
                />
                <div className="game-card__body">
                    <strong className="game-card__title">{juego.nombre}</strong>
                    {juego.notaIgdb != null && <p className="game-card__score">★ {juego.notaIgdb}</p>}
                    {bibliotecaItem && (
                        <div className="game-card__library-meta">
                            <span><strong>Estado:</strong> {bibliotecaItem.estado}</span>
                            <span><strong>Nota:</strong> {bibliotecaItem.notaPersonal ?? "—"}</span>
                            <span><strong>Horas:</strong> {bibliotecaItem.horasJugadas ?? 0}</span>
                        </div>
                    )}
                </div>
            </article>
        </Link>
    );
}