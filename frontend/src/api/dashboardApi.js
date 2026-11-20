
import api from "./axiosConfig";

export const getDashboardStatistics = async () => {
    const response = await api.get("/dashboard/statistics");
    return response.data;
};

export const getRecentTransactions = async (limit = 5) => {
    const response = await api.get(
        `/dashboard/recent-transactions?limit=${limit}`
    );
    return response.data;
};

export const getInventoryMovementReport = async (startDate, endDate) => {
    const response = await api.get("/dashboard/inventory-movement", {
        params: {
            startDate,
            endDate
        }
    });

    return response.data;
};


export const getProductMovementReport = async (
    page = 0,
    size = 10,
    startDate,
    endDate
) => {
    const response = await api.get("/dashboard/product-movement", {
        params: {
            page,
            size,
            startDate,
            endDate
        }
    });

    return response.data;
};

