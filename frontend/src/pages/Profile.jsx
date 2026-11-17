function cargarPerfil() {
    const storedProfile = localStorage.getItem("user");
    if (!storedProfile) return null;

    try {
        const profile = JSON.parse(storedProfile);
        if (typeof profile.email !== "string" || typeof profile.nombre !== "string") return null;
        return profile;
    } catch {
        return null;
    }
}

export default function Profile() {
    const profile = cargarPerfil();

    return (
        <section aria-labelledby="profile-title">
            <h1 id="profile-title">Perfil</h1>
            {profile ? (
                <dl>
                    <div>
                        <dt>Nombre</dt>
                        <dd>{profile.nombre}</dd>
                    </div>
                    <div>
                        <dt>Email</dt>
                        <dd>{profile.email}</dd>
                    </div>
                    <div>
                        <dt>Tipo de cuenta</dt>
                        <dd>{profile.rol === "ADMIN" ? "Administrador" : "Usuario"}</dd>
                    </div>
                </dl>
            ) : (
                <p role="status">No se encontraron datos de perfil. Cierra sesión e inicia sesión de nuevo.</p>
            )}
        </section>
    );
}