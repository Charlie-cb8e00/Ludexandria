import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import api from "../api/client";
import Library from "./Library";

vi.mock("../api/client", () => ({
    default: { get: vi.fn(), post: vi.fn() },
    getApiErrorMessage: (_error, fallback) => fallback,
}));

vi.mock("../components/GameCard", () => ({
    default: ({ juego }) => <article>{juego.nombre}</article>,
}));

describe("Library", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("carga páginas del servidor y añade resultados al pulsar mostrar más", async () => {
        api.get
            .mockResolvedValueOnce({
                data: {
                    content: [{ id: 1, juego: { nombre: "Hades" } }],
                    page: 0,
                    hasNext: true,
                },
            })
            .mockRejectedValueOnce(new Error("network"))
            .mockResolvedValueOnce({
                data: {
                    content: [{ id: 2, juego: { nombre: "Celeste" } }],
                    page: 1,
                    hasNext: false,
                },
            });
        render(<MemoryRouter><Library /></MemoryRouter>);

        expect(await screen.findByText("Hades")).toBeInTheDocument();
        fireEvent.click(screen.getByRole("button", { name: "Mostrar más juegos" }));

        expect(await screen.findByRole("alert")).toHaveTextContent("No se pudieron cargar más juegos.");
        fireEvent.click(await screen.findByRole("button", { name: "Mostrar más juegos" }));
        expect(await screen.findByText("Celeste")).toBeInTheDocument();
        await waitFor(() => expect(api.get).toHaveBeenLastCalledWith("/biblioteca/pagina", {
            params: { page: 1, size: 12 },
        }));
        expect(screen.queryByRole("button", { name: "Mostrar más juegos" })).not.toBeInTheDocument();
    });
});
