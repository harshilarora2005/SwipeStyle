/* eslint-disable react-hooks/exhaustive-deps */
import { useState, useEffect, useContext } from "react";
import UserContext from "../utils/UserContext";
import { GetMyUser } from "../../services/UserService";

const useAuth = () => {
    const {
        setUserName,
        setUserEmail,
        setUserProfile,
        setUserGender,
        setIsLoggedIn,
        defaultProfileUrl,
    } = useContext(UserContext);

    const [authLoading, setAuthLoading] = useState(true);

    useEffect(() => {
        GetMyUser()
        .then((res) => {
            const user = res.data;
            console.log("User fetched from /me:", user);
            setUserName(user.name || user.username);
            setUserEmail(user.email);
            setUserGender(user?.gender);
            setUserProfile(user.profilePictureUrl || user.imageUrl || defaultProfileUrl);
            setIsLoggedIn(true);
        })
        .catch((err) => {
            console.error("User not logged in", err);
            setIsLoggedIn(false);
        })
        .finally(() => {
            setAuthLoading(false);
        });
    }, []);

    return { authLoading };
};

export default useAuth;
