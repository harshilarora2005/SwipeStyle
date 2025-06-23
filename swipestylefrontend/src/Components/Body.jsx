/* eslint-disable no-unused-vars */
import { useContext, useEffect, useState, useRef} from 'react';
import { getClothing } from '../services/ClothingService';
import UserContext from './utils/UserContext';
import Loading from './Loading';
import Cards from './Cards';
import CardDetails from './CardDetails';
import { IoClose, IoHeart } from 'react-icons/io5';
import { motion,useScroll, useTransform } from 'framer-motion';
import useClothingInteraction from "./hooks/useClothingInteraction";
import useAuth from './hooks/useAuth';
const Body = () => {
  const [clothingData, setClothingData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [scrapingInProgress, setScrapingInProgress] = useState(false);
  const {userGender, userEmail} = useContext(UserContext);
  const [visibleCards, setVisibleCards] = useState([]);
  const [startIndex, setStartIndex] = useState(0);
  const [initalScrape, setInitialScrape] = useState(false);
  const [currentCardIndex, setCurrentCardIndex] = useState(0);
  const [likedItems, setLikedItems] = useState([]);
  const [skippedItems, setSkippedItems] = useState([]);
  const [dragPosition, setDragPosition] = useState(0);
  const { interactWithClothing } = useClothingInteraction(userEmail);
  const BATCH_SIZE = 5;
  const scrollRef = useRef(null);
  const { scrollYProgress } = useScroll({ target: scrollRef });

  const detailsY = useTransform(scrollYProgress, [0, 1], [50, 0]);
  const detailsOpacity = useTransform(scrollYProgress, [0, 0.5, 1], [0, 1, 1]);
  const detailsScale = useTransform(scrollYProgress, [0, 0.5, 1], [0.8, 0.9, 1]);
  const cardRefs = useRef({});
  
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
          setClothingData(response.data || []);
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
  
  useEffect(()=>{
    if (visibleCards.length <= 2 && startIndex < clothingData.length) {
      const nextBatch = clothingData.slice(startIndex, startIndex + BATCH_SIZE);
      setVisibleCards(prev => [...nextBatch,...prev]);
      setStartIndex(prev => prev + BATCH_SIZE);
    }
  },[visibleCards.length,startIndex,clothingData]);

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
              setClothingData(prev => [...prev,...newItems]);
            }
          }
        } catch (err) {
          console.error("Error checking for new clothing:", err);
        }
      }
    }, 300000); 

    return () => clearInterval(intervalId);
  }, [scrapingInProgress, clothingData, userGender]);
  const { authLoading } = useAuth(); 

    if (authLoading) {
        return (
            <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-pink-50 to-purple-50">
            <p className="text-gray-600 text-lg">Loading account info...</p>
            </div>
        );
    }
  const handleSkip = async() => {
    if (currentCardIndex < visibleCards.length) {
      const currentItem = visibleCards[currentCardIndex];
      setSkippedItems(prev => [...prev, currentItem]);
      setCurrentCardIndex(prev => prev + 1);
      await interactWithClothing(currentItem.productId,'DISLIKED');
    }
    const frontCard = visibleCards[visibleCards.length - 1];
    if (frontCard && cardRefs.current[frontCard.productId]) {
      cardRefs.current[frontCard.productId].animateSwipe(-200); 
      setDragPosition(-100);
    }
  };

  const handleLike = async() => {
    if (currentCardIndex < visibleCards.length) {
      const currentItem = visibleCards[currentCardIndex];
      setLikedItems(prev => [...prev, currentItem]);
      setCurrentCardIndex(prev => prev + 1);
      await interactWithClothing(currentItem.productId, 'LIKED');
    }
    const frontCard = visibleCards[visibleCards.length - 1];
    if (frontCard && cardRefs.current[frontCard.productId]) {
      cardRefs.current[frontCard.productId].animateSwipe(200); 
      setDragPosition(100);
    }
  };

  if (loading || scrapingInProgress || visibleCards.length == 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <Loading/>
        {scrapingInProgress && (
          <p className="mt-4 text-gray-600">Scraping in progress... Please wait.</p>
        )}
      </div>
    );
  }
  
  if (clothingData.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
        <div className="text-center space-y-3 max-w-sm">
          <div className="w-16 h-16 mx-auto bg-gray-200 rounded-full flex items-center justify-center">
            <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <h2 className="text-lg font-medium text-gray-900">
            Nothing found
          </h2>
          <p className="text-sm text-gray-500 leading-relaxed">
            We couldn't find any items right now. Check back in a moment.
          </p>
        </div>
      </div>
    );
  }
  const currentItem = visibleCards[visibleCards.length - 1];
  const isSkipActive = dragPosition < -50;
  const isLikeActive = dragPosition > 50;
  const handleSkipByDrag = async (item, index) => {
    if (index >= currentCardIndex) {
      setSkippedItems(prev => [...prev, item]);
      setCurrentCardIndex(prev => prev + 1);
      await interactWithClothing(item.productId, 'DISLIKED');
    }
  };

  const handleLikeByDrag = async (item, index) => {
    if (index >= currentCardIndex) {
      setLikedItems(prev => [...prev, item]);
      setCurrentCardIndex(prev => prev + 1);
      await interactWithClothing(item.productId, 'LIKED');
    }
  };

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
      
        <div className={`absolute right-0 top-0 w-1/3 h-full transition-all duration-300 ease-out ${
          isLikeActive 
            ? 'bg-gradient-to-l from-green-500/20 via-green-400/10 to-transparent shadow-2xl shadow-green-500/30' 
            : ''
        }`}
        style={{
          borderRadius: isLikeActive ? '50% 0 0 50%' : '0',
          filter: isLikeActive ? 'blur(1px)' : 'none'
        }} />

        <div className="flex-1 flex items-center justify-center relative z-10">
          <div className="flex flex-col items-center space-y-4">
            <button
              onClick={handleSkip}
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
          {visibleCards.map((item,index)=>{
            return <Cards key={item.productId || index} clothing={item} clothingData = {visibleCards} setClothingData={setVisibleCards} index={index} onDragPositionChange={setDragPosition}  onSwipeLeft={() => handleSkipByDrag(item, index)}
            onSwipeRight={() => handleLikeByDrag(item, index)}
            ref={(el) => cardRefs.current[item.productId] = el}
            />
          })}
        </div>

        <div className="flex-1 flex items-center justify-center relative z-10">
          <div className="flex flex-col items-center space-y-4">
            <button
              onClick={handleLike}
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

export default Body;