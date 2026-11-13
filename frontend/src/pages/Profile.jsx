import "./Profile.css";

function ProfileField({ label, value }) {
    return (
        <div className="profile-field">
            <dt>{label}</dt>
            <dd>{value}</dd>
        </div>
    );
}

function ProfileSection({ title, description, children }) {
    return (
        <section className="panel profile-section">
            <h2>{title}</h2>
            <p className="profile-section__description">{description}</p>
            <dl className="profile-fields">{children}</dl>
        </section>
    );
}

export default function Profile() {
    return (
        <div className="profile-page">
            <header className="page-header">
                <div>
                    <h1>Tu perfil</h1>
                    <p>La información de tu cuenta y tus preferencias de juego.</p>
                </div>
            </header>

            <div className="profile-grid">
                <ProfileSection
                    title="Información personal"
                    description="Los datos básicos para identificar tu cuenta."
                >
                    <ProfileField label="Nombre visible" value="Pendiente de configurar" />
                    <ProfileField label="Correo electrónico" value="Se mostrará aquí" />
                    <ProfileField label="Avatar" value="Opcional · aún sin configurar" />
                    <ProfileField label="Contraseña" value="Oculta por seguridad" />
                </ProfileSection>

                <ProfileSection
                    title="Preferencias de juego"
                    description="Ayudan a organizar y personalizar tus recomendaciones."
                >
                    <ProfileField label="Plataformas" value="Pendiente de configurar" />
                    <ProfileField label="Géneros favoritos" value="Pendiente de configurar" />
                    <ProfileField label="Géneros que quieres evitar" value="Opcional · sin configurar" />
                </ProfileSection>

                <ProfileSection
                    title="Preferencias del asistente"
                    description="Ajustes para adaptar las sugerencias a ti."
                >
                    <ProfileField label="Duración habitual de sesión" value="Pendiente de configurar" />
                    <ProfileField label="Idioma" value="Español" />
                    <ProfileField label="Zona horaria" value="Se detectará según tu dispositivo" />
                </ProfileSection>

                <ProfileSection
                    title="Resumen de biblioteca"
                    description="Estas cifras se calcularán a partir de tus juegos."
                >
                    <ProfileField label="Juegos guardados" value="—" />
                    <ProfileField label="Juegos completados" value="—" />
                    <ProfileField label="Horas jugadas" value="—" />
                </ProfileSection>
            </div>

            <p className="profile-page__notice">
                La edición y el guardado de estos datos estarán disponibles cuando se conecte el perfil con la cuenta.
            </p>
        </div>
    );
}
