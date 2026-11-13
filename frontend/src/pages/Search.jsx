import { useEffect, useState } from "react";
import api, { getApiErrorMessage } from "../api/client";
import GameCard from "../components/GameCard";

export default function Search() {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [warnings, setWarnings] = useState([]);
    const [searchedQuery, setSearchedQuery] = useState("");
    const [sources, setSources] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        let active = true;
        api.get("/juegos/fuentes")
            .then(({ data }) => {
                if (active) setSources(data);
            })
            .catch((requestError) => {
                if (active) setError(getApiErrorMessage(requestError, "No se pudo consultar el estado del catálogo."));
            });
        return () => {
            active = false;
        };
    }, []);

    async function buscar(event) {
        event.preventDefault();
        const termino = query.trim();
        if (!termino) return;

        setLoading(true);
        setError("");
        setWarnings([]);
        setSearchedQuery(termino);
        try {
            const { data } = await api.get("/juegos/buscar", { params: { q: termino, page: 0, size: 20 } });
            setResults(data.juegos);
            setWarnings(data.advertencias);
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudieron buscar juegos."));
            setResults([]);
            setWarnings([]);
        } finally {
            setLoading(false);
        }
    }

    return (
        <section>
            <header className="page-header">
                <h1>Buscar juegos</h1>
            </header>
            <p>Busca en tu catálogo local, Steam y, si está configurado, IGDB. Los resultados se guardan en la base de datos.</p>
            {sources && (
                <p role="status">
                    Steam: integrado (se comprobará al buscar) · IGDB: {sources.igdb ? "configurado" : "sin configurar (requiere IGDB_CLIENT_ID e IGDB_CLIENT_SECRET)"}
                </p>
            )}

            <form onSubmit={buscar} className="search-form">
                <label htmlFor="game-query">Nombre del juego</label>
                <input
                    id="game-query"
                    type="search"
                    value={query}
                    onChange={(event) => setQuery(event.target.value)}
                    placeholder="Ej.: Hades, Stardew Valley…"
                    maxLength={100}
                />
                <button type="submit" disabled={loading || !query.trim()}>
                    {loading ? "Buscando…" : "Buscar"}
                </button>
            </form>

            {error && <p role="alert">{error}</p>}
            {warnings.map((warning) => <p role="alert" key={warning}>{warning}</p>)}
            {loading && <p role="status">Consultando los catálogos…</p>}
            {!loading && searchedQuery && results.length === 0 && (
                <p role="status">No se encontraron juegos para «{searchedQuery}».</p>
            )}
            {results.length > 0 && (
                <div className="game-grid" aria-label="Resultados de búsqueda">
                    {results.map((juego) => <GameCard key={juego.id} juego={juego} />)}
                </div>
            )}
        </section>
    );
}
