//import './App.css';
import MainMicroserviceApp from './components/MainMicroserviceApp';
import Cookies from "js-cookie"
import {useEffect} from "react";

function App() {

    const readCookie = () => {
        const token = Cookies.get("token");
        if (token) {
            sessionStorage.setItem("token", token);
        }
    }

    useEffect(() => {
       readCookie();
    });

  return (
    <div className="App">
      <MainMicroserviceApp/>
    </div>
  );
}

export default App;
