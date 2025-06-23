import axios from 'axios';
const REST_API_BASE_URL = "http://localhost:8080/api/user-clothing";

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

