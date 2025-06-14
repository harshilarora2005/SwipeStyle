/* eslint-disable no-unused-vars */

import {motion} from 'framer-motion';
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
  
  if (loading || scrapingInProgress) {
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
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h2 className="text-xl font-semibold mb-2">No Clothing Data Found</h2>
          <p className="text-gray-600">Please try again or check back later.</p>
        </div>
      </div>
    );
  }
  return (
    <div className="grid min-h-screen place-items-center">
      {clothingData.map((item,index)=>{
        return <Cards key={item.productId || index} clothingData={item}/>
      })}
    </div>
  );
}
export default Body;