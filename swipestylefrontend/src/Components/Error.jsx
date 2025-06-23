import { useRouteError } from "react-router";

const Error = ({ error }) => {
    const routeError = useRouteError?.();
    const err = error || routeError;

    return (
        <div className="min-h-screen bg-gradient-to-br from-pink-100 via-purple-50 to-indigo-100 flex items-center justify-center p-4">
            <div className="bg-white rounded-3xl shadow-xl p-8 max-w-md w-full text-center transform hover:scale-105 transition-transform duration-300">
                <div className="text-8xl mb-4 animate-bounce">😢</div>
                <div className="bg-red-100 text-red-600 rounded-full px-4 py-2 inline-block mb-4 font-bold text-lg">
                    {err?.status || "Oops"}: {err?.statusText || err?.message || "Something went wrong"}
                </div>
                <h2 className="text-2xl font-bold text-gray-800 mb-2">
                    Uh oh! We hit a little bump!
                </h2>
                <p className="text-gray-600 mb-6">
                    Don't worry, even the best websites have bad days sometimes! 🌈
                </p>
                <div className="space-y-3">
                    <button
                        onClick={() => window.history.back()}
                        className="w-full bg-gradient-to-r from-pink-400 to-purple-500 text-white font-semibold py-3 px-6 rounded-full hover:from-pink-500 hover:to-purple-600 transition-all duration-300 transform hover:scale-105 shadow-lg"
                    >
                        ← Go Back
                    </button>
                    <button
                        onClick={() => (window.location.href = "/")}
                        className="w-full bg-gradient-to-r from-blue-400 to-indigo-500 text-white font-semibold py-3 px-6 rounded-full hover:from-blue-500 hover:to-indigo-600 transition-all duration-300 transform hover:scale-105 shadow-lg"
                    >
                        🏠 Go Home
                    </button>
                </div>
                <div className="mt-6 flex justify-center space-x-2">
                    <span className="text-2xl animate-pulse">✨</span>
                    <span className="text-2xl animate-bounce delay-100">💫</span>
                    <span className="text-2xl animate-pulse delay-200">⭐</span>
                </div>
            </div>
        </div>
    );
};
export default Error;