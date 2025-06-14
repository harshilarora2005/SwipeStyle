import axios from 'axios';
const REST_API_BASE_URL = "http://localhost:8080/api/users";

export const RegisterUser= (UserDTO) => {
    return axios.post(REST_API_BASE_URL + "/register", UserDTO);
}
export const LoginUser = (LoginUserDTO) => {
    return axios.post(REST_API_BASE_URL + "/login", LoginUserDTO);
}
export const GetMyUser = () => {
    return axios.get(REST_API_BASE_URL + "/me", { withCredentials: true })
}
