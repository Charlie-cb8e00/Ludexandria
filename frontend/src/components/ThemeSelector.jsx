import { useEffect, useState } from "react";

const THEME_STORAGE_KEY = "ludexandria-theme";

function getInitialTheme() {
    const savedTheme = localStorage.getItem(THEME_STORAGE_KEY);
    if (savedTheme === "light" || savedTheme === "dark") {
        return savedTheme;
    }

    return window.matchMedia("(prefers-color-scheme: light)").matches ? "light" : "dark";
}

export default function ThemeSelector() {
    const [theme, setTheme] = useState(getInitialTheme);

    useEffect(() => {
        document.documentElement.dataset.theme = theme;
    }, [theme]);

    function handleThemeChange() {
        const nextTheme = theme === "light" ? "dark" : "light";
        localStorage.setItem(THEME_STORAGE_KEY, nextTheme);
        setTheme(nextTheme);
    }

    return (
        <button
            type="button"
            className="theme-selector"
            onClick={handleThemeChange}
            aria-label={`Cambiar a modo ${theme === "light" ? "oscuro" : "claro"}`}
            aria-pressed={theme === "dark"}
            title={`Cambiar a modo ${theme === "light" ? "oscuro" : "claro"}`}
        >
            {theme === "dark" ? (
                <svg className="theme-selector__icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z" />
                </svg>
            ) : (
                <svg className="theme-selector__icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="12" cy="12" r="4" />
                    <path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
                </svg>
            )}
        </button>
    );
}