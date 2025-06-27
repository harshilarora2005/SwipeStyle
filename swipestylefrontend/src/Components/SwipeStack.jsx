/* eslint-disable no-unused-vars */
import { motion } from "motion/react";
import { IoClose, IoHeart } from "react-icons/io5";
import Cards from "./Cards";
import CardDetails from "./CardDetails";
const SwipeStack = ({
    scrollRef,
    visibleCards,
    setVisibleCards,
    currentCardIndex,
    currentItem,
    cardRefs,
    isSkipActive,
    isLikeActive,
    handleSkipButton,
    handleLikeButton,
    handleSkipByDrag,
    handleLikeByDrag,
    setDragPosition,
    detailsY,
    detailsOpacity,
    detailsScale,
    }) => {
    return (
        <div ref={scrollRef} className="relative">
        <div className="flex min-h-screen bg-gray-100 relative overflow-hidden">
            <div
            className={`absolute left-0 top-0 w-1/3 h-full transition-all duration-300 ease-out ${
                isSkipActive
                ? "bg-gradient-to-r from-red-500/20 via-red-400/10 to-transparent shadow-2xl shadow-red-500/30"
                : ""
            }`}
            style={{
                borderRadius: isSkipActive ? "0 50% 50% 0" : "0",
                filter: isSkipActive ? "blur(1px)" : "none",
            }}
            />
            <div
            className={`absolute right-0 top-0 w-1/3 h-full transition-all duration-300 ease-out ${
                isLikeActive
                ? "bg-gradient-to-l from-green-500/20 via-green-400/10 to-transparent shadow-2xl shadow-green-500/30"
                : ""
            }`}
            style={{
                borderRadius: isLikeActive ? "50% 0 0 50%" : "0",
                filter: isLikeActive ? "blur(1px)" : "none",
            }}
            />
            <div className="flex-1 flex items-center justify-center relative z-10">
            <div className="flex flex-col items-center space-y-4">
                <button
                onClick={handleSkipButton}
                className={`w-16 h-16 bg-red-500 hover:bg-red-600 rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:scale-105 active:scale-95 ${
                    isSkipActive
                    ? "scale-125 bg-red-600 shadow-red-300 shadow-2xl"
                    : ""
                }`}
                disabled={currentCardIndex >= visibleCards.length}
                >
                <IoClose
                    className={`text-white transition-all duration-300 ${
                    isSkipActive ? "w-12 h-12" : "w-8 h-8"
                    }`}
                />
                </button>
                <span className="text-sm font-medium text-gray-600">Skip</span>
            </div>
            </div>

            {/* Cards */}
            <div className="grid place-items-center flex-1/2 relative z-20">
            {visibleCards.map((item, index) => (
                <Cards
                key={item.productId || index}
                clothing={item}
                clothingData={visibleCards}
                setClothingData={setVisibleCards}
                index={index}
                onDragPositionChange={setDragPosition}
                onSwipeLeft={() =>
                    handleSkipByDrag(item, index, currentCardIndex)
                }
                onSwipeRight={() =>
                    handleLikeByDrag(item, index, currentCardIndex)
                }
                ref={(el) => (cardRefs.current[item.productId] = el)}
                />
            ))}
            </div>

            <div className="flex-1 flex items-center justify-center relative z-10">
            <div className="flex flex-col items-center space-y-4">
                <button
                onClick={handleLikeButton}
                className={`w-16 h-16 bg-green-500 hover:bg-green-600 rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:scale-105 active:scale-95 ${
                    isLikeActive
                    ? "scale-125 bg-green-600 shadow-green-300 shadow-2xl"
                    : ""
                }`}
                disabled={currentCardIndex >= visibleCards.length}
                >
                <IoHeart
                    className={`text-white transition-all duration-300 ${
                    isLikeActive ? "w-12 h-12" : "w-8 h-8"
                    }`}
                />
                </button>
                <span className="text-sm font-medium text-gray-600">Like</span>
            </div>
            </div>
            <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 z-30">
            <motion.div
                animate={{ y: [0, 10, 0] }}
                transition={{ repeat: Infinity, duration: 2 }}
                className="flex flex-col items-center text-gray-500"
            >
                <span className="text-sm mb-2">Scroll for details</span>
                <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                >
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 14l-7 7m0 0l-7-7m7 7V3"
                />
                </svg>
            </motion.div>
            </div>
        </div>
        <motion.div
            style={{
            y: detailsY,
            opacity: detailsOpacity,
            scale: detailsScale,
            }}
            className="relative z-10"
        >
            <CardDetails clothing={currentItem} />
        </motion.div>
        </div>
    );
};

export default SwipeStack;
