
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard.jsx";
import Login from "./pages/Login.jsx";

import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <Routes>

                {/* Public Routes */}
                <Route path="/login" element={<Login />} />

                <Route
                    path="/register"
                    element={<div>Register Page - Coming Soon</div>}
                />

                {/* Protected Routes */}
                <Route element={<ProtectedRoute />}>
                    
                    <Route
                        path="/"
                        element={<Navigate to="/dashboard" replace />}
                    />

                    <Route path="/dashboard" element={<Dashboard />} />

                    <Route
                        path="/products"
                        element={<div>Products - Coming Soon</div>}
                    />

                    <Route
                        path="/categories"
                        element={<div>Categories - Coming Soon</div>}
                    />

                    <Route
                        path="/sellers"
                        element={<div>Sellers - Coming Soon</div>}
                    />

                    <Route
                        path="/transactions"
                        element={<div>Transactions - Coming Soon</div>}
                    />

                </Route>

                {/* Unknown Routes */}
                <Route
                    path="*"
                    element={<Navigate to="/" replace />}
                />

            </Routes>
        </BrowserRouter>
    );
}

export default App;
