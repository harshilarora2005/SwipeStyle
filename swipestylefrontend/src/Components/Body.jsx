/* eslint-disable no-unused-vars */
import React, { useContext, memo, useCallback } from 'react';
import UserContext from './utils/UserContext';
import Loading from './Loading';
import SwipeStack from './SwipeStack';
import useClothingInteraction from './hooks/useClothingInteraction';
import useAuth from './hooks/useAuth';
import useClothingData from './hooks/useClothingData';
import useCardStack from './hooks/useCardStack';
import useCardAnimations from './hooks/useCardAnimations';
import useSwipeInteractions from './hooks/useSwipeInteractions';

const NoItemsState = memo(() => (
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
));

NoItemsState.displayName = 'NoItemsState';

const Body = () => {
  const { userGender, userEmail } = useContext(UserContext);
  // const { authLoading } = useAuth();
  const { interactWithClothing } = useClothingInteraction(userEmail);
  const { clothingData, loading, scrapingInProgress } = useClothingData(userGender);

  const {
    visibleCards,
    setVisibleCards,
    currentCardIndex,
    getCurrentCard,
    moveToNextCard
  } = useCardStack(clothingData);

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
    isLikeActive
  } = useSwipeInteractions(interactWithClothing, moveToNextCard);

  const {
    scrollRef,
    cardRefs,
    detailsY,
    detailsOpacity,
    detailsScale,
    animateCardSwipe
  } = useCardAnimations();

  // Optimized button handlers with useCallback
  const handleSkipButton = useCallback(async () => {
    const currentItem = getCurrentCard();
    if (currentItem && currentCardIndex < visibleCards.length) {
      await handleSkip(currentItem);
      animateCardSwipe(currentItem.productId, -200);
      setDragPosition(-100);
    }
  }, [getCurrentCard, currentCardIndex, visibleCards.length, handleSkip, animateCardSwipe, setDragPosition]);

  const handleLikeButton = useCallback(async () => {
    const currentItem = getCurrentCard();
    if (currentItem && currentCardIndex < visibleCards.length) {
      await handleLike(currentItem);
      animateCardSwipe(currentItem.productId, 200);
      setDragPosition(100);
    }
  }, [getCurrentCard, currentCardIndex, visibleCards.length, handleLike, animateCardSwipe, setDragPosition]);
  // if (authLoading) {
  //   return (
  //     <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-pink-50 to-purple-50">
  //       <p className="text-gray-600 text-lg">Loading account info...</p>
  //     </div>
  //   );
  // }

  if (loading || scrapingInProgress || visibleCards.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <Loading />
        {scrapingInProgress && (
          <p className="mt-4 text-gray-600">Scraping in progress... Please wait.</p>
        )}
      </div>
    );
  }

  if (clothingData.length === 0) {
    return <NoItemsState />;
  }

  const currentItem = getCurrentCard();

  return (
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
  );
};

export default memo(Body);