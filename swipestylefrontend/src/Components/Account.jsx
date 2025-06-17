import {useState, useEffect, useContext } from "react";
import { User, Mail, Heart, Star, Grid3X3, Compass,Pencil,X } from "lucide-react";
import UserContext from "./utils/UserContext";
import { GetMyUser } from "../services/UserService";
import { UpdateGender } from "../services/UserService";
const Account = () => {
    const { 
        setUserName, 
        setIsLoggedIn, 
        setUserEmail, 
        setUserProfile, 
        setUserGender,
        defaultProfileUrl,
        userName, 
        isLoggedIn, 
        userEmail, 
        userProfile,
        userGender
    } = useContext(UserContext);
    const [isGenderDialogOpen, setIsGenderDialogOpen] = useState(false);
    const [selectedGender, setSelectedGender] = useState(userGender);
    const handleGenderChange = async() => {
        if(selectedGender != userGender){
            setUserGender(selectedGender);
            setIsGenderDialogOpen(false);
            const payload = {
                username : userName,
                gender : selectedGender

            }
            console.log(payload);
            await UpdateGender(payload);
        }
    };

    const openGenderDialog = () => {
        setSelectedGender(userGender);
        setIsGenderDialogOpen(true);
    };
    const genderOptions = ['Male', 'Female', 'Unisex'];
    useEffect(() => {
        GetMyUser()
            .then(res => {
                const user = res.data
                console.log(user);
                setUserName(user.name || user.username);
                setUserEmail(user.email);
                setUserGender(user?.gender);
                setUserProfile(user.profilePictureUrl || user.imageUrl || defaultProfileUrl);
                setIsLoggedIn(true);
            })
            .catch(err => {
                console.error("User not logged in", err);
            });
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

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
        <div className="min-h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
            <div className="container mx-auto px-4 py-8">
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold bg-gradient-to-r from-pink-600 to-purple-600 bg-clip-text text-transparent mb-2">
                        My Account
                    </h1>
                    <p className="text-gray-600">Your personal space</p>
                </div>
                <div className="max-w-4xl mx-auto">
                    <div className="bg-white rounded-3xl shadow-xl overflow-hidden border border-pink-100">
                        <div className="h-32 bg-gradient-to-r from-pink-400 via-purple-400 to-blue-400 relative">
                            <div className="absolute inset-0 bg-white/10 backdrop-blur-sm"></div>
                        </div>
                        <div className="px-8 pb-8 -mt-16 relative">
                            <div className="flex justify-center mb-6">
                                <img
                                    src={userProfile}
                                    alt="Profile"
                                    className="w-32 h-32 rounded-full border-4 border-white shadow-xl object-cover"
                                />
                            </div>
                            <div className="text-center mb-8">
                                <h2 className="text-3xl font-bold text-gray-800 mb-4">{userName}</h2>
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                                <a
                                    href="/collections"
                                    className="bg-gradient-to-br from-pink-50 to-pink-100 hover:from-pink-100 hover:to-pink-200 rounded-2xl p-6 text-center border border-pink-200 transition-all duration-200 shadow-lg hover:shadow-xl group"
                                >
                                    <div className="bg-pink-500 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform duration-200">
                                        <Grid3X3 className="w-6 h-6 text-white" />
                                    </div>
                                    <h3 className="text-xl font-bold text-pink-600 mb-1">Collections</h3>
                                    <p className="text-pink-700 text-sm">View your saved items</p>
                                </a>
                                
                                <a
                                    href="/explore"
                                    className="bg-gradient-to-br from-purple-50 to-purple-100 hover:from-purple-100 hover:to-purple-200 rounded-2xl p-6 text-center border border-purple-200 transition-all duration-200 shadow-lg hover:shadow-xl group"
                                >
                                    <div className="bg-purple-500 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform duration-200">
                                        <Compass className="w-6 h-6 text-white" />
                                    </div>
                                    <h3 className="text-xl font-bold text-purple-600 mb-1">Explore</h3>
                                    <p className="text-purple-700 text-sm">Discover new items</p>
                                </a>
                            </div>
                            <div className="bg-gradient-to-r from-gray-50 to-gray-100 rounded-2xl p-6 border border-gray-200">
                                <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                                    <User className="w-5 h-5 text-pink-500" />
                                    Profile Information
                                </h3>
                                <div className="space-y-4">
                                    <div className="flex items-center gap-3">
                                        <Mail className="w-5 h-5 text-pink-500" />
                                        <span className="text-gray-600">Email:</span>
                                        <span className="text-gray-800 font-medium">{userEmail}</span>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <Heart className="w-5 h-5 text-purple-500" />
                                        <span className="text-gray-600">Clothing Preference:</span>
                                        <button
                                            onClick={openGenderDialog}
                                            className="relative inline-flex items-center bg-gradient-to-r from-pink-500 to-purple-500 text-white px-3 py-1 rounded-full text-sm font-medium group cursor-pointer hover:from-pink-600 hover:to-purple-600 transition-all duration-200"
                                        >
                                            {userGender || 'Select Preference'}
                                            <span className="ml-2 group-hover:scale-110 transition-transform duration-200">
                                                <Pencil className="w-4 h-4" />
                                            </span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            {isGenderDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full mx-4 overflow-hidden">
                        <div className="bg-gradient-to-r from-pink-500 to-purple-500 px-6 py-4 text-white relative">
                            <h3 className="text-xl font-bold">Choose Clothing Preference</h3>
                            <button
                                onClick={() => setIsGenderDialogOpen(false)}
                                className="absolute right-4 top-4 hover:bg-white/20 rounded-full p-1 transition-colors duration-200"
                            >
                                <X className="w-5 h-5" />
                            </button>
                        </div>
                        <div className="p-6">
                            <p className="text-gray-600 mb-6">Select your preferred clothing category to get personalized recommendations.</p>
                            <div className="space-y-3 mb-6">
                                {genderOptions.map((option) => (
                                    <label
                                        key={option}
                                        className="flex items-center p-3 rounded-xl border-2 cursor-pointer transition-all duration-200 hover:bg-gray-50"
                                        style={{
                                            borderColor: selectedGender === option ? '#ec4899' : '#e5e7eb',
                                            backgroundColor: selectedGender === option ? '#fdf2f8' : 'transparent'
                                        }}
                                    >
                                        <input
                                            type="radio"
                                            name="gender"
                                            value={option}
                                            checked={selectedGender.toLowerCase() == option.toLowerCase()}
                                            onChange={(e) => setSelectedGender(e.target.value)}
                                            className="w-4 h-4 text-pink-500"
                                        />
                                        <span className="ml-3 text-gray-800 font-medium">{option}</span>
                                    </label>
                                ))}
                            </div>
                            <div className="flex gap-3">
                                <button
                                    onClick={() => setIsGenderDialogOpen(false)}
                                    className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors duration-200"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleGenderChange}
                                    disabled={!selectedGender}
                                    className="flex-1 px-4 py-2 bg-gradient-to-r from-pink-500 to-purple-500 text-white rounded-xl hover:from-pink-600 hover:to-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                                >
                                    Save Changes
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Account;