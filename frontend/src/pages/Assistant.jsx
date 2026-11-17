import { useState } from "react";
import api, { getApiErrorMessage } from "../api/client";
import GameCard from "../components/GameCard";

export default function Assistant() {
    const [tiempo, setTiempo] = useState(60);
    const [plataforma, setPlataforma] = useState("");
    const [animo, setAnimo] = useState("");
    const [resultados, setResultados] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function recomendar(e) {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const { data } = await api.post("/recomendaciones", { tiempo, plataforma, animo });
            setResultados(data);
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudieron cargar las recomendaciones."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>Asistente de recomendaciones</h1>
            <p>Elige entre los juegos pendientes de tu biblioteca según el tiempo disponible.</p>
            <form onSubmit={recomendar} style={{ display: "grid", gap: 8, maxWidth: 400 }}>
                <label>
                    Tiempo disponible (min)
                    <input type="number" min="1" value={tiempo} onChange={(e) => setTiempo(+e.target.value)} />
                </label>
                <label>
                    Plataforma
                    <input value={plataforma} onChange={(e) => setPlataforma(e.target.value)} placeholder="PC, PS5…" />
                </label>
                <label>
                    Ánimo
                    <input value={animo} onChange={(e) => setAnimo(e.target.value)} placeholder="relajado, intenso…" />
                </label>
                <button type="submit" disabled={loading}>
                    {loading ? "Buscando recomendaciones…" : "Recomiéndame"}
                </button>
            </form>

            {error && <p className="alert alert--error" role="alert">{error}</p>}

            <div style={{ display: "flex", flexWrap: "wrap", gap: 12, marginTop: 16 }}>
                {resultados.map((r) => (
                    <div key={r.juego.id}>
                        <GameCard juego={r.juego} />
                        <p style={{ fontSize: 13, maxWidth: 180 }}>{r.motivo}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}