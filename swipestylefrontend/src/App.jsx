import './App.css'
import { lazy, Suspense } from 'react';
import Header from './Components/Header';
import Footer from './Components/Footer';
import { createBrowserRouter ,Outlet, RouterProvider} from 'react-router';
import { useState } from 'react';
import Error from './Components/Error';
import Loading from './Components/Loading';
import UserContext from './Components/utils/UserContext';
const Layout = () => {
  const [userName, setUserName] = useState("Guest");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState(null);
  const [userImage, setUserImage] = useState(null);
  const [userProfile, setUserProfile] = useState("https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg");
  const [userGender, setUserGender] = useState("UNISEX");
  return (
    <>
    <UserContext.Provider value={{ userName, setUserName, isLoggedIn, setIsLoggedIn, userEmail, setUserEmail, userImage, setUserImage, userProfile, setUserProfile, userGender, setUserGender }}>
      <Header/>
      <Outlet/>
      <Footer/>
    </UserContext.Provider>
    </>
  )
}
function App() {
  const Body = lazy(() => import('./Components/Body'));
  const ForYou = lazy(() => import('./Components/ForYou'));
  const Collections = lazy(() => import('./Components/Collections'));
  const Account = lazy(() => import('./Components/Account'));
  const LoginForm = lazy(() => import('./Components/LoginForm'));
  const NotFound = lazy(() => import('./Components/NotFound'));
  const AppRouter = createBrowserRouter([
    {
      path: "/",
      element: <Layout />,
      children: [
        {
          path: "/",
          element: <Suspense fallback={<Loading/>}><Body/></Suspense>
        },
        {
          path: "/explore",
          element: <Suspense fallback={<Loading/>}><Body/></Suspense>
        },
        {
          path: "/foryou",
          element: <Suspense fallback={<Loading/>}><ForYou/></Suspense>
        },
        {
          path: "/collections",
          element: <Suspense fallback={<Loading/>}><Collections/></Suspense>
        },
        {
          path: "/account",
          element: <Suspense fallback={<Loading/>}><Account/></Suspense>
        },
        {
          path: "/login",
          element: <Suspense fallback={<Loading/>}><LoginForm/></Suspense>
        },
        { 
          path: "*", 
          element: <NotFound /> },
      ],
      errorElement: <Error/>,
    }
  ]);
  return (
    <div className="App">
      <RouterProvider router={AppRouter} />
    </div>
  )
}

export default App;
