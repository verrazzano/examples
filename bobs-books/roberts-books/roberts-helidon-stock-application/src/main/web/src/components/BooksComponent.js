// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
import {
  Button,
  Card,
  CardImg,
  CardBody,
  CardText,
  CardTitle,
  CardSubtitle
} from 'reactstrap';
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
    <Card>
      <Link to={`/books/${book.bookId}`}>
        <CardImg width="100%" src={book.largeImageUrl} alt={book.title}/>
      </Link>
      <CardBody>
        <CardTitle><strong>{book.originalTitle}</strong></CardTitle>
        <CardSubtitle><em>{book.authors}</em></CardSubtitle>
        <CardText>Rating: {book.averageRating} (based
          on {book.ratingsCount} reviews)</CardText>
        <Button onClick={() => addToCart(cart, book, onCartChanged)}>
          <span className="fa fa-cart-plus fa-lg"/> Add to Cart
        </Button>
      </CardBody>
    </Card>
  )
}

const Books = (props) => {
  const books = props.books.map((book) => {
    return (
      <div key={book.id} className="col-12 col-md-3 m-1">
        <RenderBook book={book} cart={props.cart}
                    onCartChanged={props.onCartChanged}/>
      </div>
    );
  });

  return (
    <div className="container">
      <div className="row">
        <div className="col-12">
          <h3>Books</h3>
        </div>
      </div>
      <div className="row">
        {books}
      </div>
    </div>
  );
}

export default Books;
