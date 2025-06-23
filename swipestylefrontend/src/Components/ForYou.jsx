import { useContext } from 'react'
import UserContext from './utils/UserContext'
import useAuth from './hooks/useAuth';
const ForYou = () => {
    const {isLoggedIn} = useContext(UserContext);
    const { authLoading } = useAuth(); 

    if (authLoading) {
        return (
            <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-pink-50 to-purple-50">
            <p className="text-gray-600 text-lg">Loading account info...</p>
            </div>
        );
    }
    if (!isLoggedIn) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-pink-50 to-purple-50 flex items-center justify-center">
                <div className="text-center">
                    <p className="text-gray-600">Please Login First...</p>
                </div>
            </div>
        );
    }
    return (
        <div>
        
        </div>
    )
}

export default ForYou
