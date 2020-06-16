// Copyright (c) 2020 Oracle and/or its affiliates.

import React, {Component} from 'react';
import {Button, Media} from 'reactstrap';


function RenderBook(props) {
  const book = props.book;
  const cart = props.cart
  const onCartChanged = props.onCartChanged
  const self = props.self

  function removeFromCart(cart, book, onCartChanged) {
    // Remove the item from the existing cart, loop backwards
    for (let i = cart.length-1; i >=0; i--) {
      if (cart[i].bookId === book.bookId) {
        cart.splice(i,1);
      }
    }
    onCartChanged()

    // Force refresh
    self.setState({state: self.state});
  }

  return (
    <Media className="row-cart">
      <Media left href="#" className="col-2">
        <Media src={book.smallImageUrl}/>
      </Media>
      <Media body className="col-8">
        <Media heading>
          {book.originalTitle}
        </Media>
      </Media>
      <Media right className="col-2">
        <Button onClick={() => {
          removeFromCart(cart, book, onCartChanged)
        }}>
          <span className="fa fa-remove fa-lg"/> Remove
        </Button>
      </Media>
    </Media>
  )
}

class Cart extends Component {
  constructor(props) {
    super(props);

    this.props = props;
    this.cart = props.cart;
    this.onCartChanged = props.onCartChanged;
  }

  render() {
    const books = this.cart.map(book => {
      return (
        <div key={book.id} className="col-12">
          <RenderBook self={this}  book={book} cart={this.cart}
                      onCartChanged={this.onCartChanged}/>
        </div>
      );
    });

    return (
      <div className="container">
        <div className="row">
          <div className="col-12">
            <h3>Shopping Cart</h3>
          </div>
        </div>
        <div className="row">
          {books}
        </div>
        <div className="row"/>
        <div className="row-button">
          <Button onClick={() => this.checkOut()}>
            <span className="fa fa-sign-out fa-lg"/> Buy Now!
          </Button>
        </div>
      </div>
    );
  }

  checkOut() {
    let cart = this.cart;

    const order = {
      books: cart.map(book => {
        return {bookId: book.bookId, title: book.originalTitle}
      }),
      customer: {
        name: 'Homer Simpson',
        street: '123 Main St',
        city: 'Springfield',
        state: 'WI',
      }
    };

    this.props.zipkinAxios
    .post('/api/orders', order,
      {headers: {'Content-Type': 'application/json'}})
    .then((response) => {
      if (response.status === 200 || response.status === 204 ) {
        // Remove all array items
        cart.splice(0, cart.length);
        this.onCartChanged()

        // force re-render
        this.setState({state: this.state});
      } else {
        console.log("Post to /api/orders failed with status "  + response.status.toString())
      }
    })
    .catch(reason => alert(reason));
  }
}

export default Cart;
