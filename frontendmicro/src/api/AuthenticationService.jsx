import axios from "axios";
import Cookies from "js-cookie"

class AuthenticationService {

    async executeSignIn(login, pass) {
        return await axios.post("http://localhost:8888/login", {user: {login: login, password: pass}})
    }

    async executeSignUp(login, pass) {
        return await axios.post("http://localhost:8888/register", {user: {login: login, password: pass}})
    }

    async executeCreateNewItem(name) {
        const options = {headers: {Authorization: "Bearer " + sessionStorage.getItem("token")}}
        return await axios.post("http://localhost:8888/items", {item: {name: name}}, options)
    }

    async executeGetAllItems() {
        const options = {headers: {Authorization: "Bearer " + sessionStorage.getItem("token")}}
        return await axios.get("http://localhost:8888/items", options)
    }

    successfulLogin(token) {
        sessionStorage.setItem('token', token);
    }

    logout() {
        sessionStorage.removeItem('token');
        Cookies.remove("token")
    }

    isUserLoggedIn() {
        let token = sessionStorage.getItem('token');
        if(token === null) {
            return false;
        }
        return true;
    }

}

export default new AuthenticationService();