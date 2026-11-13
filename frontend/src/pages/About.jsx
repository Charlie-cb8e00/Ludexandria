import { useState } from "react";
import { Link } from "react-router-dom";
import TorchBackground from "../components/TorchBackground";
import "./About.css";

// TODO: sustituir los textos y cifras de ejemplo por los reales
const STEPS = [
    { title: "Cuéntanos", text: "Tus gustos, tu plataforma y cuánto tiempo tienes." },
    { title: "Buscamos", text: "Cruzamos género, estilo y valoraciones de jugadores." },
    { title: "Descubre", text: "Recibes una lista corta de juegos hechos a tu medida." },
];
const PLATFORMS = ["PlayStation", "Xbox", "Nintendo Switch", "PC", "Steam", "Epic Games"];
const STATS = [
    { value: "+500", label: "juegos analizados" },
    { value: "6", label: "plataformas" },
    { value: "100%", label: "recomendaciones sin publicidad" },
];
const THANKS = [
    "Gracias por tu sinceridad. Crea tu cuenta y ayúdanos a mejorar.",
    "Gracias. Crea tu cuenta y dinos qué podemos mejorar.",
    "Gracias. Crea tu cuenta y pruébanos con más calma.",
    "¡Genial! Crea tu cuenta para guardar tu valoración.",
    "¡Gracias! Crea tu cuenta y descubre tu próximo juego.",
];

export default function About() {
    const [rating, setRating] = useState(0);
    const [hover, setHover] = useState(0);
    const shown = hover || rating;

    return (
        <TorchBackground glow>
            <article className="about">
                <div className="about__doc">
                    <header className="about__hero">
                        <p className="about__eyebrow">Sobre nosotros</p>
                        <h1 className="about__title">Ludexandria</h1>
                        <p className="about__lead">La biblioteca donde cada jugador encuentra su próximo videojuego.</p>
                    </header>

                    <section className="about__section">
                        <h2 className="about__heading" data-icon="𓂀">Quiénes somos</h2>
                        <p className="about__text">
                            Somos una pequeña empresa que ama los videojuegos tanto como los buenos libros.
                            Ludexandria nace para que nadie se pierda entre miles de títulos: lo catalogamos,
                            lo probamos y lo recomendamos con criterio.
                        </p>
                        <p className="about__text">
                            Texto de ejemplo: aquí puedes contar vuestra historia, cuántas personas sois, desde
                            cuándo trabajáis y qué os diferencia de otros recomendadores.
                        </p>
                    </section>

                    <section className="about__section">
                        <h2 className="about__heading" data-icon="𓆣">Cómo recomendamos</h2>
                        <ol className="about__steps">
                            {STEPS.map((s) => (
                                <li className="about__step" key={s.title}>
                                    <b className="about__step-title">{s.title}</b>
                                    <span className="about__step-text">{s.text}</span>
                                </li>
                            ))}
                        </ol>
                    </section>

                    <section className="about__section">
                        <h2 className="about__heading" data-icon="𓊽">Todas las plataformas</h2>
                        <ul className="about__platforms">
                            {PLATFORMS.map((p) => (
                                <li className="about__platform" key={p}>{p}</li>
                            ))}
                        </ul>
                    </section>

                    <section className="about__stats">
                        {STATS.map((s) => (
                            <div className="about__stat" key={s.label}>
                                <strong className="about__stat-value">{s.value}</strong>
                                <span className="about__stat-label">{s.label}</span>
                            </div>
                        ))}
                    </section>

                    <section className="about__rate">
                        <h2 className="about__heading" data-icon="𓇳">¡Valóranos!</h2>
                        <p className="about__text">
                            Prueba Ludexandria gratis: crea tu cuenta, pídenos tus primeras recomendaciones y
                            cuéntanos qué te parecen. Tu opinión nos ayuda a mejorar.
                        </p>
                        <div className="about__stars" role="group" aria-label="Valoración" onMouseLeave={() => setHover(0)}>
                            {[1, 2, 3, 4, 5].map((n) => (
                                <button
                                    key={n}
                                    type="button"
                                    className="about__star"
                                    aria-label={`${n} ${n === 1 ? "estrella" : "estrellas"}`}
                                    onMouseEnter={() => setHover(n)}
                                    onFocus={() => setHover(n)}
                                    onBlur={() => setHover(0)}
                                    onClick={() => setRating(n)}
                                >
                                    {n <= shown ? "★" : "☆"}
                                </button>
                            ))}
                        </div>
                        <p className="about__thanks" role="status">
                            {rating ? THANKS[rating - 1] : "Toca una estrella para valorarnos."}
                        </p>
                        <Link className="about__btn" to="/register">Crear mi cuenta</Link>
                        <Link className="about__btn about__btn--ghost" to="/login">Ya tengo cuenta</Link>
                    </section>

                    <section className="about__cta">
                        <h2 className="about__heading" data-icon="𓅓">¿Hablamos?</h2>
                        <p className="about__text">Escríbenos si tienes dudas, ideas o quieres colaborar con nosotros.</p>
                        {/* TODO: poner el correo o la página de contacto reales */}
                        <a className="about__btn" href="#contacto">Contactar</a>
                    </section>
                </div>
            </article>
        </TorchBackground>
    );
}
