// Copyright (c) 2020 Oracle and/or its affiliates.

package org.books.robert;

import io.opentracing.Span;
import java.io.PrintWriter;
import java.io.StringWriter;

public class TraceUtils {

  public static String TAG_CONNECTION = "connection";
  public static String TAG_COHERENCE = "coherence";
  public static String TAG_BOB = "bobs-bookstore";

  static void logThrowable(Span span, Throwable t) {
    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    span.log(sw.toString());
  }
}
