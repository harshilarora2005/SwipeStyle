/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useContext, useEffect, useState, useCallback, useMemo, useRef } from "react";
import UserContext from "./utils/UserContext";
import useAuth from "./hooks/useAuth";
import useGetProducts from "./hooks/useGetProducts";
import Loading from "./Loading";
import { getRecommendations } from "../services/ClothingService";
import useCardStack from "./hooks/useCardStack";
import useCardAnimations from "./hooks/useCardAnimations";
import useSwipeInteractions from "./hooks/useSwipeInteractions";
import useClothingInteraction from "./hooks/useClothingInteraction";
import SwipeStack from "./SwipeStack";
import Error from "./Error";

const ForYou = () => {
    const { isLoggedIn, userEmail } = useContext(UserContext);
    const { authLoading } = useAuth();
    const { products, loading: productsLoading, error: productsError } = useGetProducts(userEmail, "LIKED");
    const { products: dislikedProducts, loading: dislikedLoading, error: dislikedError } = useGetProducts(userEmail, "DISLIKED");
    const [recommendations, setRecommendations] = useState([]);
    const [recoLoading, setRecoLoading] = useState(true);
    const [loadingMore, setLoadingMore] = useState(false);
    const [recommendationsError, setRecommendationsError] = useState(null);

    const [previouslyRecommended, setPreviouslyRecommended] = useState(new Set());
    
    const isInitialLoadRef = useRef(false);
    const isLoadingMoreRef = useRef(false);
    const hasErrorRef = useRef(false);

    const { interactWithClothing } = useClothingInteraction(userEmail);

    const {
        visibleCards,
        setVisibleCards,
        currentCardIndex,
        getCurrentCard,
        moveToNextCard,
    } = useCardStack(recommendations);

    const {
        likedItems,
        skippedItems,
        dragPosition,
        setDragPosition,
        handleLike,
        handleSkip,
        handleLikeByDrag,
        handleSkipByDrag,
        isSkipActive,
        isLikeActive,
    } = useSwipeInteractions(interactWithClothing, moveToNextCard);

    const {
        scrollRef,
        cardRefs,
        detailsY,
        detailsOpacity,
        detailsScale,
        animateCardSwipe,
    } = useCardAnimations();

    const memoizedProducts = useMemo(() => products, [products]);
    const dislikedProductIds = useMemo(() => {
        if (!dislikedProducts || dislikedProducts.length === 0) return new Set();
        return new Set(dislikedProducts.map(product => product.productId));
    }, [dislikedProducts]);

    const filterOutDislikedProducts = useCallback((recs) => {
        if (dislikedProductIds.size === 0) return recs;
        return recs.filter(rec => !dislikedProductIds.has(rec.productId));
    }, [dislikedProductIds]);
    const fetchRecommendations = useCallback(async (productsToUse, isLoadMore = false) => {
        if (!productsToUse || productsToUse.length === 0) return;
        
        try {
            const data = await getRecommendations(productsToUse, Array.from(previouslyRecommended));
            
            if (data.data && data.data.length > 0) {
                const filteredData = filterOutDislikedProducts(data.data);
                if (isLoadMore) {
                    setRecommendations(prev => {
                        const newRecs = filteredData.filter(newRec => 
                            !prev.some(existingRec => existingRec.productId === newRec.productId)
                        );
                        return [...prev, ...newRecs];
                    });
                } else {
                    setRecommendations(filteredData);
                }
                
                const newProductIds = data.data.map(item => item.productId);
                setPreviouslyRecommended(prev => new Set([...prev, ...newProductIds]));
                
                setRecommendationsError(null);
                hasErrorRef.current = false;
            } else {
                if (!isLoadMore) {
                    setRecommendations([]);
                }
                console.log("No more recommendations available");
            }
            
        } catch (err) {
            console.error("Error fetching recommendations:", err);
            setRecommendationsError(err);
            hasErrorRef.current = true;
        }
    }, [previouslyRecommended,filterOutDislikedProducts]);

    useEffect(() => {
        setPreviouslyRecommended(new Set());
        isInitialLoadRef.current = false;
        isLoadingMoreRef.current = false;
        hasErrorRef.current = false;
    }, [userEmail]);

    useEffect(() => {
        if (!memoizedProducts || memoizedProducts.length === 0) {
            setRecoLoading(false);
            isInitialLoadRef.current = true;
            return;
        }
        if (dislikedLoading) {
            return;
        }
        if (isInitialLoadRef.current) return;

        setRecoLoading(true);
        isInitialLoadRef.current = true;
        
        fetchRecommendations(memoizedProducts, false)
            .finally(() => {
                setRecoLoading(false);
            });
    }, [memoizedProducts, fetchRecommendations,dislikedLoading]); 

    useEffect(() => {
        const shouldLoadMore = 
            recommendations.length > 0 && 
            visibleCards.length <= 2
            !isLoadingMoreRef.current &&
            !hasErrorRef.current &&
            memoizedProducts?.length > 0;
            !dislikedLoading;
        if (shouldLoadMore) {
            isLoadingMoreRef.current = true;
            setLoadingMore(true);
            console.log("Loading more recommendations. Previously recommended:", previouslyRecommended.size);
            
            fetchRecommendations(memoizedProducts, true)
                .finally(() => {
                    setLoadingMore(false);
                    isLoadingMoreRef.current = false;
                });
        }
    }, [currentCardIndex, recommendations.length, memoizedProducts, fetchRecommendations]);
    const handleSkipButton = useCallback(async () => {
        const currentItem = getCurrentCard();
        if (currentItem && currentCardIndex < visibleCards.length) {
            await handleSkip(currentItem);
            animateCardSwipe(currentItem.productId, -200);
            setDragPosition(-100);
                        setPreviouslyRecommended(prev => new Set([...prev, currentItem.productId]));
        }
    }, [getCurrentCard, currentCardIndex, visibleCards.length, handleSkip, animateCardSwipe, setDragPosition]);

    const handleLikeButton = useCallback(async () => {
        const currentItem = getCurrentCard();
        if (currentItem && currentCardIndex < visibleCards.length) {
            await handleLike(currentItem);
            animateCardSwipe(currentItem.productId, 200);
            setDragPosition(100);
            setPreviouslyRecommended(prev => new Set([...prev, currentItem.productId]));
        }
    }, [getCurrentCard, currentCardIndex, visibleCards.length, handleLike, animateCardSwipe, setDragPosition]);
    const resetRecommendations = useCallback(() => {
        setPreviouslyRecommended(new Set());
        setRecommendations([]);
        isInitialLoadRef.current = false;
        if (memoizedProducts?.length > 0) {
            fetchRecommendations(memoizedProducts, false);
        }
    }, [memoizedProducts, fetchRecommendations]);
    if (authLoading) {
        return (
            <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-pink-50 to-purple-50">
                <p className="text-gray-600 text-lg">Loading account info...</p>
            </div>
        );
    }

    if (!isLoggedIn) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-pink-50 to-purple-50 flex items-center justify-center">
                <p className="text-gray-600">Please Login First...</p>
            </div>
        );
    }

    if (productsLoading || dislikedLoading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
                <Loading />
                <p className="mt-4 text-gray-600 text-lg">
                    Fetching your liked products... 💕
                </p>
            </div>
        );
    }

    if (productsError) {
        return <Error error={productsError} />;
    }
    if (dislikedError) {
        return <Error error={dislikedError} />;
    }
    if (recoLoading) {
        return (
            <div className="min-h-screen flex flex-col justify-center items-center bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
                <Loading />
                <p className="mt-4 text-gray-600 text-lg">
                    Loading recommendations just for you...
                </p>
            </div>
        );
    }

    if (recommendationsError) {
        return <Error error={recommendationsError} />;
    }

    if (!memoizedProducts || memoizedProducts.length === 0) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
                <div className="text-center space-y-3 max-w-sm">
                    <div className="w-16 h-16 mx-auto bg-gray-200 rounded-full flex items-center justify-center">
                        <svg
                            className="w-8 h-8 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={1.5}
                                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                            />
                        </svg>
                    </div>
                    <h2 className="text-lg font-medium text-gray-900">Nothing found</h2>
                    <p className="text-sm text-gray-500 leading-relaxed">
                        We couldn't find any items right now. Check back in a moment.
                    </p>
                </div>
            </div>
        );
    }
    if (recommendations.length === 0 && !recoLoading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
                <div className="text-center space-y-4 max-w-sm">
                    <div className="w-16 h-16 mx-auto bg-purple-100 rounded-full flex items-center justify-center">
                        <svg
                            className="w-8 h-8 text-purple-600"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={1.5}
                                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                        </svg>
                    </div>
                    <h2 className="text-lg font-medium text-gray-900">All caught up!</h2>
                    <p className="text-sm text-gray-500 leading-relaxed">
                        You've seen all available recommendations. Check back later for more!
                    </p>
                    <button 
                        onClick={resetRecommendations}
                        className="mt-4 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
                    >
                        Refresh Recommendations
                    </button>
                </div>
            </div>
        );
    }

    const currentItem = getCurrentCard();

    return (
        <>
            <SwipeStack
                scrollRef={scrollRef}
                visibleCards={visibleCards}
                setVisibleCards={setVisibleCards}
                currentCardIndex={currentCardIndex}
                currentItem={currentItem}
                cardRefs={cardRefs}
                isSkipActive={isSkipActive}
                isLikeActive={isLikeActive}
                handleSkipButton={handleSkipButton}
                handleLikeButton={handleLikeButton}
                handleSkipByDrag={handleSkipByDrag}
                handleLikeByDrag={handleLikeByDrag}
                setDragPosition={setDragPosition}
                detailsY={detailsY}
                detailsOpacity={detailsOpacity}
                detailsScale={detailsScale}
            />
            {loadingMore && (
                <div className="fixed bottom-4 left-1/2 transform -translate-x-1/2 bg-white px-4 py-2 rounded-full shadow text-gray-500 text-sm">
                    Loading more recommendations...
                </div>
            )}
        </>
    );
};

export default ForYou;