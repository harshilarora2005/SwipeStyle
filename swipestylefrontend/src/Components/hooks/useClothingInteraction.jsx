import { saveClothingInteraction, getClothingID } from "../../services/ClothingService";
import { GetUserID } from "../../services/UserService";
import { useState, useCallback } from "react";

export const InteractionType = {
    LIKED: 'LIKED',
    DISLIKED: 'DISLIKED'
};

const useClothingInteraction = (userEmail) => {
    const [loading, setLoading] = useState(false);
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

    const getClothingId = useCallback(async (productId) => {
        try {
            const response = await getClothingID(productId);
            return response.data;
        } catch (err) {
            console.error("Error getting clothing ID:", err);
            throw new Error("Failed to get clothing ID");
        }
    }, []);

    const interactWithClothing = useCallback(async (productId, interactionType1) => {
        setLoading(true);
        setError(null);
        try {
            console.log("Getting for email :", userEmail);
            const [userId, clothingId] = await Promise.all([
                getUserId(userEmail),
                getClothingId(productId)
            ]);
            const payload = {
                userId: userId,
                clothingId: clothingId,
                interactionType: interactionType1
            };
            console.log(payload);
            await saveClothingInteraction(payload);
            console.log("✅ Interaction saved (201 Created):", payload);
        } catch (err) {
            console.error("Error saving interaction:", err);
            setError(err.message || "Interaction failed");
        } finally {
            setLoading(false);
        }
    }, [getUserId, getClothingId, userEmail]);

    return {
        interactWithClothing,
        loading,
        error
    };
};

export default useClothingInteraction;
