import { useContext } from "react"
import UserContext from "./utils/UserContext";
import useGetProducts from "./hooks/useGetProducts";
import Loading from "./Loading";
import { useNavigate } from "react-router";
import useAuth from "./hooks/useAuth";
const Collections = () => {
    const navigate = useNavigate();
    const { userEmail,isLoggedIn } = useContext(UserContext);
    const { products, loading, error } = useGetProducts(userEmail, "LIKED");
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
    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
                <Loading />
                <p className="mt-4 text-gray-600 text-lg">Fetching your liked products... 💕</p>
            </div>
        );
    }

    if (error) {
        return (
            <Error error={error} />
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50 p-6">
            <div className="text-center mb-12">
                <h1 className="text-4xl lg:text-5xl font-bold bg-gradient-to-r from-pink-500 via-purple-500 to-indigo-500 bg-clip-text text-transparent mb-4">
                    Your Favorite Collection ✨
                </h1>
                <p className="text-gray-600 text-lg">
                    Items you've fallen in love with 💕
                </p>
                <div className="flex justify-center mt-4">
                    <div className="bg-white rounded-full px-6 py-2 shadow-lg">
                        <span className="text-sm font-semibold text-purple-600">
                            {products.length} {products.length === 1 ? 'item' : 'items'} saved
                        </span>
                    </div>
                </div>
            </div>

            {products.length === 0 ? (
                <div className="text-center py-20">
                    <div className="text-8xl mb-6">💔</div>
                    <h2 className="text-2xl font-bold text-gray-700 mb-4">
                        No favorites yet!
                    </h2>
                    <p className="text-gray-500 text-lg">
                        Start exploring and save items you love 💕
                    </p>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 max-w-7xl mx-auto">
                    {products.map((item, index) => (
                        <div
                            key={index}
                            className="group relative cursor-pointer"
                            onClick={() => window.open(item.productUrl, "_blank")}
                        >
                            <div className="bg-white rounded-2xl shadow-lg overflow-hidden transform transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:-translate-y-2 border-2 border-transparent hover:border-pink-200">
                                <div className="relative overflow-hidden">
                                    <img
                                        src={item.imageUrls}
                                        alt={item.altText}
                                        className="w-full h-64 object-cover transition-transform duration-500 group-hover:scale-110"
                                    />
                                    <div className="absolute top-3 right-3">
                                        <div className="bg-white/80 backdrop-blur-sm rounded-full p-2 transform transition-all duration-300 group-hover:scale-110 group-hover:bg-pink-100">
                                            <svg className="w-5 h-5 text-pink-500" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.17a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                                <div className="p-4">
                                    <div className="flex items-center justify-between mb-2">
                                        <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2 py-1 rounded-full">
                                            {item.gender}
                                        </span>
                                        <span className="text-xs text-gray-500">
                                            #{item.productId}
                                        </span>
                                    </div>

                                    <h3 className="font-bold text-gray-800 text-sm mb-1 truncate">
                                        {item.name}
                                    </h3>

                                    <p className="text-green-600 font-bold text-lg">
                                        {item.price}
                                    </p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
            <div className="fixed bottom-8 right-8">
                <button className="bg-gradient-to-r from-pink-500 to-purple-600 text-white p-4 rounded-full shadow-2xl hover:shadow-3xl transform hover:scale-110 transition-all duration-300"
                onClick={()=>navigate("/")}>
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                </button>
            </div>
        </div>
    );
}

export default Collections;
