// Copyright (c) 2020 Oracle and/or its affiliates.

import React, { Component } from 'react';
import { Badge, Navbar, NavbarBrand, Nav, NavItem, Jumbotron } from 'reactstrap';
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
        <Navbar dark expand="md">
          <div className="container">
            <NavbarBrand className="mr-auto" href="/">
              <img src="assets/images/logo.png" height="41" width="41" alt="Robert's Books"/>
            </NavbarBrand>
            <Nav navbar>
              <NavItem>
                <NavItem>
                  <SearchBox onSuggestionSelected={(event, params) => this.props.onAuthorSelected(event, params)}/>
                </NavItem>
              </NavItem>
            </Nav>
            <Nav className="ml-auto" navbar>
              <NavItem>
                <NavLink className="nav-link" to="/cart">
                  <span className="fa fa-shopping-cart fa-lg"/> Cart <Badge pill>{this.state.cart.length}</Badge>
                </NavLink>
              </NavItem>
            </Nav>
          </div>
        </Navbar>
        <Jumbotron>
          <div className="container">
            <div className="row row-header">
              <div className="col-12 col-sm-6">
                <h1>Robert's Books</h1>
                <p>We take inspiration from the World's best writers.</p>
              </div>
            </div>
          </div>
        </Jumbotron>
      </React.Fragment>
    )
  }
}

export default Header;
