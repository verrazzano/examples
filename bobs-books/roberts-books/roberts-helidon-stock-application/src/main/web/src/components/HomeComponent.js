// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
import {Media} from 'reactstrap';

function RenderCard({item}) {
  return (
    <Media>
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
    </Media>
  )
}

function Home(props) {
  return (
    <div className="container">
      <div className="row align-items-start">
        <div className="col-12 col-md m-1">
          <RenderCard item={props.book}/>
        </div>
      </div>
    </div>
  )
}

export default Home;
