import { useEffect, useContext } from "react";
import UserContext from "./utils/UserContext";
import { GetMyUser } from "../services/UserService";

const Account = () => {
    const { setUserName, setIsLoggedIn, setUserEmail, setUserProfile, defaultProfileUrl} = useContext(UserContext);

    useEffect(() => {
        GetMyUser()
        .then(res => {
            const user = res.data.attributes;
            console.log(user);
            setUserName(user.name || user.username);
            setUserEmail(user.email);
            setUserProfile(user.picture || user.imageUrl ||defaultProfileUrl);
            setIsLoggedIn(true);
        })
        .catch(err => {
            console.error("User not logged in", err);
        });
    });

    return (
        <div>
        {/* show profile */}
        </div>
    );
};
export default Account;