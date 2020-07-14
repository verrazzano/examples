// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.bobby;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import io.opentracing.Scope;
import io.opentracing.Span;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/** Provider for greeting message. */
@ApplicationScoped
public class BookStore {

  @Inject io.opentracing.Tracer tracer;

  private static final Logger logger = Logger.getLogger(BookStore.class.getName());

  private NamedCache<String, Book> books;

  @PostConstruct
  public void postConstruct() {
    logger.info("postConstruct called");
    books = CacheFactory.getCache("books");
  }

  public Collection<Book> getRange(int start, int end) {
    try (Scope scope = tracer.buildSpan("get-books").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.getAll(keys).values();");
      Collection<String> keys =
          Stream.iterate(start, n -> n + 1)
              .limit(end - start)
              .map(i -> Integer.toString(i))
              .collect(Collectors.toList());
      try {
        return books.getAll(keys).values();
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  public Optional<Book> find(String id) {
    try (Scope scope = tracer.buildSpan("get-book").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.get(id);");
      try {
        return Optional.ofNullable(books.get(id));
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  public void store(Book book) {
    try (Scope scope = tracer.buildSpan("put-book").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.put(book.getBookId(), book);");
      try {
        books.put(book.getBookId(), book);
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  public void remove(String id) {
    try (Scope scope = tracer.buildSpan("remove-book").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence  books.remove(id);");
      try {
        books.remove(id);
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }
}
