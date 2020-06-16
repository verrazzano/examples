// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class BooksApplication extends Application {
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<>();
    s.add(OrderResource.class);
    return s;
  }

}
