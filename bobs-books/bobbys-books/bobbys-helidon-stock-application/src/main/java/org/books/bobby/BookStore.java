// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.bobby;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import io.opentracing.Scope;
import io.opentracing.Span;
import java.util.Collection;
import java.util.ArrayList;
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
  private boolean initDone;

  /**
   * Init the caches, there will be a period of time during startup where
   * the coherence cluster is not ready and this will fail.  This just needs
   * to be called before each cache access to make sure the caches are ready.
   */
  public synchronized boolean init() {
    if (initDone) return true;
    logger.info("initializing caches");
    try {
      books = CacheFactory.getCache("books");
      if (books == null) {
        logger.info("Coherence books cache not ready");
        return false;
      }
      books.addIndex(Book::getAuthors, false, null);
    } catch (Throwable t) {
      logger.info("Coherence CQC not ready");
      logger.log(java.util.logging.Level.INFO, "Failed Here - ### DEBUG ", t);
      return false;
    }

    initDone = true;
    logger.info("Coherence is ready to be used");
    return true;
  }

  public Collection<Book> getRange(int start, int end) {
    try {
      Span span = tracer.buildSpan("get-books").start();
      tracer.activateSpan(span);
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.getAll(keys).values();");
      Collection<String> keys =
          Stream.iterate(start, n -> n + 1)
              .limit(end - start)
              .map(i -> Integer.toString(i))
              .collect(Collectors.toList());
      try {
        if (!init()) {
          return new ArrayList<Book>();
        }
        return books.getAll(keys).values();
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public Optional<Book> find(String id) {
    try {
      Span span = tracer.buildSpan("get-book").start();
      tracer.activateSpan(span);
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.get(id);");
      try {
        if (!init()) {
          return null;
        }
        return Optional.ofNullable(books.get(id));
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void store(Book book) {
    try {
      Span span = tracer.buildSpan("put-book").start();
      tracer.activateSpan(span);
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.put(book.getBookId(), book);");
      try {
        if (!init()) {
          throw new RuntimeException("Coherence not ready");
        }
        books.put(book.getBookId(), book);
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void remove(String id) {
    try {
      Span span = tracer.buildSpan("remove-book").start();
      tracer.activateSpan(span);
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence  books.remove(id);");
      try {
        if (!init()) {
          throw new RuntimeException("Coherence not ready");
        }
        books.remove(id);
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
