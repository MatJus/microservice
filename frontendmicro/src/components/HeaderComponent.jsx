import React, {Component} from "react";
import AuthenticationService from "../api/AuthenticationService";
import {Link} from "react-router-dom";
import {withRouter} from "react-router"

class HeaderComponent extends Component {
    render() {
        const isUserLoggedIn = AuthenticationService.isUserLoggedIn();
        return (
            <header>
                <nav className="navbar navbar-expand navbar-light bg-light">
                    <ul className="navbar-nav">
                        <li><Link className="nav-link" to="/">Home</Link></li>
                        {isUserLoggedIn && <li><Link className="nav-link" to="/items">Items</Link></li>}
                    </ul>
                    <ul className="navbar-nav navbar-collapse justify-content-end">
                        {!isUserLoggedIn && <li><Link className="nav-link" to="/signinsignup">Login</Link></li>}
                        {isUserLoggedIn && <li><Link className="nav-link" to="/signinsignup" onClick={AuthenticationService.logout}>Logout</Link></li>}
                    </ul>
                </nav>
            </header>
        );
    }
}

export default withRouter(HeaderComponent);