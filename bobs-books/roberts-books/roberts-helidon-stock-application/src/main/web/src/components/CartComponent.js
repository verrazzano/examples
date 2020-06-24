// Copyright (c) 2020 Oracle and/or its affiliates.

import React, {Component} from 'react';


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
    <div className="oj-sm-padding-4x-start oj-sm-padding-2x-bottom oj-flex">
      <div className="oj-flex-item oj-sm-2">
      <div class="oj-avatar oj-avatar-image oj-avatar-bg-neutral oj-avatar-sm" aria-hidden="true">
        <div className="oj-avatar-background-image" style={{backgroundImage: `url(${book.smallImageUrl})`}} aria-label={book.originalTitle}>
        </div>
        </div>
      </div>
      <div className="oj-flex-item oj-sm-8 oj-padding-sm-2x">
        <h5>
          {book.originalTitle}
        </h5>
      </div>
      <div className="oj-flex-item oj-sm-2">
      <div className="oj-flex-item oj-sm-2 oj-button oj-component oj-enabled oj-button-outlined-chrome oj-button-text-only oj-complete oj-default">
        <button className="btn" onClick={() => {
          removeFromCart(cart, book, onCartChanged)
        }}>
        
          <span className="fa fa-remove fa-lg"/> Remove
        </button>
        </div>
      </div>
    </div>
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
        <div key={book.id}>
          <RenderBook self={this}  book={book} cart={this.cart}
                      onCartChanged={this.onCartChanged}/>
        </div>
      );
    });

    return (
      <div>
        <div className="oj-sm-padding-4x-start oj-flex">
          <div className="oj-flex-item oj-sm-12">
            <h2>Shopping Cart</h2>
          </div>
        </div>
        <div className="oj-flex">
        <div className="oj-flex-item oj-sm-12">
          {books}
        </div>
        </div>
        <div className="oj-flex"/>
        <div className="oj-flex">
        <div className="oj-flex-item">
        <div className="oj-sm-padding-4x-start oj-button oj-component oj-enabled oj-button-outlined-chrome oj-button-text-only oj-complete oj-default">
          <button className="btn" onClick={() => this.checkOut()}>
            <span className="fa fa-cart-plus fa-lg" /> Buy Now!
          </button>
        </div>
        </div>
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
