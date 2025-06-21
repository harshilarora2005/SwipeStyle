/* eslint-disable no-unused-vars */
import { motion } from "framer-motion";

const containerVariants = {
    hidden: {},
    visible: {
        transition: {
        staggerChildren: 0.15,
        delayChildren: 0.2,
        },
    },
};

const itemVariants = {
    hidden: { opacity: 0, y: 40, scale: 0.95 },
    visible: { opacity: 1, y: 0, scale: 1 },
};

const CardDetails = ({ clothing }) => {
    if (!clothing) return null;

    const {
        altText,
        gender,
        imageUrl,
        name,
        price,
        productId,
        productUrl,
    } = clothing;

    return (
        <div className="min-h-screen flex items-center justify-center px-4 bg-gradient-to-br from-pink-50 via-purple-50 to-blue-50">
        <motion.div
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="max-w-2xl w-full"
        >
            <motion.div
            variants={itemVariants}
            className="rounded-3xl shadow-2xl border border-gray-100 overflow-hidden relative backdrop-blur-sm bg-white/95"
            >
            <div className="relative p-8 lg:p-12">
                {/* Corner image */}
                <motion.div
                variants={itemVariants}
                className="absolute top-6 right-6"
                >
                <div className="w-20 h-20 lg:w-24 lg:h-24 rounded-2xl overflow-hidden shadow-xl border-4 border-pink-200 transform rotate-12 hover:rotate-0 transition-transform duration-500">
                    <img
                    src={imageUrl}
                    alt={altText}
                    className="w-full h-full object-cover"
                    />
                </div>
                </motion.div>

                <div className="pr-24 lg:pr-32">
                <motion.h3
                    variants={itemVariants}
                    className="text-3xl lg:text-4xl font-bold text-gray-900 mb-4 leading-tight"
                >
                    {name}
                </motion.h3>

                <motion.div
                    variants={itemVariants}
                    className="flex items-center gap-4 flex-wrap mb-4"
                >
                    <span className="text-4xl lg:text-5xl font-black bg-gradient-to-r from-green-500 to-emerald-600 bg-clip-text text-transparent">
                    {price}
                    </span>
                    <div className="bg-gradient-to-r from-pink-100 to-purple-100 px-4 py-2 rounded-full">
                    <span className="text-sm text-purple-700 uppercase tracking-wide font-semibold">
                        {gender}
                    </span>
                    </div>
                </motion.div>

                <motion.div
                    variants={itemVariants}
                    className="bg-gray-50 rounded-2xl p-4 mb-6"
                >
                    <p className="text-gray-600 text-sm leading-relaxed">
                    <span className="font-semibold text-gray-800">
                        Product ID:
                    </span>{" "}
                    {productId}
                    </p>
                    <p className="text-gray-600 text-sm leading-relaxed mt-1">
                    <span className="font-semibold text-gray-800">Category:</span>{" "}
                    {gender} Fashion
                    </p>
                </motion.div>

                {/* Action buttons */}
                <motion.div
                    variants={itemVariants}
                    className="flex gap-4 flex-col sm:flex-row"
                >
                    <a
                    href={productUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex-1 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-bold py-4 px-8 rounded-2xl hover:from-blue-600 hover:to-purple-700 transition-all duration-300 text-center shadow-lg hover:shadow-2xl transform hover:-translate-y-1 hover:scale-105 text-lg"
                    >
                    ✨ Shop Now
                    </a>
                    <button className="flex-shrink-0 sm:w-auto w-full bg-white hover:bg-pink-50 text-pink-600 font-bold py-4 px-8 rounded-2xl transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-1 border-2 border-pink-200 hover:border-pink-300 flex items-center justify-center gap-2">
                    <svg
                        className="w-6 h-6"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                    >
                        <path
                        fillRule="evenodd"
                        d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.17a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
                        clipRule="evenodd"
                        />
                    </svg>
                    <span className="sm:hidden">Add to Favorites</span>
                    </button>
                </motion.div>
                </div>
            </div>
            </motion.div>
        </motion.div>
        </div>
    );
};

export default CardDetails;