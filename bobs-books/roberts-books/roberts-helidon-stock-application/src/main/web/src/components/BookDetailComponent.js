// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
import {Card, CardImg, CardText, CardSubtitle, CardBody, CardTitle, Breadcrumb, BreadcrumbItem, Button} from 'reactstrap';
import {Link} from 'react-router-dom';

function RenderBook(props) {
  const book = props.book;
  const cart = props.cart
  const onCartChanged = props.onCartChanged;

  function addToCart(cart, book, onCartChanged) {
    cart.push(book);
    onCartChanged()
  }

  return (
    <div className="col-12 col-md-4 m-1">
      <Card>
        <CardImg width="100%" src={book.largeImageUrl} alt={book.originalTitle}/>
        <CardBody>
          <CardTitle><strong>{book.originalTitle}</strong></CardTitle>
          <CardSubtitle><em>{book.authors}</em></CardSubtitle>
          <CardText>Rating: {book.averageRating} (based on {book.ratingsCount} reviews)</CardText>
          <Button onClick={() => addToCart(cart, book, onCartChanged)}>
            <span className="fa fa-cart-plus fa-lg"/> Add to Cart
          </Button>
        </CardBody>
      </Card>
    </div>
  )
}

function RenderComments({comments}) {
  if (comments != null) {
    const commentList = comments.map((comment) => {
      return (
        <li key={comment.id}>
          <p>{comment.comment}</p>
          <p>-- {comment.author}, {new Intl.DateTimeFormat('en-US',
            {year: 'numeric', month: 'short', day: '2-digit'})
            .format(new Date(Date.parse(comment.date)))}</p>
        </li>
      )
    })

    return (
      <div className="col-12 col-md-5 m-1">
        <h4>Comments</h4>
        <ul className="list-unstyled">{commentList}</ul>
      </div>
    )
  } else {
    return (
      <div></div>
    )
  }
}

const BookDetail = (props) => {
  if (props.book != null) {
    return (
      <div className="container">
        <div className="row">
          <Breadcrumb>
            <BreadcrumbItem><Link to="/books">Books</Link></BreadcrumbItem>
            <BreadcrumbItem active>{props.book.originalTitle}</BreadcrumbItem>
          </Breadcrumb>
          <div className="col-12">
            <h3>{props.book.originalTitle}</h3>
          </div>
        </div>
        <div className="row">
          <RenderBook book={props.book} cart={props.cart}  onCartChanged={props.onCartChanged}/>
          <RenderComments comments={props.comments}/>
        </div>
      </div>
    )
  } else {
    return (
      <div></div>
    )
  }
}

export default BookDetail;
