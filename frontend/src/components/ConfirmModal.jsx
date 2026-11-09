export default function ConfirmModal({ open, title, onConfirm, onCancel }) {
    if (!open) return null;
    return (
        <div style={{ border: "1px solid #ccc", padding: 16, borderRadius: 8 }}>
            <p>{title || "¿Confirmar?"}</p>
            <button onClick={onConfirm}>Sí</button>
            <button onClick={onCancel}>No</button>
        </div>
    );
}