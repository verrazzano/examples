// Copyright (c) 2020 Oracle and/or its affiliates.

import React from 'react';
import {Link} from 'react-router-dom';

function Footer(props) {
  return (
    <div className="footer">
      <div>
        <div className="oj-flex">
          <div className="oj-flex-item oj-sm-4">
            <h5 className="oj-sm-padding-4x">Links</h5>
            <ul className="list-unstyled">
              <li><Link to="/books">Home</Link></li>
              <li><Link to="/books">Books</Link></li>
              <li><Link to="/cart">Check Out</Link></li>
            </ul>
          </div>
          <div className="oj-sm-padding-4x oj-flex-item oj-sm-5">
            <h5>Our Address</h5>
            <address>
              10101 5th Avenue<br/>
              New York, New York<br/>
              <i className="fa fa-phone"></i>: (212) 555-5678<br/>
              <i className="fa fa-fax"></i>: (212) 555-4321<br/>
              <i className="fa fa-envelope"></i>: <a
              href="mailto:">bob@bobs-books.com</a>
            </address>
          </div>
          <div className="oj-sm-padding-4x oj-flex-item">
            <div className="oj-flex">
              <div className="oj-sm-1 oj-flex-item"><a className="btn btn-social-icon btn-facebook"
                  href="http://www.facebook.com/profile.php?id="><i
                  className="fa fa-facebook"></i></a></div>
                <div className="oj-sm-1 oj-flex-item"><a className="btn btn-social-icon btn-linkedin"
                  href="http://www.linkedin.com/in/"><i
                  className="fa fa-linkedin"></i></a></div>
                <div className="oj-sm-1 oj-flex-item"><a className="btn btn-social-icon btn-twitter"
                  href="http://twitter.com/"><i
                  className="fa fa-twitter"></i></a></div>
                <div className="oj-sm-1 oj-flex-item"><a className="btn btn-social-icon btn-google"
                  href="http://youtube.com/"><i
                  className="fa fa-youtube"></i></a></div>
            </div>
          </div>
        </div>
        <div className="oj-sm-padding-4x oj-flex">
          <div className="oj-flex-item">
            <p>© Copyright 2019 Robert's Books</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Footer;
