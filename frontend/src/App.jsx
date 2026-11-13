import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Home from "./pages/Home";
import Search from "./pages/Search";
import GameDetail from "./pages/GameDetail";
import Library from "./pages/Library";
import Assistant from "./pages/Assistant";
import ImportCsv from "./pages/ImportCsv";
import Profile from "./pages/Profile";
import Admin from "./pages/Admin";
import About from "./pages/About";

function PrivateRoute({ children }) {
    const token = localStorage.getItem("token");
    return token ? children : <Navigate to="/login" />;
}

// Páginas que ocupan toda la pantalla (sin barra de navegación ni contenedor)
const FULLSCREEN_ROUTES = ["/login", "/sobre-nosotros"];

function Layout() {
    const { pathname } = useLocation();
    const fullscreen = FULLSCREEN_ROUTES.includes(pathname);

    const routes = (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/sobre-nosotros" element={<About />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={<PrivateRoute><Home /></PrivateRoute>} />
            <Route path="/buscar" element={<PrivateRoute><Search /></PrivateRoute>} />
            <Route path="/juegos/:id" element={<PrivateRoute><GameDetail /></PrivateRoute>} />
            <Route path="/biblioteca" element={<PrivateRoute><Library /></PrivateRoute>} />
            <Route path="/asistente" element={<PrivateRoute><Assistant /></PrivateRoute>} />
            <Route path="/importar" element={<PrivateRoute><ImportCsv /></PrivateRoute>} />
            <Route path="/perfil" element={<PrivateRoute><Profile /></PrivateRoute>} />
            <Route path="/admin" element={<PrivateRoute><Admin /></PrivateRoute>} />
        </Routes>
    );

    if (fullscreen) return routes;

    return (
        <>
            <Navbar />
            <main style={{ padding: "1rem", maxWidth: 1100, margin: "0 auto" }}>
                {routes}
            </main>
        </>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <Layout />
        </BrowserRouter>
    );
}