import { useState, useEffect } from 'react';
import { getClothing } from '../../services/ClothingService';
import { shuffleArray } from '../utils/shuffleArray';

const useClothingData = (userGender) => {
    const [clothingData, setClothingData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [scrapingInProgress, setScrapingInProgress] = useState(false);
    const [initialScrape, setInitialScrape] = useState(false);

    useEffect(() => {
        let intervalId;
        
        const fetchClothing = async () => {
        try {
            const response = await getClothing(userGender); 
            
            if (response.status === 202) {
            console.log("Scraping in progress...");
            setScrapingInProgress(true);
            setLoading(true);
            setInitialScrape(true);
            } else if (response.status === 200) {
            console.log("Data received successfully");
            const shuffledResponse = shuffleArray(response.data);
            setClothingData(shuffledResponse || []);
            setLoading(false);
            setScrapingInProgress(false);
            clearInterval(intervalId);
            } else {
            console.log("Unexpected status:", response.status);
            setLoading(false);
            setScrapingInProgress(false);
            }
        } catch (error) {
            console.error("Failed to fetch clothing data:", error);
            setLoading(false);
            setScrapingInProgress(false);
            clearInterval(intervalId);
        }
        };
        
        fetchClothing();
        intervalId = setInterval(fetchClothing, 20000);

        return () => clearInterval(intervalId);
    }, [userGender]);
    useEffect(() => {
        const intervalId = setInterval(async () => {
        if (!scrapingInProgress) {
            try {
            const response = await getClothing(userGender);
            if (response.status === 200 && response.data) {
                const newData = response.data;
                const newItems = newData.filter(
                item => !clothingData.some(c => c.productId === item.productId)
                );

                if (newItems.length > 0) {
                console.log("New clothing items added:", newItems.length);
                setClothingData(prev => [...prev, ...newItems]);
                }
            }
            } catch (err) {
            console.error("Error checking for new clothing:", err);
            }
        }
        }, 300000);

        return () => clearInterval(intervalId);
    }, [scrapingInProgress, clothingData, userGender]);

    return {
        clothingData,
        loading,
        scrapingInProgress,
        initialScrape,
        setClothingData
    };
};

export default useClothingData;