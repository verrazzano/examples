// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

import React from 'react';
import {Link} from 'react-router-dom';

function RenderBook(props) {
  const book = props.book;
  const cart = props.cart
  const onCartChanged = props.onCartChanged

  function addToCart(cart, book, onCartChanged) {
    cart.push(book);
    onCartChanged()
  }

  return (
    <div className="bookitem oj-panel oj-sample-card oj-complete oj-active" tabindex="0">
      <div className="oj-sample-profile-card-container">
        <div className="oj-sample-profile-card-inner">
          <Link to={`/books/${book.bookId}`}>
            <div className="oj-avatar oj-avatar-image oj-avatar-bg-neutral oj-avatar-xxl" aria-hidden="true">
                <div className="oj-avatar-background-image" style={{backgroundImage: `url(${book.largeImageUrl})`}}>
                </div>
            </div>
          </Link>
          <div className="oj-sample-profile-card-emp-spacer"></div>
          <div className="oj-text-primary-color oj-sample-profile-card-emp-name oj-typography-subheading-sm">
            {book.originalTitle}
          </div>
          <div className="oj-sample-profile-card-emp-spacer"></div>
          <div className="oj-text-tertiary-color oj-sample-profile-card-emp-title oj-typography-body-md">
            <i>{book.authors}</i>
          </div>
          <div className="oj-sample-profile-card-emp-spacer"></div>
          <div className="oj-text-tertiary-color oj-sample-profile-card-emp-title oj-typography-body-md">
            Rating: {book.averageRating} (based on {book.ratingsCount} reviews)
          </div>
          <div className="oj-sample-profile-card-emp-spacer"></div>
          <div className="oj-button oj-component oj-enabled oj-button-outlined-chrome oj-button-text-only oj-complete oj-default">
          <button className="btn" onClick={() => addToCart(cart, book, onCartChanged)}>
            <span className="fa fa-cart-plus fa-lg" /> Add to Cart
          </button>
          </div>
        </div>
      </div>
    </div>
  )
}

const Books = (props) => {
  const books = props.books.map((book) => {
    return (
      <div key={book.id} className="oj-flex-item oj-sm-3">
        <RenderBook book={book} cart={props.cart}
                    onCartChanged={props.onCartChanged}/>
      </div>
    );
  });

  return (
    <div className="container">
      <div className="oj-flex">
        <div className="oj-flex-item oj-sm-12">
          <h4>Books</h4>
        </div>
      </div>
      <div className="booktiles oj-flex">
      <div className="oj-flex oj-sm-12">
        {books}
        </div>
      </div>
    </div>
  );
}

export default Books;
