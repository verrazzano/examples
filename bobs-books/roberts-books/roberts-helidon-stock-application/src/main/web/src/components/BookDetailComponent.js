// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
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
    <div className="bookitem oj-panel oj-sample-card oj-complete oj-panel-shadow-md oj-active" tabindex="0">
      <div className="oj-sample-profile-card-container">
        <div className="oj-sample-profile-card-inner">
        <div className="oj-avatar oj-avatar-image oj-avatar-bg-neutral oj-avatar-xxl" aria-hidden="true">
            <div className="oj-avatar-background-image" style={{backgroundImage: `url(${book.largeImageUrl})`}} aria-label={book.originalTitle}>
            </div>
        </div>
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
      <div className="oj-flex-item oj-sm-12">
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
      <div>
        <div className="oj-flex">
        <div className="oj-flex-item">
          <ul className="breadcrumb">
            <li><Link to="/books">Books</Link></li>
            <li active>{props.book.originalTitle}</li>
          </ul>
          <h2 className="oj-sm-padding-4x-start">{props.book.originalTitle}</h2>
          </div>
        </div>
        <div className="oj-sm-padding-4x-start oj-flex">
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
