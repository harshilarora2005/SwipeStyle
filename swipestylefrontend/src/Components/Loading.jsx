import {Shirt, Crown, Star } from 'lucide-react';
import React, { useState, useEffect } from 'react';
const Loading = () => {
    const [currentDot, setCurrentDot] = useState(0);
    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentDot(prev => (prev + 1) % 3);
        }, 500);
        return () => clearInterval(interval);
    }, []);
    return (
        <div className="flex items-center justify-center space-x-4">
        {[Shirt, Crown, Star].map((Icon, index) => (
            <div
            key={index}
            className={`w-12 h-12 bg-gradient-to-r from-pink-400 to-purple-500 rounded-full flex items-center justify-center transition-all duration-500 ${
                currentDot === index ? 'scale-110 shadow-lg' : 'scale-90 opacity-50'
            }`}
            style={{
                animationDelay: `${index * 0.2}s`
            }}
            >
            <Icon className="w-6 h-6 text-white" />
            </div>
        ))}
        </div>
    )
};

export default Loading;