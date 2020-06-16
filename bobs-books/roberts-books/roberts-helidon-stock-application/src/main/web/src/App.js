// Copyright (c) 2020 Oracle and/or its affiliates.

import React, { Component } from 'react';
import Main from './components/MainComponent';
import './App.css';
import { BrowserRouter } from 'react-router-dom';
import { Tracer, ExplicitContext, BatchRecorder, jsonEncoder } from 'zipkin';
import wrapFetch from 'zipkin-instrumentation-fetch';
import wrapAxios from 'zipkin-js-instrumentation-axios'
import { HttpLogger } from 'zipkin-transport-http';
import axios from 'axios';

const localServiceName = 'roberts-frontend';
const remoteServiceName = 'roberts-helidon-stock-application';
const { JSON_V2 } = jsonEncoder;
const tracer = new Tracer({
  ctxImpl: new ExplicitContext(),
  recorder: new BatchRecorder({
    logger: new HttpLogger({
      endpoint: `/api/v2/spans`,
      jsonEncoder: JSON_V2,
      fetch,
    }),
  }),
  localServiceName: `${localServiceName}`,
});
const zipkinFetch = wrapFetch(fetch, {tracer, remoteServiceName});
const zipkinAxios = wrapAxios(axios, { tracer, localServiceName, remoteServiceName });


class App extends Component {

  render() {
    return (
      <BrowserRouter>
      <div>
        <Main zipkinFetch={zipkinFetch} zipkinAxios={zipkinAxios}/>
      </div>
    </BrowserRouter>
    );
  }
}

export default App;
