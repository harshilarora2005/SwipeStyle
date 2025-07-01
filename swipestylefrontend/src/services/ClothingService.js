import axios from 'axios';
import { SWIPE_STYLE_API_BASE_URL } from '../../public/constants';
const REST_API_BASE_URL = SWIPE_STYLE_API_BASE_URL;
export const getClothing = (gender) => {
    return axios.get(REST_API_BASE_URL + "/products/"+ gender);
}
export const getClothingID = (productID) => {
    return axios.get(REST_API_BASE_URL+"/get-clothing-id",{
        params:{
            productId:productID
        },
        withCredentials:true
    })
}   
export const getRecommendations = async (likedItems, previouslyRecommended = []) => {
    const requestBody = {
        likedItems: likedItems,
        previouslyRecommended: previouslyRecommended
    };
    
    return axios.post(REST_API_BASE_URL + "/recommend", requestBody, {
        withCredentials: true
    });
}