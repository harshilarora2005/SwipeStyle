import { useState } from 'react';

const useSwipeInteractions = (interactWithClothing, moveToNextCard) => {
    const [likedItems, setLikedItems] = useState([]);
    const [skippedItems, setSkippedItems] = useState([]);
    const [dragPosition, setDragPosition] = useState(0);

    const handleLike = async (item) => {
        if (item) {
        setLikedItems(prev => [...prev, item]);
        moveToNextCard();
        await interactWithClothing(item.productId, 'LIKED');
        }
    };

    const handleSkip = async (item) => {
        if (item) {
        setSkippedItems(prev => [...prev, item]);
        moveToNextCard();
        await interactWithClothing(item.productId, 'DISLIKED');
        }
    };

    const handleLikeByDrag = async (item, index, currentCardIndex) => {
        if (index >= currentCardIndex) {
        await handleLike(item);
        }
    };

    const handleSkipByDrag = async (item, index, currentCardIndex) => {
        if (index >= currentCardIndex) {
        await handleSkip(item);
        }
    };

    const isSkipActive = dragPosition < -50;
    const isLikeActive = dragPosition > 50;

    return {
        likedItems,
        skippedItems,
        dragPosition,
        setDragPosition,
        handleLike,
        handleSkip,
        handleLikeByDrag,
        handleSkipByDrag,
        isSkipActive,
        isLikeActive
    };
};

export default useSwipeInteractions;