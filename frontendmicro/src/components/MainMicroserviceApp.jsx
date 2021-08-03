import React, {Component} from "react";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Home from "./HomePage";
import SignInSignUp from "./SignInSignUp";
import "./../css/bootstrap.min.css"
import "./../css/style.css"
import AuthenticatedRoute from "./AuthenticatedRoute";
import HeaderComponent from "./HeaderComponent";
import ItemsComponent from "./ItemsComponent";

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
                            <AuthenticatedRoute path="/items" exact component={ItemsComponent}/>
                        </Switch>
                    </div>
                </Router>
            </div>
        );
    }
}

export default MainMicroserviceApp;