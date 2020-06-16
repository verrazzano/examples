// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.books.bobby.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.propagation.Format.Builtin;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.servlet.http.HttpServletRequest;

import static org.books.utils.TracingUtils.*;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

@Named
@SessionScoped
public class BookBean implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(BookBean.class);

    private Book book;

    private String bookId;

	@Inject
	private HttpServletRequest servletRequest;

    public void find() {
		Span tracingSpan = buildSpan("BookBean.find", servletRequest);
		Scope scope = tracerPreprocessing(tracingSpan);

		logger.info("In find(), with bookId=" + bookId);

        if (bookId == null) {
            bookId = "2";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String hostname = System.getenv("HELIDON_HOSTNAME");
            String port = System.getenv("HELIDON_PORT");

            HttpGet httpget = new HttpGet("http://" + hostname + ":" +
                    port + "/books/" + bookId);

			logger.info("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
			tracer().inject(tracer().activeSpan().context(), Builtin.HTTP_HEADERS,
					apacheHttpRequestBuilderCarrier(httpget));
            String responseBody = httpClient.execute(httpget, responseHandler);
			// logger.info("raw response body " + responseBody);

            JsonReader reader = Json.createReader(new StringReader(responseBody));
            JsonObject jbook = reader.readObject();
			// logger.info("after json reader " + jbook.toString());

            book = new Book();
            book.setBookId(bookId);
            book.setAuthors(((JsonString) jbook.get("authors")).getString());
            book.setTitle(((JsonString) jbook.get("title")).getString());
            book.setImageUrl(((JsonString) jbook.get("imageUrl")).getString());
        } catch (Exception e) {
			logger.error("Error fetching book\n", e);
		}
		if (book != null) {
			tracer().activeSpan().log(String.format("Found book: %s", book.toString()));
		}

		logger.info("Got book:" + book.toString());
		finishTrace(scope);
    }

	private Scope tracerPreprocessing(Span tracingSpan) {
		tracingSpan.setTag("bookId", bookId);
		return activateSpan(tracingSpan);

	}

	public String getTitle() {
        return book.getTitle();
    }

    public String getAuthors() {
        return book.getAuthors();
    }

    public String getImageUrl() {
        return book.getImageUrl();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
