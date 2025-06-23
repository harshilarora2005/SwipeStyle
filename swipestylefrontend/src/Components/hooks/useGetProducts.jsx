import { useState, useCallback, useEffect } from 'react';
import { GetUserID } from '../../services/UserService';
import { getInteraction } from '../../services/UserClothingService';

const useGetProducts = (userEmail, interactionType) => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const getUserId = useCallback(async (email) => {
        try {
            const response = await GetUserID(email);
            return response.data;
        } catch (err) {
            console.error("Error getting user ID:", err);
            throw new Error("Failed to get user ID");
        }
    }, []);

    const getInteractedProducts = useCallback(async () => {
        setLoading(true);
        try {
            const userId = await getUserId(userEmail);
            const response = await getInteraction(userId, interactionType);
            setProducts(response.data);
        } catch (err) {
            console.error("Error fetching interacted products:", err);
            setError(err.message || "Something went wrong");
        } finally {
            setLoading(false);
        }
    }, [userEmail, interactionType, getUserId]);

    useEffect(() => {
        if (userEmail && interactionType) {
            getInteractedProducts();
        }
    }, [userEmail, interactionType, getInteractedProducts]);

    return { products, loading, error };
};

export default useGetProducts;
