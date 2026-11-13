import { useState } from "react";
import { Link } from "react-router-dom";
import api, { getApiErrorMessage } from "../api/client";

export default function ImportCsv() {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState("");

    async function importar(event) {
        event.preventDefault();
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);
        setLoading(true);
        setResult(null);
        setError("");

        try {
            const response = await api.post("/importaciones", formData);
            setResult(response.data);
        } catch (requestError) {
            setError(getApiErrorMessage(requestError, "No se pudo importar el archivo."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <section>
            <h1>Importar CSV</h1>
            <p>Sube un CSV con las columnas nombre, plataforma, estado, nota y horas.</p>
            <form onSubmit={importar}>
                <label htmlFor="csv-file">Archivo CSV</label>
                <input
                    id="csv-file"
                    type="file"
                    accept=".csv,text/csv"
                    required
                    onChange={(event) => {
                        setFile(event.target.files?.[0] || null);
                        setResult(null);
                        setError("");
                    }}
                />
                <button type="submit" disabled={!file || loading}>
                    {loading ? "Importando…" : "Importar CSV"}
                </button>
            </form>

            {error && <p role="alert">{error}</p>}
            {result && (
                <div role="status" aria-live="polite">
                    <p>
                        Importados: {result.importados} · Ya existentes: {result.yaExistentes} ·
                        Fallidos: {result.fallidos}
                    </p>
                    {result.errores?.length > 0 && (
                        <ul>
                            {result.errores.map((message, index) => <li key={index}>{message}</li>)}
                        </ul>
                    )}
                </div>
            )}
            <p><Link to="/biblioteca">Volver a mi biblioteca</Link></p>
        </section>
    );
}