package org.todo.services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.todo.services.resource.ItemResource;
import org.todo.services.resource.ItemsResource;

@ApplicationPath("/rest")
public class TodoListApplication extends Application {
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<>();
    s.add(ItemsResource.class);
    s.add(ItemResource.class);
    return s;
  }
}
