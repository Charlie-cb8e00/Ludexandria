import { render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it } from "vitest";
import Profile from "./Profile";

describe("Profile", () => {
    beforeEach(() => {
        localStorage.clear();
    });

    it("muestra la información básica guardada al autenticar", () => {
        localStorage.setItem("user", JSON.stringify({
            nombre: "Ana",
            email: "ana@example.com",
            rol: "ADMIN",
        }));

        render(<Profile />);

        expect(screen.getByText("Ana")).toBeInTheDocument();
        expect(screen.getByText("ana@example.com")).toBeInTheDocument();
        expect(screen.getByText("Administrador")).toBeInTheDocument();
    });

    it("muestra un aviso si no hay información de perfil válida", () => {
        localStorage.setItem("user", "{");

        render(<Profile />);

        expect(screen.getByRole("status")).toHaveTextContent(
            "No se encontraron datos de perfil.",
        );
    });
});
