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
**AI/ML Service**: FastAPI + Python (Sentence Transformers)  
**Authentication**: Google OAuth  
**Deployment**: Docker containerized

## 🏗 Architecture Overview
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React App     │    │   Spring Boot    │    │   FastAPI       │    │   PostgreSQL    │
│                 │    │                  │    │   (Python)      │    │                 │
│ • Swipe UI      │◄──►│ • REST APIs      │◄──►│ • ML Models     │    │ • User Data     │
│ • Auth          │    │ • Google OAuth   │    │ • Embeddings    │◄──►│ • Clothing DB   │
│ • Preferences   │    │ • Data Layer     │    │ • Classification│    │ • Preferences   │
└─────────────────┘    └──────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 Getting Started

### Prerequisites
Before running SwipeStyle, ensure you have the following installed:
- **Java 17+** (for Spring Boot backend)
- **Node.js 16+** and **npm** (for React frontend)
- **Python 3.8+** and **pip** (for AI/ML service)
- **PostgreSQL 13+** (database)
- **Git** (for cloning the repository)

### 1. Clone the Repository
```bash
git clone https://github.com/harshilarora2005/SwipeStyle.git
cd SwipeStyle
```

### 2. Database Setup
First, create the PostgreSQL database:

```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create the database
CREATE DATABASE swipestyle;

-- Create a user (optional but recommended)
CREATE USER swipestyle_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE swipestyle TO swipestyle_user;

-- Exit psql
\q
```

### 3. Backend Configuration
Navigate to the backend directory and configure environment variables:

```bash
cd swipestylebackend
```

Copy the example environment file and edit it:
```bash
cp .env.example .env
```

Edit the `.env` file with your configuration:
```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/swipestyle
DATABASE_USERNAME=swipestyle
DATABASE_PASSWORD=your_password

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 4. Google OAuth Setup
To enable Google authentication:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Set application type to "Web application"
6. Add authorized redirect URIs:
   - `http://localhost:8080/oauth2/callback/google`
   - `http://localhost:3000/auth/callback`
7. Copy the Client ID and Client Secret to your `.env` file

### 5. Run the Backend
Install dependencies and start the Spring Boot server:

```bash
# Install Maven dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 6. Frontend Configuration
Open a new terminal and navigate to the frontend directory:

```bash
cd swipestylefrontend
```

### 7. Run the Frontend
Install dependencies and start the React development server:

```bash
# Install npm dependencies
npm install

# Start the development server
npm start
```

The frontend will start on `http://localhost:5173`

### 8. Running the whole application
No additional configuration needed - runs on `http://localhost:8000`

### 9. 🚀 Running the Complete Application
To run SwipeStyle, you need to start all services:

1. **PostgreSQL Database** - Make sure it's running
2. **Python AI Service** -
   ```python
   cd PythonServer
   pip install -r requirements.txt
   && python embedding_server.py
   ```
3. Visit `localhost:5173`.

## 📋 Environment Variables Reference

### Backend (.env in swipestylebackend/)
| Variable | Description | Required | Example |
|----------|-------------|----------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | Yes | `jdbc:postgresql://localhost:5432/swipestyle` |
| `DATABASE_USERNAME` | Database username | Yes | `swipestyle` |
| `DATABASE_PASSWORD` | Database password | Yes | `your_password` |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID | Yes | `xxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Secret | Yes | `your_secret` |

### Frontend Configuration
The frontend is pre-configured with API endpoints in `public/constants.js`:
- User Clothing API: `http://localhost:8080/api/user-clothing`
- Users API: `http://localhost:8080/api/users`  
- Swipe Style API: `http://localhost:8080/api/swipe-style`

*No additional environment configuration is needed for the frontend.*

The services will be available at:
- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- AI Service: `http://localhost:8000`

## 🚀 Implementation Highlights
### Smart Recommendation Engine
- Analyzes user swipe patterns to build preference vectors
- Weights attributes like style, color, material, and occasion
- Uses collaborative filtering to suggest items similar users loved

### AI-Powered Style Analysis
- Uses Sentence Transformers (`all-MiniLM-L6-v2`) for generating clothing embeddings
- Zero-shot classification with `facebook/bart-large-mnli` for style categorization
- Extracts style attributes: *casual, formal, streetwear, vintage*
- Color analysis: *earth tones, pastels, bold colors*
- Material detection: *cotton, denim, silk, polyester*

### Seamless Data Pipeline
- FastAPI service provides ML-powered embedding generation
- Spring Boot backend handles business logic and data persistence
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

## 📈 Future Enhancements
- Social features (share outfits, follow fashion influencers)
- AR try-on integration
- Price tracking and deal alerts  
- Seasonal style recommendations
- Brand partnership integrations

## 🔗 Links
**API Documentation**: [View Endpoints](https://harshilarora2005.github.io/SwipeStyle/)  

## 🆘 Support
If you encounter any issues or have questions:
- Open an issue on GitHub
- Email: support@swipestyle.app
- Documentation: [docs.swipestyle.app](https://docs.swipestyle.app)

---
*Built with ❤️ for fashion enthusiasts who want to discover their perfect style*
