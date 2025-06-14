import { Sparkles, User } from 'lucide-react';
import { useNavigate, Link } from 'react-router'; 
import { useContext } from 'react';
import UserContext from './utils/UserContext';
const Header = () => {
    const navigate = useNavigate();
    const { isLoggedIn, setIsLoggedIn, userName, setUserName, userProfile, setUserProfile } = useContext(UserContext);
    const navItems = [
        { name: 'Explore' },
        { name: 'For You' },
        { name: 'Collections' },
        { name: 'Account' }
    ];

    const handleLoginClick = () => {
        if (isLoggedIn) {
            setIsLoggedIn(false);
            setUserName("Guest");
            setUserProfile("https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg");
        } else {
            navigate("/login");
        }
    };

    return (
        <header className="bg-white/90 backdrop-blur-md border-b border-pink-100 sticky top-0 z-50 shadow-sm">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <div className="flex items-center">
                        <div className="inline-flex items-center justify-center w-10 h-10 bg-gradient-to-r from-pink-500 to-purple-600 rounded-full shadow-lg">
                            <Sparkles className="w-5 h-5 text-white" />
                        </div>
                        <h1 className="ml-3 text-xl font-bold bg-gradient-to-r from-pink-600 to-purple-600 bg-clip-text text-transparent">
                            SwipeStyle
                        </h1>
                    </div>
                    <nav className="hidden md:flex items-center space-x-8">
                        {navItems.map((item) => (
                            <Link to={`/${item.name.toLowerCase().replace(/\s+/g, '')}`} key={item.name}>
                                <div className="text-gray-700 hover:text-purple-600 px-3 py-2 rounded-lg text-sm font-medium transition-colors hover:bg-purple-50">
                                    {item.name}
                                </div>
                            </Link>
                        ))}
                    </nav>
                    <div className="flex items-center space-x-4">
                        <div className="w-9 h-9 rounded-full overflow-hidden border-2 border-purple-300 shadow-sm hover:scale-105 transition-transform">
                            <img
                                src={userProfile}
                                alt="User profile"
                                className="w-full h-full object-cover"
                            />
                        </div>
                        <span className="text-sm sm:text-base font-medium">
                            Hi, <span className="text-[#7289da] font-semibold">{userName}</span>
                        </span>

                        <button
                            onClick={handleLoginClick}
                            className="bg-gradient-to-r from-pink-500 to-purple-600 text-white px-4 py-2 rounded-full text-sm font-medium shadow-md hover:shadow-lg transition-all flex items-center"
                        >
                            {isLoggedIn ? 'Logout' : 'Login'}
                        </button>
                    </div>

                </div>
            </div>
        </header>
    );
};

export default Header;
