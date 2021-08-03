import React, {Component} from "react";
import AuthenticationService from "./../api/AuthenticationService";
import Cookies from "js-cookie"

class SignInSignUp extends Component {
    constructor() {
        super();

        this.signIn = this.signIn.bind(this)
        this.signUp = this.signUp.bind(this)
    }

    render() {
        return (
            <div className="SignInSignUp">
                <div className="login-wrap">
                    <div className="login-html">
                        <input id="tab-1" type="radio" name="tab" className="sign-in" defaultChecked={true}/>
                        <label htmlFor="tab-1" className="tab">Sign In</label>
                        <input id="tab-2" type="radio" name="tab" className="sign-up"/>
                        <label htmlFor="tab-2" className="tab">Sign Up</label>
                        <div className="login-form">
                            {/***SIGN IN***/}
                            <div className="sign-in-htm">
                                <div className="group">
                                    <label htmlFor="userSignIn" className="label">Username</label>
                                    <input id="userSignIn" type="text" className="input"/>
                                </div>
                                <div className="group">
                                    <label htmlFor="passSignIn" className="label">Password</label>
                                    <input id="passSignIn" type="password" className="input" data-type="password"/>
                                </div>
                                <div className="group">
                                    <input id="check" type="checkbox" className="check"/>
                                    <label htmlFor="check"><span className="icon"></span> Keep me Signed in</label>
                                </div>
                                <div className="group">
                                    <input type="submit" className="button" onClick={this.signIn} value="Sign In"/>
                                </div>
                                <div className="hr"></div>
                                <div className="foot-lnk">
                                    <a href="#forgot">Forgot Password?</a>
                                </div>
                            </div>
                            {/***SIGN UP***/}
                            <div className="sign-up-htm">
                                <div className="group">
                                    <label htmlFor="userSignUp" className="label">Username</label>
                                    <input id="userSignUp" type="text" className="input"/>
                                </div>
                                <div className="group">
                                    <label htmlFor="passSignUp" className="label">Password</label>
                                    <input id="passSignUp" type="password" className="input" data-type="password"/>
                                </div>
                                <div className="group">
                                    <label htmlFor="passReSignUp" className="label">Repeat Password</label>
                                    <input id="passReSignUp" type="password" className="input" data-type="password"/>
                                </div>
                                {/*<div className="group">*/}
                                {/*    <label htmlFor="pass" className="label">Email Address</label>*/}
                                {/*    <input id="pass" type="text" className="input"/>*/}
                                {/*</div>*/}
                                <div className="group">
                                    <input type="submit" className="button" onClick={this.signUp} value="Sign Up"/>
                                </div>
                                <div className="hr"></div>
                                <div className="foot-lnk">
                                    <label htmlFor="tab-1">Already Member?</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    signIn() {
        var login = document.getElementById("userSignIn").value;
        var pass = document.getElementById("passSignIn").value;
        var check = document.getElementById("check").checked;

        if (login != null && pass != null)
            if (login !== "" && pass !== "")
                AuthenticationService.executeSignIn(login, pass)
                    .then(response => {
                        AuthenticationService.successfulLogin(response.data.access_token);
                        console.log(response.data.access_token);
                        if (check) {
                            Cookies.set("token", response.data.access_token, {expires: 7})
                        }
                        this.props.history.push("/items");
                    })
                    .catch(err => console.log(err.data));
    }

    signUp() {
        var login = document.getElementById("userSignUp").value;
        var pass = document.getElementById("passSignUp").value;
        var passRe = document.getElementById("passReSignUp").value;

        if (login != null && pass != null && passRe != null)
            if (login !== "" && pass !== "" && passRe !== "")
                if (pass === passRe)
                    AuthenticationService.executeSignUp(login, pass)
                        .then(response => {
                            console.log(response.data)
                            alert("Successfully registered!")
                        })
                        .catch(err => console.log(err.data))
    }
}

export default SignInSignUp;