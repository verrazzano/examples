// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.*;
import javax.servlet.http.HttpServletRequest;

import static org.books.utils.TracingUtils.*;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class BooksBean implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(BooksBean.class);

    private List<Book> books;

    private String start;
    private String end;

	@Inject
	private HttpServletRequest servletRequest;

    public void find() {
		Span tracingSpan = buildSpan("BooksBean.find", servletRequest);
		Scope scope = tracerPreprocessing(tracingSpan);

		logger.info("In find(), with start=" + start + " and end=" + end);

        if (start == null) {
            start = "1";
        }

        if (end == null) {
            end = "13";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String hostname = System.getenv("HELIDON_HOSTNAME");
            String port = System.getenv("HELIDON_PORT");


			HttpGet httpget = new HttpGet("http://" + hostname + ":" + port + "/books?start=" + start + "&end=" + end);

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
            JsonArray jbooks = reader.readArray();
			// logger.info("after json reader " + jbooks.toString());

            books = new ArrayList<Book>();

            for (int i = 0; i < jbooks.size(); i++) {
                JsonObject jbook = jbooks.getJsonObject(i);

                Book book = new Book();
                book.setBookId(((JsonString) jbook.get("bookId")).getString());
                book.setAuthors(((JsonString) jbook.get("authors")).getString());
                book.setTitle(((JsonString) jbook.get("title")).getString());
                book.setImageUrl(((JsonString) jbook.get("imageUrl")).getString());
                books.add(book);
            }

        } catch (Exception e) {
			logger.error("Error fetching books\n", e);
        }
		if (books != null) {
			tracer().activeSpan().log(String.format("Found %d books", books.size()));
		}
		finishTrace(scope);

    }

	private Scope tracerPreprocessing(Span tracingSpan) {
		tracingSpan.setTag("start", start);
		tracingSpan.setTag("end", end);
		return activateSpan(tracingSpan);

	}

	public void previous() {
        int s = Integer.parseInt(getStart()) - 12;
        if (s < 0) s = 0;

        int e = s + 12;
        setStart(Integer.toString(s));
        setEnd(Integer.toString(e));
		logger.info("previous: s=" + getStart() + " e=" + getEnd());
        reload();
    }

    public void next() {
        int s = Integer.parseInt(getStart()) + 12;
        int e = s + 12;
        setStart(Integer.toString(s));
        setEnd(Integer.toString(e));
		logger.info("next: s=" + getStart() + " e=" + getEnd());
        reload();
    }

    public List<Book> getBooks() {
        return books;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    private void reload() {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ec.getRequest();
            String url = "https://"+request.getServerName().toString()+request.getRequestURI().toString();
            ec.redirect(url);
        } catch (Exception e) {
			logger.error("failed to refresh page", e);
        }
    }

}
