import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getApiErrorMessage } from "../api/client";
import GameCard from "../components/GameCard";

const ESTADOS = ["pendiente", "jugando", "completado", "abandonado"];
const ITEMS_POR_CARGA = 12;

export default function Library() {
    const [items, setItems] = useState([]);
    const [page, setPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [estado, setEstado] = useState("");
    const [refreshKey, setRefreshKey] = useState(0);
    const [loading, setLoading] = useState(true);
    const [addingPopular, setAddingPopular] = useState(false);
    const [notice, setNotice] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        let active = true;
        api
            .get("/biblioteca/pagina", {
                params: { ...(estado ? { estado } : {}), page: 0, size: ITEMS_POR_CARGA },
            })
            .then(({ data }) => {
                if (!active) return;
                setItems(data.content);
                setPage(data.page);
                setHasNext(data.hasNext);
            })
            .catch((requestError) => {
                if (active) setError(getApiErrorMessage(requestError, "No se pudo cargar la biblioteca."));
            })
            .finally(() => {
                if (active) setLoading(false);
            });
        return () => {
            active = false;
        };
    }, [estado, refreshKey]);

    async function cargarMas() {
        if (loading || !hasNext) return;
        setLoading(true);
        setError("");
        try {
            const { data } = await api.get("/biblioteca/pagina", {
                params: { ...(estado ? { estado } : {}), page: page + 1, size: ITEMS_POR_CARGA },
            });
            setItems((current) => [...current, ...data.content]);
            setPage(data.page);
            setHasNext(data.hasNext);
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudieron cargar más juegos."));
        } finally {
            setLoading(false);
        }
    }

    async function anadirPopulares() {
        setAddingPopular(true);
        setLoading(true);
        setNotice("");
        setError("");
        try {
            const response = await api.post("/biblioteca/populares");
            const { anadidos, yaExistentes } = response.data;
            setNotice(`Añadidos: ${anadidos}. Ya estaban en tu biblioteca: ${yaExistentes}.`);
            setPage(0);
            setRefreshKey((key) => key + 1);
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudieron añadir los juegos populares."));
        } finally {
            setAddingPopular(false);
        }
    }

    return (
        <section aria-labelledby="library-title">
            <h1 id="library-title">Mi biblioteca</h1>
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                <Link className="btn btn--ghost" to="/importar">Importar CSV</Link>
                <button type="button" onClick={anadirPopulares} disabled={addingPopular}>
                    {addingPopular ? "Añadiendo…" : "Añadir selección popular (50 juegos)"}
                </button>
            </div>
            {notice && <p role="status">{notice}</p>}
            {error && <p role="alert">{error}</p>}
            <label htmlFor="library-status">Filtrar por estado</label>
            <select id="library-status" value={estado} onChange={(e) => {
                setEstado(e.target.value);
                setPage(0);
                setItems([]);
                setLoading(true);
                setError("");
            }}>
                <option value="">Todos</option>
                {ESTADOS.map((e) => (
                    <option key={e} value={e}>{e}</option>
                ))}
            </select>

            {loading && page === 0 && <p role="status">Cargando biblioteca…</p>}
            <div style={{ display: "flex", flexWrap: "wrap", gap: 12, marginTop: 16 }} aria-busy={loading}>
                {items.map((item) => (
                    <div key={item.id}>
                        <GameCard juego={item.juego} bibliotecaItem={item} />
                    </div>
                ))}
            </div>
            {!loading && items.length === 0 && !error && <p role="status">No hay juegos en esta lista todavía.</p>}
            {hasNext && (
                <button
                    type="button"
                    onClick={cargarMas}
                    disabled={loading}
                >
                    {loading ? "Cargando…" : "Mostrar más juegos"}
                </button>
            )}
        </section>
    );
}
