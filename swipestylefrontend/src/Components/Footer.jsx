import { Sparkles, Mail, Heart } from 'lucide-react';
import { FaInstagram,FaGithub,FaLinkedin } from 'react-icons/fa';
import { Link} from 'react-router';
const Footer = () => {
    return (
        <footer className="bg-gradient-to-br from-pink-50 via-purple-50 to-indigo-50 border-t border-pink-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="col-span-1 md:col-span-2">
                <div className="flex items-center mb-4">
                <div className="inline-flex items-center justify-center w-12 h-12 bg-gradient-to-r from-pink-500 to-purple-600 rounded-full shadow-lg">
                    <Sparkles className="w-6 h-6 text-white" />
                </div>
                <h2 className="ml-3 text-2xl font-bold bg-gradient-to-r from-pink-600 to-purple-600 bg-clip-text text-transparent">
                    SwipeStyle
                </h2>
                </div>
                <p className="text-gray-600 mb-4 max-w-md">
                Discover your perfect style with AI-powered fashion recommendations. 
                Swipe, style, and shine with the latest trends tailored just for you.
                </p>
                <div className="flex space-x-4">
                <a href="#" className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">
                    <FaInstagram className="w-5 h-5 text-purple-600" />
                </a>
                <a href="#" className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">
                    <FaGithub className="w-5 h-5 text-purple-600" />
                </a>
                <a href="#" className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">
                    <FaLinkedin className="w-5 h-5 text-purple-600" />
                </a>
                <a href="#" className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">
                    <Mail className="w-5 h-5 text-purple-600" />
                </a>
                </div>
            </div>
            <div>
                <h3 className="font-semibold text-gray-800 mb-4">Quick Links</h3>
                <ul className="space-y-2">
                {['Explore', 'For You', 'Collections'].map((link) => (
                    <li key={link}>
                    <Link to={`/${link.toLowerCase().replace(/\s+/g, '')}`} key={link}>
                        {link}
                    </Link>
                    </li>
                ))}
                </ul>
            </div>
            <div>
                <h3 className="font-semibold text-gray-800 mb-4">Support</h3>
                <ul className="space-y-2">
                {['Contact Me'].map((link) => (
                    <li key={link}>
                    <a href="#" className="text-gray-600 hover:text-purple-600 transition-colors">
                        {link}
                    </a>
                    </li>
                ))}
                </ul>
            </div>
            </div>

            <div className="border-t border-pink-200 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
            <p className="text-gray-600 text-sm">
                © 2025 SwipeStyle. All rights reserved.
            </p>
            <div className="flex items-center mt-4 md:mt-0 text-gray-500 text-sm">
                Made with <Heart className="inline w-4 h-4 text-pink-500 mx-1" /> for fashion lovers
            </div>
            </div>
        </div>
        </footer>
    );
};
export default Footer;
