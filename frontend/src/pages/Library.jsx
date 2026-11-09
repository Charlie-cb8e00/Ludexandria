import { useEffect, useState } from "react";
import api from "../api/client";
import GameCard from "../components/GameCard";

const ESTADOS = ["pendiente", "jugando", "completado", "abandonado"];

export default function Library() {
    const [items, setItems] = useState([]);
    const [estado, setEstado] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        api
            .get("/biblioteca", { params: estado ? { estado } : {} })
            .then((res) => setItems(res.data))
            .catch(console.error)
            .finally(() => setLoading(false));
    }, [estado]);

    if (loading) return <p>Cargando biblioteca…</p>;

    return (
        <div>
            <h1>Mi biblioteca</h1>
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