import { createContext} from "react";

const UserContext = createContext({
    userName: "Guest",
    isLoggedIn: false,
    userEmail: null,
    userImage: null,
    userProfile: "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg",
    userGender: "UNISEX",
});
export default UserContext;