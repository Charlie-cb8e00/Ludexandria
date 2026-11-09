import { useState } from "react";
import api from "../api/client";
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
        } catch (err) {
            setError(err.response?.data?.message || "Error al pedir recomendaciones");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>Asistente IA</h1>
            <form onSubmit={recomendar} style={{ display: "grid", gap: 8, maxWidth: 400 }}>
                <label>
                    Tiempo disponible (min)
                    <input type="number" value={tiempo} onChange={(e) => setTiempo(+e.target.value)} />
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
                    {loading ? "Pensando…" : "Recomiéndame"}
                </button>
            </form>

            {error && <p style={{ color: "crimson" }}>{error}</p>}

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