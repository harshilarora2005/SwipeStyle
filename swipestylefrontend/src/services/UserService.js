import axios from 'axios';
const REST_API_BASE_URL = "http://localhost:8080/api/users";

export const RegisterUser= (UserDTO) => {
    return axios.post(REST_API_BASE_URL + "/register", UserDTO,{ withCredentials: true });
}
export const LoginUser = (LoginUserDTO) => {
    return axios.post(REST_API_BASE_URL + "/login", LoginUserDTO,{ withCredentials: true });
}
export const GetMyUser = () => {
    return axios.get(REST_API_BASE_URL + "/me", { withCredentials: true })
}
export const LogoutUser = () => {
    return axios.post(REST_API_BASE_URL + "/logout", { withCredentials: true })
}
export const UpdateGender = (UserDTO) => {
    return axios.put(REST_API_BASE_URL + "/updateGender", UserDTO,{ withCredentials: true })
}
export const GetUserID = (email) => {
    return axios.get (REST_API_BASE_URL + "/get-id", {
        params:{
            email:email
        }, 
        withCredentials: true });
}