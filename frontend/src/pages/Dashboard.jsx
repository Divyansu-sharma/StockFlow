
import { useEffect, useState } from "react";
import {
    getDashboardStatistics,
    getRecentTransactions,
    getInventoryMovementReport,
    getProductMovementReport
} from "../api/dashboardApi";

const Dashboard = () => {
    const [dashboard, setDashboard] = useState(null);
    const [recentTransactions, setRecentTransactions] = useState([]);
    const [inventoryMovement, setInventoryMovement] = useState(null);
    const [productMovement, setProductMovement] = useState(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchDashboard = async () => {
            try {
                // Fetch dashboard statistics
                const response = await getDashboardStatistics();

                console.log("Dashboard API response:", response);
                setDashboard(response.data);

                // Fetch recent transactions
                const transactionsResponse = await getRecentTransactions(5);

                console.log(
                    "Recent transactions response:",
                    transactionsResponse
                );

                setRecentTransactions(transactionsResponse.data);

                const movementResponse = await getInventoryMovementReport();

                console.log("Inventory movement response:", movementResponse);

                setInventoryMovement(movementResponse.data);

                const productMovementResponse = await getProductMovementReport(0, 10);

                console.log(
                    "Product movement response:",
                    productMovementResponse
                );

                setProductMovement(productMovementResponse.data);

            } catch (err) {
                console.error("Dashboard API error:", err);

                setError(
                    err.response?.data?.message ||
                    "Failed to load dashboard data"
                );
            } finally {
                setLoading(false);
            }
        };

        fetchDashboard();
    }, []);

    if (loading) {
        return <p>Loading dashboard...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div>
            <h1>StockFlow Dashboard</h1>

            <h2>Dashboard Statistics</h2>
            <pre>
                {JSON.stringify(dashboard, null, 2)}
            </pre>

            <h2>Recent Transactions</h2>
            <pre>
                {JSON.stringify(recentTransactions, null, 2)}
            </pre>
            <h2>Inventory Movement Report</h2>
            <pre>
                {JSON.stringify(inventoryMovement, null, 2)}
            </pre>
            <h2>Product Movement Report</h2>
            <pre>
                {JSON.stringify(productMovement, null, 2)}
            </pre>
        </div>
    );
};

export default Dashboard;
