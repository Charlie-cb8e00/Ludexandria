const ESTADOS = ["pendiente", "jugando", "completado", "abandonado"];

export default function StatusSelect({ value, onChange }) {
    return (
        <select value={value} onChange={(e) => onChange?.(e.target.value)}>
            {ESTADOS.map((e) => (
                <option key={e} value={e}>{e}</option>
            ))}
        </select>
    );
}