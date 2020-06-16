// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
import Autosuggest from 'react-autosuggest';

function getSuggestionValue(suggestion) {
  return suggestion;
}

function renderSuggestion(suggestion) {
  return (
    <span>{suggestion}</span>
  );
}

class SearchBox extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      value: '',
      suggestions: [],
    };

    this.onSuggestionsFetchRequested = this.onSuggestionsFetchRequested.bind(this);
    this.onSuggestionsClearRequested = this.onSuggestionsClearRequested.bind(this);
    this.onChange = this.onChange.bind(this);
  }

  shouldRenderSuggestions(value) {
    return value.trim().length > 0;
  }

  onSuggestionsFetchRequested = ({ value }) => {
    this.props.zipkinFetch('/api/authors?q=' + value)
        .then(res => res.json())
        .then(data => this.setState({suggestions: data}));
  };

  onSuggestionsClearRequested = () => {
    this.setState({
      suggestions: [],
    });
  };

  onChange = (event, { newValue, }) => {
     this.setState({
       value: newValue
     });
   };

  render() {
    const { value, suggestions } = this.state;
    const inputProps = {
      placeholder: "Enter author name",
      value,
      onChange: this.onChange
    };


    return (
      <Autosuggest
        suggestions={suggestions}
        onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
        onSuggestionsClearRequested={this.onSuggestionsClearRequested}
        onSuggestionSelected={this.props.onSuggestionSelected}
        shouldRenderSuggestions={this.shouldRenderSuggestions}
        getSuggestionValue={getSuggestionValue}
        renderSuggestion={renderSuggestion}
        inputProps={inputProps} />
    );
  }
}

export default SearchBox;
