# SwipeStyle
# Tinder for Clothes: Full-Stack Project Implementation

## Core Components

1. **Database Schema**: The PostgreSQL database design includes tables for users, clothing items, attribute tokens, user preferences, swipes, wishlist items, and outfits.
   
2. **Backend Architecture**: The Spring Boot application includes:
   - Authentication system with JWT
   - Web scraper for clothing data acquisition
   - AI integration for clothing description
   - Recommendation engine based on user preferences
   - RESTful API endpoints for all functionality

3. **Frontend Design**: The React application features:
   - Swipe interface with smooth animations
   - User authentication and profile management
   - Style preference selection
   - Wishlist management
   - Outfit builder with Gen Z-inspired UI

## Key Features Implementation

### 1. Clothing Discovery Interface
The core of the application is the swipe interface where users can discover clothing items. Each item is displayed with an image, basic details, and AI-generated style tokens.

### 2. AI-Powered Description System
When clothes are scraped from external sites, an AI service (like OpenAI) generates descriptive tokens for attributes like:
- Color (blue, red, pastel)
- Pattern (solid, striped, floral)
- Material (cotton, denim, polyester)
- Style (casual, formal, streetwear)
- Occasion (everyday, office, party)

### 3. Preference Learning
When users swipe right on items, the system stores those tokens and gradually builds a preference profile. The recommendation engine uses these weighted preferences to suggest new items.

## Technical Implementation Details

The technical artifacts I've created include:

1. **System Architecture Diagram**: Shows how components interact across frontend, backend, and database.

2. **Database Schema**: Complete SQL schema with tables, relationships, and indexes for optimal performance.

3. **API Endpoints**: Spring Boot controllers for authentication, clothes swiping, wishlist management, and outfit generation.

4. **Core Services**: Implementation of the recommendation engine, AI description service, web scraper, and outfit generator.

5. **React Components**: Key UI components including the swipe card and main application interface with mobile-first design.

6. **Implementation Plan**: A phased approach to building the complete system.

### 5. API Documentation
Click [here](https://www.example.com](https://harshilarora2005.github.io/SwipeStyle/) to see the API documentation and endpoints
