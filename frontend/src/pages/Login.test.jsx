import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, useLocation } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import api from "../api/client";
import Login from "./Login";

vi.mock("../api/client", () => ({
    default: { post: vi.fn() },
    getApiErrorMessage: (error, fallback) => error.response?.data?.message || fallback,
}));

function CurrentPath() {
    const location = useLocation();
    return <output data-testid="current-path">{location.pathname}</output>;
}

function renderLogin() {
    return render(
        <MemoryRouter initialEntries={["/login"]}>
            <Login />
            <CurrentPath />
        </MemoryRouter>,
    );
}

describe("Login", () => {
    beforeEach(() => {
        localStorage.clear();
        vi.clearAllMocks();
    });

    it("autentica contra la API y guarda el JWT antes de entrar", async () => {
        api.post.mockResolvedValue({
            data: {
                token: "jwt-de-prueba",
                email: "ana@example.com",
                nombre: "Ana",
                rol: "USER",
            },
        });
        renderLogin();

        fireEvent.change(screen.getByLabelText("Email"), { target: { value: "ana@example.com" } });
        fireEvent.change(screen.getByLabelText("Contraseña"), { target: { value: "clave-segura" } });
        fireEvent.click(screen.getByRole("button", { name: "Iniciar sesión" }));

        await waitFor(() => expect(api.post).toHaveBeenCalledWith("/auth/login", {
            email: "ana@example.com",
            password: "clave-segura",
        }));
        await waitFor(() => expect(screen.getByTestId("current-path")).toHaveTextContent("/"));
        expect(localStorage.getItem("token")).toBe("jwt-de-prueba");
        expect(JSON.parse(localStorage.getItem("user"))).toEqual({
            email: "ana@example.com",
            nombre: "Ana",
            rol: "USER",
        });
    });

    it("muestra los errores de autenticación sin guardar un token", async () => {
        api.post.mockRejectedValue({
            response: { data: { message: "Credenciales incorrectas" } },
        });
        renderLogin();

        fireEvent.change(screen.getByLabelText("Email"), { target: { value: "ana@example.com" } });
        fireEvent.change(screen.getByLabelText("Contraseña"), { target: { value: "incorrecta" } });
        fireEvent.click(screen.getByRole("button", { name: "Iniciar sesión" }));

        expect(await screen.findByRole("alert")).toHaveTextContent("Credenciales incorrectas");
        expect(localStorage.getItem("token")).toBeNull();
    });

    it("rechaza una respuesta de login sin token", async () => {
        api.post.mockResolvedValue({ data: {} });
        renderLogin();

        fireEvent.change(screen.getByLabelText("Email"), { target: { value: "ana@example.com" } });
        fireEvent.change(screen.getByLabelText("Contraseña"), { target: { value: "clave-segura" } });
        fireEvent.click(screen.getByRole("button", { name: "Iniciar sesión" }));

        expect(await screen.findByRole("alert")).toHaveTextContent("El servidor no devolvió una sesión válida.");
        expect(localStorage.getItem("token")).toBeNull();
        expect(screen.getByTestId("current-path")).toHaveTextContent("/login");
    });
});
