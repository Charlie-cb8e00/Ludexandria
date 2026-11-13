import { useEffect, useState } from "react";
import lightThemeIcon from "../assets/theme-light.png";
import darkThemeIcon from "../assets/theme-dark.png";

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

    const themeIcon = theme === "light" ? lightThemeIcon : darkThemeIcon;

    return (
        <button
            type="button"
            className="theme-selector"
            onClick={handleThemeChange}
            aria-label={`Cambiar a modo ${theme === "light" ? "oscuro" : "claro"}`}
            aria-pressed={theme === "dark"}
            title={`Cambiar a modo ${theme === "light" ? "oscuro" : "claro"}`}
        >
            <span
                className="theme-selector__icon"
                aria-hidden="true"
                style={{ "--icon-mask": `url("${themeIcon}")` }}
            />
        </button>
    );
}