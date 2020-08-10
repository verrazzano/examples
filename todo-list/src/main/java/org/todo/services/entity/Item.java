package org.todo.services.entity;

import javax.json.Json;
import javax.json.JsonObject;

public class Item {
  private int key;
  private String description;
  private boolean complete;

  public Item(int id) {
    key = id;
    complete = false;
  }

  public int id() {
    return key;
  }

  public String desc() {
    return description;
  }

  public Item desc(String value) {
    description = value;
    return this;
  }

  public boolean done() {
    return complete;
  }

  public Item done(boolean value) {
    complete = value;
    return this;
  }

  public JsonObject toJson() {
    return Json.createObjectBuilder()
        .add("id", id())
        .add( "description", desc())
        .add( "done", done())
        .build();
  }
}
