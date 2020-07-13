// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

const proxy = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(proxy('/api/v2/span', { target: 'http://zipkin.istio-system:9411/' }));
    //Uncomment when running in development webpack server (npm start)
    //app.use(proxy('/api', { target: 'http://localhost:8080/' }));
  };