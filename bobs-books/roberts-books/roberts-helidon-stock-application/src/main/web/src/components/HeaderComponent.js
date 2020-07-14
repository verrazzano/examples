// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

import React, { Component } from 'react';
import { NavLink } from 'react-router-dom';
import SearchBox from './SearchBox';

class Header extends Component {
  constructor(props) {
    super(props);

    this.state = {
      cart: props.cart,
    };

    this.onCartChanged = this.onCartChanged.bind(this);
  }

  componentDidMount() {
    this.props.cartAddListener(this.onCartChanged)
  }

  onCartChanged() {
    // force re-render
    this.setState({state: this.state});
  }

  render() {
    return (
      <React.Fragment>
         <div className="navbar-dark oj-flex-bar">
         <NavLink className="nav-link" to="/">
            <div className="oj-sm-padding-4x oj-flex-bar-start" href="/">
              <img src="assets/images/logo.png" height="41" width="41" alt="Robert's Books"/>
            </div>
            </NavLink>
            <div className="oj-sm-padding-4x oj-flex-bar-middle oj-sm-justify-content-center">
              <SearchBox zipkinFetch={this.props.zipkinFetch} zipkinAxios={this.props.zipkinAxios} onSuggestionSelected={(event, params) => this.props.onAuthorSelected(event, params)}/>
            </div>

            <div className="oj-sm-padding-4x oj-flex-bar-end" to="/cart">
            <NavLink className="cart nav-link" to="/cart">
                  <span className="fa fa-shopping-cart fa-lg"/> Cart <div className="oj-badge">{this.state.cart.length}</div>
                  </NavLink>
                </div>
        </div>
       
          <div className="jumbotron oj-panel">
            <div className="container oj-flex oj-flex-items-pad">
              <div className="oj-flex-item">
                <h1 className="h1white">Robert's Books</h1>
                <p>We take inspiration from the World's best writers.</p>
              </div>
            </div>
          </div>
     
      </React.Fragment>
    )
  }
}

export default Header;
