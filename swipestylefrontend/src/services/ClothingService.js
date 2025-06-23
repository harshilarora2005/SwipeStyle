import axios from 'axios';
const REST_API_BASE_URL = "http://localhost:8080/api/swipe-style";
export const getClothing = (gender) => {
    return axios.get(REST_API_BASE_URL + "/products/"+ gender);
}

export const saveClothingInteraction = (UserClothingDTO) => {
    return axios.post(REST_API_BASE_URL+ "/save-interaction", UserClothingDTO,{ withCredentials: true })
}
export const getClothingID = (productID) => {
    return axios.get(REST_API_BASE_URL+"/get-clothing-id",{
        params:{
            productId:productID
        },
        withCredentials:true
    })
}   
