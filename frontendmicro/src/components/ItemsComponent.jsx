import React, {Component} from "react";
import AuthenticationService from "../api/AuthenticationService";

class ItemComponent extends Component {
    constructor() {
        super();
        this.state = {items: []};

        this.addItem = this.addItem.bind(this);
    }

    componentDidMount() {
        if (!this.state.data) {
            AuthenticationService.executeGetAllItems()
                .then(response => {
                    this.setState({items: response.data.items});
                    console.log(response.data.items);
                    console.log(this.state.items)
                })
                .catch(err => console.log(err.data))
        }
    }

    addItem() {
        var itemName = document.getElementById("itemNameInput").value
        if (itemName != null)
            AuthenticationService.executeCreateNewItem(itemName)
                .then(response => {
                    console.log(response.data);
                    AuthenticationService.executeGetAllItems()
                        .then(response => {
                            this.setState({items: response.data.items});
                            console.log(response.data.items);
                            console.log(this.state.items)
                        })
                        .catch(err => console.log(err.data))
                })
                .catch(err => console.log(err.data))
    }

    render() {
        var items = this.state.items;
        var result = null;
        if (items.length > 0) {
            result = items.map((value, key) => {
                return (
                    <tr key={key}>
                        <td>{key + 1}</td>
                        {/*<td>{value._id}</td>*/}
                        {/*<td>{value.owner}</td>*/}
                        <td>{value.name}</td>
                    </tr>
                );
            });
        }

        return (
            <div className="ItemComponent">
                <div className="grid">
                    <div className="row justify-content-center">
                        <div className="container">
                            <table id="tab" className="table table-bordered table-striped table-dark">
                                <tbody>
                                {result}
                                </tbody>
                            </table>
                            <div className="row justify-content-center">
                                <input id="itemNameInput" placeholder="add item name here"/>
                            </div>
                            <div className="row justify-content-center">
                                <button id="btnAddItem" onClick={this.addItem}>Add item</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default ItemComponent;