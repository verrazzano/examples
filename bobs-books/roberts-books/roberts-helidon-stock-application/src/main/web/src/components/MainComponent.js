// Copyright (c) 2020 Oracle and/or its affiliates.

import React, {Component} from 'react';
import Books from './BooksComponent';
import Cart from './CartComponent';
import Header from './HeaderComponent';
import Footer from './FooterComponent';
import BookDetail from './BookDetailComponent';
import {Switch, Route, Redirect} from 'react-router-dom';

class Main extends Component {
  constructor(props) {
    super(props);

    this.state = {
      books: [],
      cart: [],
      author: '',
      cartListener: null,
    };

    this.onAuthorSelected = this.onAuthorSelected.bind(this);
    this.cartAddListener = this.cartAddListener.bind(this);
    this.onCartChanged = this.onCartChanged.bind(this);
  }

  cartAddListener(listener) {
    this.setState({cartListener: listener});
  }

  onCartChanged() {
    this.state.cartListener()
  }

  onAuthorSelected(event, {suggestion}) {
    const author = suggestion;
    const query = author ? "author=" + author : "count=12";
    this.props.zipkinFetch('/api/books?' + query)
    .then(response => response.json())
    .then(books => this.setState({books: books}));
  }

  componentDidMount() {
    const query = this.state.author ? "author=" + this.state.author
      : "count=12";

    this.props.zipkinFetch('/api/books?' + query)
    .then(response => response.json())
    .then(books => this.setState({books: books}));
  }

  render() {
    const BookWithId = ({match}) => {
      return (
        <BookDetail book={this.state.books.filter(
          (book) => book.bookId === match.params.bookId)[0]}
                    cart={this.state.cart}
                    onCartChanged={this.onCartChanged}
                    zipkinFetch={this.props.zipkinFetch}
                    zipkinAxios={this.props.zipkinAxios}/>
      )
    };

    return (
      <div>
        <Header cart={this.state.cart}
                cartAddListener={this.cartAddListener}
                onAuthorSelected={this.onAuthorSelected}
                zipkinFetch={this.props.zipkinFetch}
                zipkinAxios={this.props.zipkinAxios} className="sticky-header"/>
        <Switch>
          <Route exact path="/books"
                 component={() => <Books books={this.state.books}
                                         cart={this.state.cart}
                                         onCartChanged={this.onCartChanged}
                                         zipkinFetch={this.props.zipkinFetch}
                                         zipkinAxios={this.props.zipkinAxios}/>}/>
          <Route exact path="/cart"
                 component={() => <Cart cart={this.state.cart}
                                        onCartChanged={this.onCartChanged}
                                        zipkinFetch={this.props.zipkinFetch}
                                        zipkinAxios={this.props.zipkinAxios}/>}/>
          <Route path="/books/:bookId" component={BookWithId}/>
          <Redirect to="/books"/>
        </Switch>
        <Footer cart={this.state.cart}
                zipkinFetch={this.props.zipkinFetch}
                zipkinAxios={this.props.zipkinAxios} className="sticky-footer"/>
      </div>
    );
  }
}

export default Main;
