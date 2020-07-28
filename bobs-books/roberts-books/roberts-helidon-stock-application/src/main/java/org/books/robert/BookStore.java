// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.ContinuousQueryCache;
import com.tangosol.util.Aggregators;
import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.function.Remote;
import io.opentracing.Scope;
import io.opentracing.Span;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.metrics.annotation.Gauge;

/** Provider for greeting message. */
@ApplicationScoped
public class BookStore {
  private static final Remote.Comparator<Book> BOOK_RATING_COMPARATOR =
      Remote.Comparator.comparing(Book::getAverageRating).reversed();
  private static final Remote.Comparator<Book> BOOK_TITLE_COMPARATOR =
      Remote.Comparator.comparing(Book::getTitle);

  @Inject io.opentracing.Tracer tracer;

  private NamedCache<String, Book> books;
  private NamedCache<Long, String> orders;

  private static final Logger logger = Logger.getLogger(BookStore.class.getName());

  public BookStore() {}

  @PostConstruct
  public void postConstruct() {
    logger.info("postConstruct called");

    NamedCache<String, Book> books = CacheFactory.getCache("books");
    NamedCache<Long, String> orders = CacheFactory.getCache("orders");

    this.books = new ContinuousQueryCache<>(books, AlwaysFilter.INSTANCE());
    this.books.addIndex(Book::getAuthors, false, null);
    this.orders = orders;
  }

  Collection<String> getDistinctAuthors() {
    try (Scope scope = tracer.buildSpan("get-distinct-authors").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.aggregate(Aggregators.distinctValues(Book::getAuthors)))");
      try {
        return books.aggregate(Aggregators.distinctValues(Book::getAuthors));
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  List<BookCount> getBookCountByAuthor(int minCount) {
    try (Scope scope = tracer.buildSpan("get-book-count-by-author").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.aggregate(Aggregators.grouping(Book::getAuthors,...)");
      try {
        return books.aggregate(Aggregators.grouping(Book::getAuthors, Aggregators.count()))
            .entrySet().stream()
            .map(e -> new BookCount(e.getKey(), e.getValue()))
            .filter(bc -> bc.getCount() >= minCount)
            .sorted(Comparator.comparing(BookCount::getCount).reversed())
            .collect(Collectors.toList());
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  Collection<Book> getTopRatedBooks(int count) {
    try (Scope scope = tracer.buildSpan("get-top-rated-books").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.AlwaysFilter.INSTANCE(), BOOK_RATING_COMPARATOR,...)");
      try {
        return books.values(AlwaysFilter.INSTANCE(), BOOK_RATING_COMPARATOR).stream()
            .filter(book -> !book.getImageUrl().contains("/nophoto/"))
            .limit(count)
            .collect(Collectors.toList());
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  Collection<Book> getBooksByAuthor(String author) {
    try (Scope scope = tracer.buildSpan("get-books-by-author").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.AlwaysFilter.INSTANCE(), BOOK_TITLE_COMPARATOR,...)");
      try {
        return books.values(authorFilter(author), BOOK_TITLE_COMPARATOR).stream()
            .filter(book -> !book.getImageUrl().contains("/nophoto/"))
            .collect(Collectors.toList());
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  Optional<Book> find(String id) {
    try (Scope scope = tracer.buildSpan("get-book-by-id").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.get(id));");
      try {
        return Optional.ofNullable(books.get(id));
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  void store(Book book) {
    try (Scope scope = tracer.buildSpan("store-book").startActive(true)) {
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

  void remove(String id) {
    try (Scope scope = tracer.buildSpan("remove-book").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence books.remove(id);");
      try {
        books.remove(id);
      } catch (Throwable t) {
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  void submitOrder(Long id, String jsonOrder) {
    System.out.println("submitOrder in BookStore.java");
    try (Scope scope = tracer.buildSpan("submit-order").startActive(true)) {
      Span span = scope.span();
      span.setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_COHERENCE);
      span.log("Calling Coherence orders.put(id, jsonOrder)");
      System.out.println("Calling Coherence orders.put(id, jsonOrder)");
      try {
        orders.put(id, jsonOrder);
      } catch (Throwable t) {
        System.out.println("Error calling order");
        TraceUtils.logThrowable(span, t);
        throw t;
      }
    }
  }

  @Gauge(unit = "count")
  public int pendingOrders() {
    return orders.size();
  }

  private Filter authorFilter(String author) {
    return Filters.like(Book::getAuthors, "%" + author + "%");
  }

  public static class BookCount {
    private final String author;
    private final int count;

    BookCount(String author, int count) {
      this.author = author;
      this.count = count;
    }

    public String getAuthor() {
      return author;
    }

    public int getCount() {
      return count;
    }
  }
}
