// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

import React from 'react';
import ojpanel from 'ojs/';

function RenderCard({item}) {
  return (
    <ojpanel>
      <Media left href="#">
        <Media object src={item.imageUrl} alt={item.title}/>
      </Media>
      <Media>&nbsp;&nbsp;</Media>
      <Media body>
        <Media heading>
          Our featured book
        </Media>
        <p>{item.title}</p>
        <p>author</p>
      </Media>
    </ojpanel>
  )
}

function Home(props) {
  return (
    <div className="oj-flex">
      <div className="oj-flex-item oj-sm-12">
          <RenderCard item={props.book}/>
      </div>
    </div>
  )
}

export default Home;
