import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getApiErrorMessage } from "../api/client";
import GameCard from "../components/GameCard";

const ESTADOS = ["pendiente", "jugando", "completado", "abandonado"];

export default function Library() {
    const [items, setItems] = useState([]);
    const [estado, setEstado] = useState("");
    const [loadedEstado, setLoadedEstado] = useState(null);
    const [addingPopular, setAddingPopular] = useState(false);
    const [notice, setNotice] = useState("");
    const [error, setError] = useState("");
    const loading = loadedEstado !== estado;

    useEffect(() => {
        api
            .get("/biblioteca", { params: estado ? { estado } : {} })
            .then((res) => setItems(res.data))
            .catch(console.error)
            .finally(() => setLoadedEstado(estado));
    }, [estado]);

    async function anadirPopulares() {
        setAddingPopular(true);
        setNotice("");
        setError("");
        try {
            const response = await api.post("/biblioteca/populares");
            const { anadidos, yaExistentes } = response.data;
            setNotice(`Añadidos: ${anadidos}. Ya estaban en tu biblioteca: ${yaExistentes}.`);
            try {
                const result = await api.get("/biblioteca", { params: estado ? { estado } : {} });
                setItems(result.data);
            } catch {
                setError("Los juegos se añadieron, pero no se pudo actualizar la biblioteca.");
            }
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudieron añadir los juegos populares."));
        } finally {
            setAddingPopular(false);
        }
    }

    if (loading) return <p>Cargando biblioteca…</p>;

    return (
        <div>
            <h1>Mi biblioteca</h1>
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                <Link className="btn btn--ghost" to="/importar">Importar CSV</Link>
                <button type="button" onClick={anadirPopulares} disabled={addingPopular}>
                    {addingPopular ? "Añadiendo…" : "Añadir juegos populares"}
                </button>
            </div>
            {notice && <p role="status">{notice}</p>}
            {error && <p role="alert">{error}</p>}
            <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                <option value="">Todos</option>
                {ESTADOS.map((e) => (
                    <option key={e} value={e}>{e}</option>
                ))}
            </select>

            <div style={{ display: "flex", flexWrap: "wrap", gap: 12, marginTop: 16 }}>
                {items.map((item) => (
                    <div key={item.id}>
                        <GameCard juego={item.juego} />
                        <p style={{ fontSize: 13 }}>
                            {item.estado} · {item.horasJugadas ?? 0} h · nota {item.notaPersonal ?? "—"}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
}