/* eslint-disable no-unused-vars */

import { useContext, useEffect, useState } from 'react';
import { getClothing } from '../services/ClothingService';
import UserContext from './utils/UserContext';
import Loading from './Loading';
import Cards from './Cards';
const Body = () => {
  const [clothingData, setClothingData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [scrapingInProgress, setScrapingInProgress] = useState(false);
  const {userGender, setUserGender} = useContext(UserContext);
  const [visibleCards, setVisibleCards] = useState([]);
  const [startIndex, setStartIndex] = useState(0);
  const BATCH_SIZE = 5;
  useEffect(() => {
    let intervalId;
    
    const fetchClothing = async () => {
      try {
        const response = await getClothing(userGender); 
        
        if (response.status === 202) {
          console.log("Scraping in progress...");
          setScrapingInProgress(true);
          setLoading(true);

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
      setVisibleCards(prev => [...prev, ...nextBatch]);
      setStartIndex(prev => prev + BATCH_SIZE);
    }
  },[visibleCards.length,startIndex,clothingData]);
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
  
  if (clothingData.length === 0){
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h2 className="text-xl font-semibold mb-2">No Clothing Data Found</h2>
          <p className="text-gray-600">Please try again or check back later.</p>
        </div>
      </div>
    );
  }
  console.log(visibleCards);
  return (
    <div className="grid min-h-screen place-items-center">
      {visibleCards.map((item,index)=>{
        return <Cards key={item.productId || index} clothing={item} clothingData = {visibleCards} setClothingData={setVisibleCards}/>
      })}
    </div>
  );
}
export default Body;