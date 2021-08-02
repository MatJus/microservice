import React, {Component} from 'react';
import {BrowserRouter as Router, Link, Route, Switch} from 'react-router-dom';
import Home from './HomePage'
import SignInSignUp from './SignInSignUp'
import './../css/bootstrap.min.css'
import './../css/style.css'

class MainMicroserviceApp extends Component {
    render() {
        return (
            <div className="MainMicroserviceApp">
                <Router>
                        <HeaderComponent/>
                        <div className="bg">
                        <Switch>
                            <Route path="/" exact component={Home}/>
                            <Route path="/signinsignup" exact component={SignInSignUp}/>
                            <Route path="/" exact component={Home}/>
                        </Switch>
                        </div>
                </Router>
            </div>
        );
    }
}

class HeaderComponent extends Component {
    render() {
        return (
            <header>
                <nav className="navbar navbar-expand navbar-light bg-light">
                    <ul className="navbar-nav">
                        <li><Link className="nav-link" to="/">Home</Link></li>
                    </ul>
                    <ul className="navbar-nav navbar-collapse justify-content-end">
                        <li><Link className="nav-link" to="/signinsignup">Login</Link></li>
                        <li><Link className="nav-link" to="/logout">Logout</Link></li>
                    </ul>
                </nav>
            </header>
        );
    }
}

export default MainMicroserviceApp;