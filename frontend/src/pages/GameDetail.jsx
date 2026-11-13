import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api, { getApiErrorMessage } from "../api/client";

const PLACEHOLDER_COVER = "/cover-placeholder.svg";
const WIKIPEDIA_SOURCES = [
    { domain: "es.wikipedia.org", suffix: "_(videojuego)" },
    { domain: "en.wikipedia.org", suffix: "_(video_game)" },
];
const ESTADOS_BIBLIOTECA = ["pendiente", "jugando", "completado", "abandonado"];

async function buscarResumenWikipedia(nombre, signal) {
    let lastError;
    let bestArticle = null;
    const titulo = nombre.trim().replace(/\s+/g, "_");
    const candidates = [
        ...WIKIPEDIA_SOURCES.map((source) => ({ source, title: `${titulo}${source.suffix}` })),
        ...WIKIPEDIA_SOURCES.map((source) => ({ source, title: titulo })),
    ];

    for (const { source, title } of candidates) {
        const url = `https://${source.domain}/api/rest_v1/page/summary/${encodeURIComponent(title)}`;
        try {
            const response = await fetch(url, {
                signal,
                headers: { Accept: "application/json" },
            });
            if (response.status === 404) continue;
            if (!response.ok) {
                lastError = new Error(`Wikipedia respondió con HTTP ${response.status}`);
                continue;
            }

            const article = await response.json();
            if (article.type === "disambiguation") continue;
            const image = article.thumbnail || article.originalimage;
            if (!article.extract && !image?.source) continue;

            const result = {
                description: article.description,
                extract: article.extract,
                image: image?.source,
                url: article.content_urls?.desktop?.page,
            };
            const ratio = image?.width && image?.height ? image.width / image.height : 0;
            if (!bestArticle || (ratio > 0.4 && ratio < 0.9)) bestArticle = result;
            if (ratio > 0.4 && ratio < 0.9) return result;
        } catch (error) {
            if (signal.aborted) throw error;
            lastError = error;
        }
    }

    if (bestArticle) return bestArticle;
    if (lastError) throw lastError;
    return null;
}

function formatearFecha(fecha) {
    if (!fecha) return "Sin datos";
    const value = new Date(`${fecha}T00:00:00`);
    if (Number.isNaN(value.getTime())) return "Sin datos";
    return new Intl.DateTimeFormat("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
    }).format(value);
}

function formatearDuracion(minutos) {
    if (!minutos || minutos < 1) return "Sin datos";
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    if (horas === 0) return `${resto} min`;
    return resto ? `${horas} h ${resto} min` : `${horas} h`;
}

export default function GameDetail() {
    const { id } = useParams();
    const [gameResult, setGameResult] = useState({ id: null, game: null, error: "" });
    const [wikiResult, setWikiResult] = useState({ id: null, article: null, error: "" });
    const [libraryResult, setLibraryResult] = useState({ id: null, item: null, error: "" });
    const [libraryForm, setLibraryForm] = useState({ estado: "pendiente", notaPersonal: "", horasJugadas: 0 });
    const [libraryAction, setLibraryAction] = useState({ saving: false, error: "", notice: "" });
    const [failedImage, setFailedImage] = useState("");

    useEffect(() => {
        const controller = new AbortController();
        api.get(`/juegos/${id}`, { signal: controller.signal })
            .then(({ data }) => setGameResult({ id, game: data, error: "" }))
            .catch((error) => {
                if (error.code !== "ERR_CANCELED") {
                    setGameResult({
                        id,
                        game: null,
                        error: getApiErrorMessage(error, "No se pudo cargar la ficha del juego."),
                    });
                }
            });
        return () => controller.abort();
    }, [id]);

    const game = gameResult.id === id ? gameResult.game : null;
    const gameError = gameResult.id === id ? gameResult.error : "";
    const gameLoading = gameResult.id !== id;
    const gameId = game?.id ?? null;
    const gameName = game?.nombre ?? "";

    useEffect(() => {
        if (gameId == null || !gameName) return undefined;
        const controller = new AbortController();
        buscarResumenWikipedia(gameName, controller.signal)
            .then((article) => setWikiResult({ id: gameId, article, error: "" }))
            .catch(() => {
                if (!controller.signal.aborted) {
                    setWikiResult({
                        id: gameId,
                        article: null,
                        error: "No se pudo consultar Wikipedia; se muestran los datos guardados.",
                    });
                }
            });
        return () => controller.abort();
    }, [gameId, gameName]);

    useEffect(() => {
        if (gameId == null) return undefined;
        const controller = new AbortController();
        // Consulta solo la entrada de este juego, no toda la biblioteca del usuario.
        api.get(`/biblioteca/juego/${gameId}`, { signal: controller.signal })
            .then(({ data }) => {
                const item = data || null;
                setLibraryResult({ id: gameId, item, error: "" });
                if (item) {
                    setLibraryForm({
                        estado: item.estado,
                        notaPersonal: item.notaPersonal ?? "",
                        horasJugadas: item.horasJugadas ?? 0,
                    });
                }
            })
            .catch((error) => {
                if (error.code !== "ERR_CANCELED") {
                    setLibraryResult({
                        id: gameId,
                        item: null,
                        error: getApiErrorMessage(error, "No se pudo consultar tu biblioteca."),
                    });
                }
            });
        return () => controller.abort();
    }, [gameId]);

    if (gameLoading) return <p role="status">Cargando ficha…</p>;

    if (gameError || !game) {
        return (
            <section>
                <p role="alert">{gameError || "Juego no encontrado."}</p>
                <Link to="/biblioteca">Volver a mi biblioteca</Link>
            </section>
        );
    }

    const article = wikiResult.id === game.id ? wikiResult.article : null;
    const wikiLoading = wikiResult.id !== game.id;
    const libraryItem = libraryResult.id === game.id ? libraryResult.item : null;
    const libraryLoading = libraryResult.id !== game.id;
    const libraryError = libraryResult.id === game.id ? libraryResult.error : "";
    const coverCandidates = [game.urlPortada, article?.image].filter(Boolean);
    const description = article?.extract || game.resumen;
    const imageSrc = coverCandidates.find((source) => source !== failedImage) || PLACEHOLDER_COVER;
    const imageFromWikipedia = Boolean(article?.image) && imageSrc === article.image;

    async function anadirABiblioteca() {
        setLibraryAction({ saving: true, error: "", notice: "" });
        try {
            const { data } = await api.post("/biblioteca", {
                idJuego: game.id,
                estado: "pendiente",
                notaPersonal: null,
                horasJugadas: 0,
            });
            const item = { ...data, juego: game };
            setLibraryResult({ id: game.id, item, error: "" });
            setLibraryForm({ estado: item.estado, notaPersonal: "", horasJugadas: 0 });
            setLibraryAction({ saving: false, error: "", notice: "Añadido a tu biblioteca." });
        } catch (error) {
            setLibraryAction({
                saving: false,
                error: getApiErrorMessage(error, "No se pudo añadir el juego."),
                notice: "",
            });
        }
    }

    async function guardarBiblioteca(event) {
        event.preventDefault();
        if (!libraryItem) return;
        setLibraryAction({ saving: true, error: "", notice: "" });
        try {
            const { data } = await api.put(`/biblioteca/${libraryItem.id}`, {
                idJuego: game.id,
                estado: libraryForm.estado,
                notaPersonal: libraryForm.notaPersonal === "" ? null : Number(libraryForm.notaPersonal),
                horasJugadas: Number(libraryForm.horasJugadas),
            });
            setLibraryResult({ id: game.id, item: data, error: "" });
            setLibraryForm({
                estado: data.estado,
                notaPersonal: data.notaPersonal ?? "",
                horasJugadas: data.horasJugadas ?? 0,
            });
            setLibraryAction({ saving: false, error: "", notice: "Cambios guardados." });
        } catch (error) {
            setLibraryAction({
                saving: false,
                error: getApiErrorMessage(error, "No se pudieron guardar los cambios."),
                notice: "",
            });
        }
    }

    return (
        <section>
            <p><Link to="/biblioteca">← Volver a mi biblioteca</Link></p>
            <article className="game-detail">
                <div className="game-detail__cover">
                    <img
                        key={game.id}
                        src={imageSrc}
                        alt={`Portada de ${game.nombre}`}
                        referrerPolicy="no-referrer"
                        onError={() => setFailedImage(imageSrc)}
                    />
                    {article?.url && (
                        <a href={article.url} target="_blank" rel="noreferrer">
                            {imageFromWikipedia ? "Ver fuente de la imagen" : "Ver el artículo"}
                        </a>
                    )}
                </div>

                <div className="game-detail__content">
                    {article?.description && <p className="game-detail__eyebrow">{article.description}</p>}
                    <h1>{game.nombre}</h1>
                    <p className="game-detail__summary">
                        {description || "Todavía no hay una descripción disponible para este juego."}
                    </p>

                    <dl className="game-detail__facts">
                        <div>
                            <dt>Fecha de lanzamiento</dt>
                            <dd>{formatearFecha(game.fechaSalida)}</dd>
                        </div>
                        <div>
                            <dt>Valoración IGDB</dt>
                            <dd>{game.notaIgdb == null ? "Sin datos" : `${game.notaIgdb.toFixed(1)} / 100`}</dd>
                        </div>
                        <div>
                            <dt>Duración estimada</dt>
                            <dd>{formatearDuracion(game.duracionMinutos)}</dd>
                        </div>
                        <div>
                            <dt>Ficha</dt>
                            <dd>{article ? "Datos complementados desde Wikipedia" : "Catálogo Ludexandria"}</dd>
                        </div>
                    </dl>

                    <section className="game-detail__library" aria-labelledby="library-info-title">
                        <h2 id="library-info-title">Mi biblioteca</h2>
                        {libraryLoading && <p role="status">Consultando tu biblioteca…</p>}
                        {libraryError && <p role="alert">{libraryError}</p>}
                        {!libraryLoading && !libraryError && libraryItem && (
                            <>
                                <form className="game-detail__library-form" onSubmit={guardarBiblioteca}>
                                    <label>
                                        Estado
                                        <select
                                            value={libraryForm.estado}
                                            onChange={(event) => setLibraryForm({ ...libraryForm, estado: event.target.value })}
                                        >
                                            {ESTADOS_BIBLIOTECA.map((estado) => (
                                                <option key={estado} value={estado}>{estado}</option>
                                            ))}
                                        </select>
                                    </label>
                                    <label>
                                        Nota personal (0-10)
                                        <input
                                            type="number"
                                            min="0"
                                            max="10"
                                            step="0.5"
                                            value={libraryForm.notaPersonal}
                                            onChange={(event) => setLibraryForm({ ...libraryForm, notaPersonal: event.target.value })}
                                        />
                                    </label>
                                    <label>
                                        Horas jugadas
                                        <input
                                            type="number"
                                            min="0"
                                            step="0.1"
                                            value={libraryForm.horasJugadas}
                                            onChange={(event) => setLibraryForm({ ...libraryForm, horasJugadas: event.target.value })}
                                        />
                                    </label>
                                    <button type="submit" disabled={libraryAction.saving}>
                                        {libraryAction.saving ? "Guardando…" : "Guardar cambios"}
                                    </button>
                                </form>
                                {libraryAction.notice && <p role="status">{libraryAction.notice}</p>}
                                {libraryAction.error && <p role="alert">{libraryAction.error}</p>}
                            </>
                        )}
                        {!libraryLoading && !libraryError && !libraryItem && (
                            <>
                                <p>Este juego todavía no está en tu biblioteca.</p>
                                <button type="button" onClick={anadirABiblioteca} disabled={libraryAction.saving}>
                                    {libraryAction.saving ? "Añadiendo…" : "Añadir a mi biblioteca"}
                                </button>
                                {libraryAction.error && <p role="alert">{libraryAction.error}</p>}
                            </>
                        )}
                    </section>

                    {wikiLoading && <p role="status">Buscando imagen e información en Wikipedia…</p>}
                    {wikiResult.id === game.id && wikiResult.error && (
                        <p role="status">{wikiResult.error}</p>
                    )}
                    {article?.url && (
                        <p className="game-detail__source">
                            {imageFromWikipedia ? "Información e imagen: " : "Información complementaria: "}
                            <a href={article.url} target="_blank" rel="noreferrer">Wikipedia</a>
                        </p>
                    )}
                </div>
            </article>
        </section>
    );
}
