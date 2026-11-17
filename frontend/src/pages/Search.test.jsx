import { fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import api from "../api/client";
import Search from "./Search";

vi.mock("../api/client", () => ({
    default: { get: vi.fn() },
    getApiErrorMessage: (_error, fallback) => fallback,
}));

vi.mock("../components/GameCard", () => ({
    default: ({ juego }) => <article>{juego.nombre}</article>,
}));

describe("Search", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        api.get.mockResolvedValueOnce({ data: { steam: true, igdb: false } });
    });

    it("carga y concatena los resultados paginados", async () => {
        api.get
            .mockResolvedValueOnce({
                data: {
                    juegos: [{ id: 1, nombre: "Hades" }],
                    advertencias: [],
                    page: 0,
                    hasNext: true,
                },
            })
            .mockResolvedValueOnce({
                data: {
                    juegos: [{ id: 2, nombre: "Hades II" }],
                    advertencias: [],
                    page: 1,
                    hasNext: false,
                },
            });
        render(<MemoryRouter><Search /></MemoryRouter>);

        fireEvent.change(screen.getByLabelText("Nombre del juego"), { target: { value: "Hades" } });
        fireEvent.click(screen.getByRole("button", { name: "Buscar" }));

        expect(await screen.findByText("Hades")).toBeInTheDocument();
        fireEvent.click(await screen.findByRole("button", { name: "Mostrar más resultados" }));

        expect(await screen.findByText("Hades II")).toBeInTheDocument();
        expect(api.get).toHaveBeenLastCalledWith("/juegos/buscar", {
            params: { q: "Hades", page: 1, size: 20 },
        });
        expect(screen.queryByRole("button", { name: "Mostrar más resultados" })).not.toBeInTheDocument();
    });
});
