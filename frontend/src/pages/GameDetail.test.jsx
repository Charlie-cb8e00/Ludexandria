import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import api from "../api/client";
import GameDetail from "./GameDetail";

vi.mock("../api/client", () => ({
    default: { get: vi.fn(), post: vi.fn(), put: vi.fn() },
    getApiErrorMessage: (_error, fallback) => fallback,
}));

const game = { id: 7, nombre: "Hades", resumen: "Un juego de acción" };

function renderGameDetail() {
    return render(
        <MemoryRouter initialEntries={["/juegos/7"]}>
            <Routes>
                <Route path="/juegos/:id" element={<GameDetail />} />
            </Routes>
        </MemoryRouter>,
    );
}

describe("GameDetail", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.stubGlobal("fetch", vi.fn().mockResolvedValue({ status: 404 }));
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it("añade un juego a la biblioteca", async () => {
        api.get.mockImplementation((url) => Promise.resolve({
            data: url === "/juegos/7" ? game : null,
        }));
        api.post.mockResolvedValue({
            data: { id: 23, estado: "pendiente", notaPersonal: null, horasJugadas: 0 },
        });
        renderGameDetail();

        fireEvent.click(await screen.findByRole("button", { name: "Añadir a mi biblioteca" }));

        await waitFor(() => expect(api.post).toHaveBeenCalledWith("/biblioteca", {
            idJuego: 7,
            estado: "pendiente",
            notaPersonal: null,
            horasJugadas: 0,
        }));
        expect(await screen.findByRole("status")).toHaveTextContent("Añadido a tu biblioteca.");
    });

    it("guarda los cambios de estado y horas", async () => {
        api.get.mockImplementation((url) => Promise.resolve({
            data: url === "/juegos/7"
                ? game
                : { id: 23, estado: "pendiente", notaPersonal: null, horasJugadas: 0 },
        }));
        api.put.mockResolvedValue({
            data: { id: 23, estado: "completado", notaPersonal: null, horasJugadas: 3 },
        });
        renderGameDetail();

        fireEvent.change(await screen.findByLabelText("Estado"), { target: { value: "completado" } });
        fireEvent.change(screen.getByLabelText("Horas jugadas"), { target: { value: "3" } });
        fireEvent.click(screen.getByRole("button", { name: "Guardar cambios" }));

        await waitFor(() => expect(api.put).toHaveBeenCalledWith("/biblioteca/23", {
            idJuego: 7,
            estado: "completado",
            notaPersonal: null,
            horasJugadas: 3,
        }));
        expect(await screen.findByRole("status")).toHaveTextContent("Cambios guardados.");
    });
});
