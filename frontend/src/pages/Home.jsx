import { Link } from "react-router-dom";

export default function Home() {
    return (
        <div>
            <section className="portada">
                <div className="portada__contenido">
                    <h1>Ludexandria</h1>
                    <p className="portada__descripcion">
                        Tu biblioteca personal de videojuegos. Organiza el backlog, guarda estado,
                        nota y horas, e elige a qué jugar con ayuda de la IA.
                    </p>
                </div>
            </section>

            <section className="tiles">
                <Link to="/biblioteca" className="tile">
                    <strong>Mi biblioteca</strong>
                    <span>Pendientes, jugando, completados…</span>
                </Link>

                <Link to="/buscar" className="tile">
                    <strong>Buscar juegos</strong>
                    <span>Catálogo con datos de IGDB</span>
                </Link>

                <Link to="/asistente" className="tile">
                    <strong>Asistente IA</strong>
                    <span>¿A qué juego hoy según tiempo y ánimo?</span>
                </Link>

                <Link to="/importar" className="tile">
                    <strong>Importar CSV</strong>
                    <span>Carga tu biblioteca desde un archivo</span>
                </Link>
            </section>
        </div>
    );
}