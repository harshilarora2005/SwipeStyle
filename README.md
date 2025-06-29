# SwipeStyle 👗✨
*Tinder for Clothes - Discover your perfect style with AI-powered recommendations*

## What is SwipeStyle?
SwipeStyle is a web app that revolutionizes how you discover fashion. Swipe right on clothes you love, build your style profile, and get personalized recommendations powered by AI. Think Tinder, but for building your dream wardrobe.

## ✨ Key Features

### 🔥 Smart Discovery
- **Swipe Interface**: Intuitive left/right swiping to discover new pieces
- **AI-Powered Matching**: Machine learning analyzes your preferences to surface perfect matches
- **Visual-First Design**: High-quality images with smooth animations and transitions

### 🎯 Personalized Experience  
- **Style Profiling**: AI learns your taste from swipes (casual vs formal, colors, patterns, materials)
- **Smart Recommendations**: Algorithm improves with every interaction
- **Preference Categories**: Color palettes, style aesthetics, occasions, and more

### 💫 Organization Tools
- **Wishlist**: Save items you love for later purchase
- **Outfit Builder**: Mix and match pieces to create complete looks
- **Style Analytics**: Track your fashion preferences over time

## 🛠 Tech Stack

**Frontend**: React + Tailwind CSS  
**Backend**: Spring Boot + PostgreSQL  
**AI Integration**: OpenAI API for style analysis  
**Authentication**: Session-based login  
**Deployment**: Docker containerized

## 🏗 Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   React App     │    │   Spring Boot    │    │   PostgreSQL    │
│                 │    │                  │    │                 │
│ • Swipe UI      │◄──►│ • REST APIs      │◄──►│ • User Data     │
│ • Auth          │    │ • AI Integration │    │ • Clothing DB   │
│ • Preferences   │    │ • Web Scraper    │    │ • Preferences   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 🚀 Implementation Highlights

### Smart Recommendation Engine
- Analyzes user swipe patterns to build preference vectors
- Weights attributes like style, color, material, and occasion
- Uses collaborative filtering to suggest items similar users loved

### AI-Powered Style Analysis
- Automatically generates descriptive tokens for scraped clothing items
- Extracts style attributes: *casual, formal, streetwear, vintage*
- Color analysis: *earth tones, pastels, bold colors*
- Material detection: *cotton, denim, silk, polyester*

### Seamless Data Pipeline
- Web scraper collects clothing data from fashion retailers
- AI service processes images and descriptions
- Real-time recommendation updates based on user interactions

## 📱 User Experience

1. **Onboarding**: Quick style quiz to set initial preferences
2. **Discovery**: Swipe through curated clothing recommendations  
3. **Learning**: AI adapts to your taste with every interaction
4. **Organization**: Build wishlists and outfit collections
5. **Shopping**: Direct links to purchase favorited items

## 🎨 Design Philosophy

**Mobile-First**: Optimized for touch interactions and mobile screens  
**Gen Z Aesthetic**: Modern, vibrant UI with smooth animations  
**Minimal Friction**: One-tap actions for maximum engagement  
**Visual Focus**: Large, high-quality product imagery

## 📊 Database Design

Core entities include users, clothing items, style attributes, swipe history, wishlists, and outfit combinations. Optimized for fast recommendation queries and real-time updates.

## 📈 Future Enhancements

- Social features (share outfits, follow fashion influencers)
- AR try-on integration
- Price tracking and deal alerts  
- Seasonal style recommendations
- Brand partnership integrations

## 🔗 Links

**API Documentation**: [View Endpoints](https://harshilarora2005.github.io/SwipeStyle/)

---

*Built with ❤️ for fashion enthusiasts who want to discover their perfect style*
