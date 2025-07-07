import React, { useState } from 'react';
import { Eye, EyeOff, User, Mail, Lock, Sparkles, Heart} from 'lucide-react';
import * as Yup from 'yup';
import { LoginUser, RegisterUser, LogoutUser } from '../services/UserService';
import { FcGoogle } from "react-icons/fc";
import { useNavigate } from 'react-router';
const LoginForm = () => {
    const navigate = useNavigate();
    const [isLogin, setIsLogin] = useState(true);
    const [showPassword, setShowPassword] = useState(false);
    const [formData, setFormData] = useState({
        usernameOrEmail: '',
        email: '',
        username: '',
        password: '',
        gender: ''
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const loginSchema = Yup.object().shape({
        usernameOrEmail: Yup.string()
            .required('Username or Email is required')
            .test('is-email-or-username', 'Invalid email or username', (value) => {
                if (value) {
                    if (value.includes('@')) {
                        return Yup.string().email().isValidSync(value);
                    } else {
                        return Yup.string().min(3).max(20).matches(/^[a-zA-Z0-9]+$/).isValidSync(value);
                    }
                }
                return false;
            }),
        password: Yup.string()
            .min(6, 'Password must be at least 6 characters')
            .required('Password is required')
    });

    const registerSchema = Yup.object().shape({
        email: Yup.string()
            .email('Please enter a valid email')
            .required('Email is required'),
        username: Yup.string()
            .min(3, 'Username must be at least 3 characters')
            .max(20, 'Username must be less than 20 characters')
            .required('Username is required'),
        password: Yup.string()
            .min(8, 'Password must be at least 8 characters')
            .matches(/(?=.*[a-z])/, 'At least one lowercase letter')
            .matches(/(?=.*[A-Z])/, 'At least one uppercase letter')
            .matches(/(?=.*\d)/, 'At least one number')
            .required('Password is required'),
        gender: Yup.string()
            .oneOf(['male', 'female', 'unisex'], 'Select a clothing preference')
            .required('Clothing preference is required')
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = async () => {
        try {
            const schema = isLogin ? loginSchema : registerSchema;
            await schema.validate(formData, { abortEarly: false });
            setErrors({});
            return true;
        } catch (validationErrors) {
            const errorObject = {};
            validationErrors.inner.forEach(error => {
                errorObject[error.path] = error.message;
            });
            setErrors(errorObject);
            return false;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        const isValid = await validateForm();
        if (!isValid) {
            setIsLoading(false);
            return;
        }

        try {
            const payload = isLogin
                ? { usernameOrEmail: formData.usernameOrEmail, password: formData.password }
                : {
                    email: formData.email,
                    username: formData.username,
                    password: formData.password,
                    gender: formData.gender
                };

            const response = await (isLogin ? LoginUser(payload) : RegisterUser(payload));

            if (response.status === 200 || response.status === 201) {
                navigate("/account");
                setFormData({
                    usernameOrEmail: '',
                    email: '',
                    username: '',
                    password: '',
                    gender: ''
                });
                setErrors({});
            }
        } catch (error) {
            console.error('Error:', error.response ? error.response.data : error.message);
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400 || status === 409) {
                    setErrors({
                        ...data,
                        general: data.message || 'Validation error'
                    });
                } else if (status === 401) {
                    setErrors({ general: 'Invalid credentials' });
                } else {
                    setErrors({ general: data.message || 'Unexpected error' });
                }
            } else {
                setErrors({ general: 'Network error or unknown error occurred.' });
            }
        } finally {
            setIsLoading(false);
        }
    };

    const toggleMode = () => {
        setIsLogin(!isLogin);
        setFormData({
            usernameOrEmail: '',
            email: '',
            username: '',
            password: '',
            gender: ''
        });
        setErrors({});
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-pink-100 via-purple-50 to-indigo-100 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-pink-500 to-purple-600 rounded-full mb-4 shadow-lg">
                        <Sparkles className="w-8 h-8 text-white" />
                    </div>
                    <h1 className="text-3xl font-bold bg-gradient-to-r from-pink-600 to-purple-600 bg-clip-text text-transparent">
                        SwipeStyle
                    </h1>
                    <p className="text-gray-600 mt-2">
                        {isLogin ? 'Welcome back, fashionista!' : 'Join the style revolution!'}
                    </p>
                </div>

                <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl p-8 border border-white/20">
                    <div className="flex justify-center mb-6">
                        <div className="bg-gray-100 rounded-full p-1 flex">
                            <button
                                onClick={() => setIsLogin(true)}
                                className={`px-6 py-2 rounded-full text-sm font-medium transition-all ${isLogin ? 'bg-white text-purple-600 shadow-md' : 'text-gray-600 hover:text-purple-600'}`}
                            >
                                Login
                            </button>
                            <button
                                onClick={() => setIsLogin(false)}
                                className={`px-6 py-2 rounded-full text-sm font-medium transition-all ${!isLogin ? 'bg-white text-purple-600 shadow-md' : 'text-gray-600 hover:text-purple-600'}`}
                            >
                                Register
                            </button>
                        </div>
                    </div>

                    {errors.general && (
                        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl">
                            <p className="text-red-600 text-sm">{errors.general}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        {isLogin ? (
                            <div>
                                <div className="relative">
                                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                                    <input
                                        type="text"
                                        name="usernameOrEmail"
                                        placeholder="Username or Email"
                                        value={formData.usernameOrEmail}
                                        onChange={handleInputChange}
                                        className={`w-full pl-12 pr-4 py-3 rounded-xl border-2 transition-all focus:outline-none ${
                                            errors.usernameOrEmail
                                                ? 'border-red-300 bg-red-50'
                                                : 'border-gray-200 focus:border-purple-500 bg-white/50'
                                        }`}
                                    />
                                </div>
                                {errors.usernameOrEmail && (
                                    <p className="text-red-500 text-sm mt-1 ml-1">{errors.usernameOrEmail}</p>
                                )}
                            </div>
                        ) : (
                            <div>
                                <div className="relative">
                                    <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                                    <input
                                        type="email"
                                        name="email"
                                        placeholder="Email address"
                                        value={formData.email}
                                        onChange={handleInputChange}
                                        className={`w-full pl-12 pr-4 py-3 rounded-xl border-2 transition-all focus:outline-none ${
                                            errors.email
                                                ? 'border-red-300 bg-red-50'
                                                : 'border-gray-200 focus:border-purple-500 bg-white/50'
                                        }`}
                                    />
                                </div>
                                {errors.email && (
                                    <p className="text-red-500 text-sm mt-1 ml-1">{errors.email}</p>
                                )}
                            </div>
                        )}

                        {!isLogin && (
                            <div>
                                <div className="relative">
                                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                                    <input
                                        type="text"
                                        name="username"
                                        placeholder="Username"
                                        value={formData.username}
                                        onChange={handleInputChange}
                                        className={`w-full pl-12 pr-4 py-3 rounded-xl border-2 transition-all focus:outline-none ${
                                            errors.username
                                                ? 'border-red-300 bg-red-50'
                                                : 'border-gray-200 focus:border-purple-500 bg-white/50'
                                        }`}
                                    />
                                </div>
                                {errors.username && (
                                    <p className="text-red-500 text-sm mt-1 ml-1">{errors.username}</p>
                                )}
                            </div>
                        )}

                        <div>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    name="password"
                                    placeholder="Password"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                    className={`w-full pl-12 pr-12 py-3 rounded-xl border-2 transition-all focus:outline-none ${
                                        errors.password
                                            ? 'border-red-300 bg-red-50'
                                            : 'border-gray-200 focus:border-purple-500 bg-white/50'
                                    }`}
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-purple-600"
                                >
                                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                            {errors.password && (
                                <p className="text-red-500 text-sm mt-1 ml-1">{errors.password}</p>
                            )}
                        </div>

                        {!isLogin && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2 ml-1">
                                    What type of clothing do you prefer?
                                </label>
                                <div className="grid grid-cols-3 gap-2">
                                    {[
                                        { value: 'male', label: 'Male', emoji: '👔' },
                                        { value: 'female', label: 'Female', emoji: '👗' },
                                        { value: 'unisex', label: 'Unisex', emoji: '👕' }
                                    ].map((option) => (
                                        <button
                                            key={option.value}
                                            type="button"
                                            onClick={() => handleInputChange({ target: { name: 'gender', value: option.value } })}
                                            className={`p-3 rounded-xl border-2 transition-all text-center ${
                                                formData.gender === option.value
                                                    ? 'border-purple-500 bg-purple-50 text-purple-700'
                                                    : 'border-gray-200 hover:border-purple-300 bg-white/50'
                                            }`}
                                        >
                                            <div className="text-2xl mb-1">{option.emoji}</div>
                                            <div className="text-sm font-medium">{option.label}</div>
                                        </button>
                                    ))}
                                </div>
                                {errors.gender && (
                                    <p className="text-red-500 text-sm mt-1 ml-1">{errors.gender}</p>
                                )}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full bg-gradient-to-r from-pink-500 to-purple-600 text-white py-3 rounded-xl font-semibold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:transform-none"
                        >
                            {isLoading ? (
                                <div className="flex items-center justify-center">
                                    <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                                    {isLogin ? 'Signing In...' : 'Creating Account...'}
                                </div>
                            ) : (
                                <div className="flex items-center justify-center">
                                    <Heart className="w-5 h-5 mr-2" />
                                    {isLogin ? 'Sign In' : 'Join SwipeStyle'}
                                </div>
                            )}
                        </button>
                    </form>

                    <div className="text-center mt-6">
                        <p className="text-gray-600">
                            {isLogin ? "Don't have an account? " : "Already have an account? "}
                            <button
                                onClick={toggleMode}
                                className="text-purple-600 font-semibold hover:text-purple-800 transition-colors"
                            >
                                {isLogin ? 'Sign up here' : 'Sign in here'}
                            </button>
                        </p>
                    </div>
                </div>
                <div className="flex items-center my-4">
                    <div className="flex-grow border-t border-gray-300"></div>
                    <span className="mx-4 text-gray-400 text-sm">or</span>
                    <div className="flex-grow border-t border-gray-300"></div>
                    </div>
                    <div className="flex flex-col gap-3">
                    <button
                        type="button"
                        onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorization/google'}
                        className="w-full flex items-center justify-center gap-3 bg-white border border-gray-300 py-2 rounded-xl shadow-sm hover:bg-gray-50 transition-all"
                    >
                        <FcGoogle className="w-5 h-5" />
                        Continue with Google
                    </button>
                </div>
                <div className="text-center mt-6 text-gray-500 text-sm">
                    Made with <Heart className="inline w-4 h-4 text-pink-500" /> for fashion lovers
                </div>
            </div>
        </div>
    );
};

export default LoginForm;
