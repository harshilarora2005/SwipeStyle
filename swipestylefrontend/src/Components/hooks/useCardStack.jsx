import { useState, useEffect } from 'react';

const useCardStack = (clothingData, batchSize = 5) => {
    const [visibleCards, setVisibleCards] = useState([]);
    const [startIndex, setStartIndex] = useState(0);
    const [currentCardIndex, setCurrentCardIndex] = useState(0);

    useEffect(() => {
        if (visibleCards.length <= 2 && startIndex < clothingData.length) {
        const nextBatch = clothingData.slice(startIndex, startIndex + batchSize);
        setVisibleCards(prev => [...nextBatch, ...prev]);
        setStartIndex(prev => prev + batchSize);
        }
    }, [visibleCards.length, startIndex, clothingData, batchSize]);

    const moveToNextCard = () => {
        setCurrentCardIndex(prev => prev + 1);
    };

    const getCurrentCard = () => {
        return visibleCards[visibleCards.length - 1];
    };

    return {
        visibleCards,
        setVisibleCards,
        currentCardIndex,
        getCurrentCard,
        moveToNextCard
    };
};
export default useCardStack;