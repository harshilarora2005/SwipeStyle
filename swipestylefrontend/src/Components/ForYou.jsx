/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useContext,useEffect,useState } from 'react'
import UserContext from './utils/UserContext'
import useAuth from './hooks/useAuth';
import { useNavigate } from 'react-router';
import useGetProducts from './hooks/useGetProducts';
import Loading from './Loading';
import { getRecommendations } from '../services/ClothingService';
import useCardStack from './hooks/useCardStack';
import useCardAnimations from './hooks/useCardAnimations';
import useSwipeInteractions from './hooks/useSwipeInteractions';
import useClothingInteraction from './hooks/useClothingInteraction';
import Cards from './Cards';
import CardDetails from './CardDetails';
import { IoClose, IoHeart } from 'react-icons/io5';
import { motion } from 'framer-motion';
const ForYou = () => {
    const {isLoggedIn,userEmail} = useContext(UserContext);
    const { authLoading } = useAuth(); 
    const { products, loading, error } = useGetProducts(userEmail, "LIKED");
    const [recommendations, setRecommendations] = useState([]);
    const [recoLoading, setRecoLoading] = useState(true);
    const { interactWithClothing } = useClothingInteraction(userEmail);
    const { 
        visibleCards, 
        setVisibleCards, 
        currentCardIndex, 
        getCurrentCard, 
        moveToNextCard 
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
    useEffect(() => {
        if (products && products.length > 0) {
            setRecoLoading(true);
            getRecommendations(products)
                .then(data => {
                    setRecommendations(data.data);
                    console.log(recommendations);
                    setRecoLoading(false);
                })
                .catch(err => {
                    console.log("huh");
                    console.error(err);
                    setRecoLoading(false);
                });
        } else {
            setRecoLoading(false);
        }
    }, [products]);
    if (recoLoading) {
        return (
            <div className="min-h-screen flex flex-col justify-center items-center bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
                <Loading />
                <div className='block'>
                <p className="mt-4 text-gray-600 text-lg">Loading recommendations just for you...</p>
                </div>
            </div>
        );
    }

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
                <div className="text-center">
                    <p className="text-gray-600">Please Login First...</p>
                </div>
            </div>
        );
    }
    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
                <Loading />
                <p className="mt-4 text-gray-600 text-lg">Fetching your liked products... 💕</p>
            </div>
        );
    }
    

    const handleSkipButton = async () => {
        const currentItem = getCurrentCard();
        if (currentItem && currentCardIndex < visibleCards.length) {
        await handleSkip(currentItem);
        animateCardSwipe(currentItem.productId, -200);
        setDragPosition(-100);
        }
    };

    const handleLikeButton = async () => {
        const currentItem = getCurrentCard();
        if (currentItem && currentCardIndex < visibleCards.length) {
        await handleLike(currentItem);
        animateCardSwipe(currentItem.productId, 200);
        setDragPosition(100);
        }
    };

    if (products.length === 0) {
        return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
            <div className="text-center space-y-3 max-w-sm">
            <div className="w-16 h-16 mx-auto bg-gray-200 rounded-full flex items-center justify-center">
                <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
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

    const currentItem = getCurrentCard();

    return (
        <div ref={scrollRef} className='relative'>
        <div className='flex min-h-screen bg-gray-100 relative overflow-hidden'>
            <div className={`absolute left-0 top-0 w-1/3 h-full transition-all duration-300 ease-out ${
            isSkipActive 
                ? 'bg-gradient-to-r from-red-500/20 via-red-400/10 to-transparent shadow-2xl shadow-red-500/30' 
                : ''
            }`} 
            style={{
            borderRadius: isSkipActive ? '0 50% 50% 0' : '0',
            filter: isSkipActive ? 'blur(1px)' : 'none'
            }} />
        
            {/* Like Side Effect */}
            <div className={`absolute right-0 top-0 w-1/3 h-full transition-all duration-300 ease-out ${
            isLikeActive 
                ? 'bg-gradient-to-l from-green-500/20 via-green-400/10 to-transparent shadow-2xl shadow-green-500/30' 
                : ''
            }`}
            style={{
            borderRadius: isLikeActive ? '50% 0 0 50%' : '0',
            filter: isLikeActive ? 'blur(1px)' : 'none'
            }} />

            {/* Skip Button */}
            <div className="flex-1 flex items-center justify-center relative z-10">
            <div className="flex flex-col items-center space-y-4">
                <button
                onClick={handleSkipButton}
                className={`w-16 h-16 bg-red-500 hover:bg-red-600 rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:scale-105 active:scale-95 ${
                    isSkipActive ? 'scale-125 bg-red-600 shadow-red-300 shadow-2xl' : ''
                }`}
                disabled={currentCardIndex >= visibleCards.length}
                >
                <IoClose className={`text-white transition-all duration-300 ${
                    isSkipActive ? 'w-12 h-12' : 'w-8 h-8'
                }`} />
                </button>
                <span className="text-sm font-medium text-gray-600">Skip</span>
            </div>
            </div>
            <div className="grid place-items-center flex-1/2 relative z-20">
            {visibleCards.map((item, index) => (
                <Cards 
                key={item.productId || index} 
                clothing={item} 
                clothingData={visibleCards} 
                setClothingData={setVisibleCards} 
                index={index} 
                onDragPositionChange={setDragPosition}  
                onSwipeLeft={() => handleSkipByDrag(item, index, currentCardIndex)}
                onSwipeRight={() => handleLikeByDrag(item, index, currentCardIndex)}
                ref={(el) => cardRefs.current[item.productId] = el}
                />
            ))}
            </div>
            <div className="flex-1 flex items-center justify-center relative z-10">
            <div className="flex flex-col items-center space-y-4">
                <button
                onClick={handleLikeButton}
                className={`w-16 h-16 bg-green-500 hover:bg-green-600 rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:scale-105 active:scale-95 ${
                    isLikeActive ? 'scale-125 bg-green-600 shadow-green-300 shadow-2xl' : ''
                }`}
                disabled={currentCardIndex >= visibleCards.length}
                >
                <IoHeart className={`text-white transition-all duration-300 ${
                    isLikeActive ? 'w-12 h-12' : 'w-8 h-8'
                }`} />
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
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
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
}

export default ForYou
