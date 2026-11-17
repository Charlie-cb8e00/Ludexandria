import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import api from "../api/client";
import ImportCsv from "./ImportCsv";

vi.mock("../api/client", () => ({
    default: { post: vi.fn() },
    getApiErrorMessage: (_error, fallback) => fallback,
}));

describe("ImportCsv", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("envía el CSV y muestra el resumen de importación", async () => {
        api.post.mockResolvedValue({
            data: { importados: 1, yaExistentes: 2, fallidos: 0, errores: [] },
        });
        const user = userEvent.setup();
        render(<MemoryRouter><ImportCsv /></MemoryRouter>);
        const file = new File(["nombre,plataforma\nHades,PC"], "biblioteca.csv", { type: "text/csv" });

        await user.upload(screen.getByLabelText("Archivo CSV"), file);
        fireEvent.submit(screen.getByRole("button", { name: "Importar CSV" }).closest("form"));

        await waitFor(() => expect(api.post).toHaveBeenCalledWith(
            "/importaciones",
            expect.any(FormData),
        ));
        const formData = api.post.mock.calls[0][1];
        expect(formData.get("file")).toBe(file);
        expect(await screen.findByRole("status")).toHaveTextContent(
            "Importados: 1 · Ya existentes: 2 · Fallidos: 0",
        );
    });

    it("muestra el error de API cuando falla la importación", async () => {
        api.post.mockRejectedValue(new Error("network"));
        const user = userEvent.setup();
        render(<MemoryRouter><ImportCsv /></MemoryRouter>);
        const file = new File(["Hades"], "biblioteca.csv", { type: "text/csv" });

        await user.upload(screen.getByLabelText("Archivo CSV"), file);
        fireEvent.submit(screen.getByRole("button", { name: "Importar CSV" }).closest("form"));

        expect(await screen.findByRole("alert")).toHaveTextContent("No se pudo importar el archivo.");
    });
});
