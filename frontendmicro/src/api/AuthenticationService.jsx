class AuthenticationService {
    successfulLogin(token) {
        sessionStorage.setItem('token', token);
    }

    logout() {
        sessionStorage.removeItem('token');
    }

    isUserLoggedIn() {
        let token = sessionStorage.getItem('token');
        if(token === null)
            return false
        return true;
    }

}

export default AuthenticationService;