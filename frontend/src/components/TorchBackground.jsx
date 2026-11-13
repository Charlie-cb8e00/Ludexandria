import { useEffect, useRef, useState } from "react";
import "./TorchBackground.css";

// Fondo de biblioteca con papiros, jeroglíficos y una antorcha que sigue al cursor.
// Uso:
//   <TorchBackground centered hint="...">contenido centrado</TorchBackground>
//   <TorchBackground glow>contenido que fluye (página larga)</TorchBackground>

const GLYPHS = Array.from("𓀀𓁐𓂀𓃭𓆣𓇳𓈖𓊽𓋹𓌳𓍯𓎛𓏏𓐍𓅓𓆓𓉐𓊪𓋴𓍿𓏌");
const ROW_HEIGHT = 58;

function glyphRow(r) {
    let s = "";
    for (let c = 0; c < 34; c++) s += GLYPHS[(r * 7 + c * 3 + r * c) % GLYPHS.length];
    return s;
}

export default function TorchBackground({ children, centered = false, glow = false, hint }) {
    const ref = useRef(null);
    const [rows, setRows] = useState(16);

    // Calcula cuántas filas de jeroglíficos hacen falta según la altura de la página
    useEffect(() => {
        const el = ref.current;
        if (!el) return;
        const update = () => setRows(Math.ceil(el.offsetHeight / ROW_HEIGHT) + 2);
        update();
        const observer = new ResizeObserver(update);
        observer.observe(el);
        return () => observer.disconnect();
    }, []);

    // Mueve la antorcha: solo cambia dos variables CSS, sin volver a renderizar
    function moveTorch(e) {
        const el = ref.current;
        if (!el) return;
        const r = el.getBoundingClientRect();
        el.style.setProperty("--mx", `${e.clientX - r.left}px`);
        el.style.setProperty("--my", `${e.clientY - r.top}px`);
    }

    return (
        <div
            ref={ref}
            className={`torch${centered ? " torch--centered" : ""}`}
            onPointerMove={moveTorch}
            onPointerDown={moveTorch}
        >
            <div className="torch__lib" aria-hidden="true">
                <div className="torch__glyphs">
                    {Array.from({ length: rows }, (_, r) => (
                        <span key={r}>{glyphRow(r)}</span>
                    ))}
                </div>
            </div>
            <div className="torch__dark" aria-hidden="true" />
            {hint && <p className="torch__hint">{hint}</p>}
            <div className="torch__content">{children}</div>
            {glow && <div className="torch__glow" aria-hidden="true" />}
        </div>
    );
}
