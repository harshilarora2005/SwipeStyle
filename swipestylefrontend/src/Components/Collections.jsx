/* eslint-disable no-unused-vars */
import React, { useContext, memo, useCallback, useMemo, useState } from "react";
import UserContext from "./utils/UserContext";
import useGetProducts from "./hooks/useGetProducts";
import Loading from "./Loading";
import { useNavigate } from "react-router";
import axios from 'axios';

const ProductCard = memo(({ item, index }) => {
    const handleClick = useCallback(() => {
        window.open(item.productUrl, "_blank");
    }, [item.productUrl]);

    return (
        <div
        className="group relative cursor-pointer"
        onClick={handleClick}
        >
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden transform transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:-translate-y-2 border-2 border-transparent hover:border-pink-200">
            <div className="relative overflow-hidden">
            <img
                src={item.imageUrl}
                alt={item.altText}
                className="w-full h-64 object-cover transition-transform duration-500 group-hover:scale-110"
                loading="lazy"
                decoding="async"
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
    );
});

ProductCard.displayName = 'ProductCard';

const EmptyState = memo(() => (
    <div className="text-center py-20">
        <div className="text-8xl mb-6">💔</div>
        <h2 className="text-2xl font-bold text-gray-700 mb-4">
        No favorites yet!
        </h2>
        <p className="text-gray-500 text-lg">
        Start exploring and save items you love 💕
        </p>
    </div>
));

EmptyState.displayName = 'EmptyState';

const AestheticAnalysis = memo(({ aesthetic, isLoading, onAnalyze }) => {
    if (isLoading) {
        return (
            <div className="bg-white rounded-2xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-500 mx-auto mb-4"></div>
                    <p className="text-gray-600">Analyzing your aesthetic style... ✨</p>
                </div>
            </div>
        );
    }

    if (!aesthetic) {
        return (
            <div className="bg-white rounded-2xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
                <div className="text-center">
                    <h3 className="text-2xl font-bold text-gray-800 mb-4">
                        Discover Your Aesthetic 🎨
                    </h3>
                    <p className="text-gray-600 mb-6">
                        Let AI analyze your liked items to reveal your unique style preferences
                    </p>
                    <button
                        onClick={onAnalyze}
                        className="bg-gradient-to-r from-purple-500 to-pink-500 text-white px-6 py-3 rounded-full font-semibold hover:from-purple-600 hover:to-pink-600 transform hover:scale-105 transition-all duration-300 shadow-lg"
                    >
                        Analyze My Style ✨
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-2xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
            <h3 className="text-2xl font-bold text-gray-800 mb-4 text-center">
                Your Aesthetic Analysis 🎨
            </h3>
            <div className="bg-gradient-to-r from-pink-50 via-purple-50 to-pink-100 rounded-3xl p-8 shadow-md">
                <div className="whitespace-pre-wrap text-gray-600 leading-relaxed text-lg font-light tracking-wide">
                    {aesthetic}
                </div>
            </div>
            <div className="text-center mt-4">
                <button
                    onClick={onAnalyze}
                    className="text-purple-600 hover:text-purple-800 font-semibold text-sm transition-colors duration-300"
                >
                    Re-analyze Style
                </button>
            </div>
        </div>
    );
});

AestheticAnalysis.displayName = 'AestheticAnalysis';

const Collections = () => {
    const navigate = useNavigate();
    const { userEmail, isLoggedIn } = useContext(UserContext);
    const { products, loading, error } = useGetProducts(userEmail, "LIKED");
    const [aesthetic, setAesthetic] = useState(null);
    const [isAnalyzing, setIsAnalyzing] = useState(false);

    const handleNavigateHome = useCallback(() => {
        navigate("/");
    }, [navigate]);

    const itemsCountText = useMemo(() => {
        const count = products?.length || 0;
        return `${count} ${count === 1 ? 'item' : 'items'} saved`;
    }, [products?.length]);

    const analyzeAesthetic = useCallback(async () => {
        if (!products || products.length === 0) {
            alert("No liked items to analyze!");
            return;
        }

        setIsAnalyzing(true);
        
        try {
            const altTexts = products
                .map(item => item.altText)
                .filter(altText => altText && altText.trim() !== "");

            if (altTexts.length === 0) {
                alert("No alt text available for analysis!");
                setIsAnalyzing(false);
                return;
            }

            const prompt = `
                Analyze the following product descriptions from a user's liked items and determine their aesthetic style preferences. 
                
                Product descriptions:
                ${altTexts.map((text, index) => `${index + 1}. ${text}`).join('\n')}
                
                Based on these descriptions, please:
                Identify the dominant aesthetic style(s) (e.g., dark academia, goth, cottagecore, minimalist, boho, vintage, streetwear, etc.)
                Give in the form, "Your aesthetic feels like {___}"
                Just replace {___} with the aesthetic
            `;
            console.log(import.meta.env.VITE_GEMINI_API_KEY);
            const response = await axios.post(
                `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${import.meta.env.VITE_GEMINI_API_KEY}`,
                {
                    contents: [
                        {
                            parts: [
                                {
                                    text: prompt
                                }
                            ]
                        }
                    ]
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            const analysis = response.data?.candidates?.[0]?.content?.parts?.[0]?.text;
            
            if (analysis) {
                setAesthetic(analysis);
            } else {
                throw new Error("No analysis received from Gemini API");
            }
        } catch (error) {
            console.error("Error analyzing aesthetic:", error);
            
            if (error.response?.status === 400) {
                alert("Invalid request to Gemini API. Please check your API key and try again.");
            } else if (error.response?.status === 403) {
                alert("Access denied. Please verify your Gemini API key has the necessary permissions.");
            } else if (error.response?.status === 429) {
                alert("Too many requests. Please wait a moment and try again.");
            } else {
                alert(`Failed to analyze aesthetic: ${error.message}\n\nPlease check your Gemini API key and try again.`);
            }
        } finally {
            setIsAnalyzing(false);
        }
    }, [products]);

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
        return <Error error={error} />;
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
                {itemsCountText}
                </span>
            </div>
            </div>
        </div>

        {/* Aesthetic Analysis Component */}
        {products && products.length > 0 && (
            <AestheticAnalysis 
                aesthetic={aesthetic} 
                isLoading={isAnalyzing} 
                onAnalyze={analyzeAesthetic}
            />
        )}

        {(!products || products.length === 0) ? (
            <EmptyState />
        ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 max-w-7xl mx-auto">
            {products.map((item, index) => (
                <ProductCard key={`${item.productId}-${index}`} item={item} index={index} />
            ))}
            </div>
        )}
        
        <div className="fixed bottom-8 right-8">
            <button 
            className="bg-gradient-to-r from-pink-500 to-purple-600 text-white p-4 rounded-full shadow-2xl hover:shadow-3xl transform hover:scale-110 transition-all duration-300"
            onClick={handleNavigateHome}
            aria-label="Go to home"
            >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            </button>
        </div>
        </div>
    );
};

export default memo(Collections);