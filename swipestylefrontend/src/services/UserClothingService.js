import axios from 'axios';
import { USER_CLOTHING_API_BASE_URL } from '../../public/constants';
const REST_API_BASE_URL = USER_CLOTHING_API_BASE_URL

export const saveClothingInteraction = (UserClothingDTO) => {
    return axios.post(REST_API_BASE_URL+ "/save-interaction", UserClothingDTO,{ withCredentials: true })
}

export const getInteraction = (userID, interaction) =>{
    return axios.get(REST_API_BASE_URL+"/get-interactions",{
        params:{
            userId:userID,
            interactionType: interaction
        },
        withCredentials:true
    })
}

