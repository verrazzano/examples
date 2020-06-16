// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books;

import io.opentracing.Scope;
import io.opentracing.Span;

import io.opentracing.contrib.jaxrs2.client.ClientTracingFeature;
import io.opentracing.contrib.jaxrs2.client.TracingProperties;
import io.opentracing.propagation.Format.Builtin;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.books.bobby.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import static org.books.utils.TracingUtils.*;

@Named
@SessionScoped
public class OrderBean implements Serializable {
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());
	private static Logger logger = LoggerFactory.getLogger(OrderBean.class);

    @Inject
    private HttpServletRequest servletRequest;

    public void order() {
		logger.info("[front end] submitting order");
		logger.info("got an order: servletRequest.getHeaderNames()=" + servletRequest.getHeaderNames());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        CartBean cartBean
                = (CartBean) facesContext.getApplication()
                .getVariableResolver().resolveVariable(facesContext, "cartBean");

		logger.info("[front end] cart bean=" + cartBean.toString());
//        placeOrderWithTracingApacheHttp(cartBean);
        placeOrderWithTracing(cartBean);
    }

    private void placeOrderWithTracing(CartBean cartBean) {
        Span tracingSpan = buildSpan("OrderBean.order", servletRequest);
        Scope tracingScope = tracerPreprocessing(cartBean, tracingSpan);
        String hostname = System.getenv("HELIDON_HOSTNAME");
        String port = System.getenv("HELIDON_PORT");
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Book book : cartBean.getBooks()) {
            jab.add(Json.createObjectBuilder()
                    .add("bookId", book.getBookId())
                    .add("title", book.getTitle())
                    .build());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("customer", Json.createObjectBuilder()
                        .add("name", cartBean.getName())
                        .add("street", cartBean.getStreet())
                        .add("city", cartBean.getCity())
                        .add("state", cartBean.getState())
                        .build())
                .add("books", jab.build())
                .build();
        logger.info("[front end] post body=" + jsonObject.toString());
        // this src uses the io.opentracing.contrib.jaxrs2.client.TracingProperties.CHILD_OF property below
        // rather than this apache tracer().inject/apacheHttpRequestBuilderCarrier
        // if this works then...
        //  https://opentracing.io/guides/java/inject-extract/ should be updated to add it to "one of the following for the Inject operation"
        // if this doesnt work then https://github.com/opentracing-contrib/java-jaxrs should be studied
//  tracer().inject(tracer().activeSpan().context(), Builtin.HTTP_HEADERS, apacheHttpRequestBuilderCarrier(httpPost));
        javax.ws.rs.client.Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
                .register(ClientTracingFeature.class)
                .build();

        Response response = client.target(String.format("http://%s:%s/%s", hostname, port, "order"))
                .request()
                .header("Content-Type", "application/json")
                .property(TracingProperties.CHILD_OF, tracer().activeSpan().context()) // optional, by default new parent is inferred from span source
                .post(Entity.json(jsonObject));
        logger.info("[front end] order POST status : " + response.getStatus());
        tracerPostprocessing(tracingScope);
    }


    private void placeOrderWithTracingApacheHttp(CartBean cartBean) {
		Span tracingSpan = buildSpan("OrderBean.order", servletRequest);
		Scope tracingScope = tracerPreprocessing(cartBean, tracingSpan);
		try (CloseableHttpClient client = HttpClients.createDefault();) {
            String hostname = System.getenv("HELIDON_HOSTNAME");
            String port = System.getenv("HELIDON_PORT");

            HttpPost httpPost = new HttpPost(String.format("http://%s:%s/%s", hostname, port,"order"));
            httpPost.addHeader("Content-Type", "application/json");
            JsonArrayBuilder jab = Json.createArrayBuilder();
            for (Book book : cartBean.getBooks()) {
                jab.add(Json.createObjectBuilder()
                        .add("bookId", book.getBookId())
                        .add("title", book.getTitle())
                        .build());
            }
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("customer", Json.createObjectBuilder()
                            .add("name", cartBean.getName())
                            .add("street", cartBean.getStreet())
                            .add("city", cartBean.getCity())
                            .add("state", cartBean.getState())
                            .build())
                    .add("books", jab.build())
                    .build();
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
			logger.info("[front end] post body=" + jsonObject.toString());

			tracer().inject(tracer().activeSpan().context(), Builtin.HTTP_HEADERS,
					apacheHttpRequestBuilderCarrier(httpPost));
			CloseableHttpResponse response = client.execute(httpPost);
			logger.info("[front end] order POST response code : " + response.getStatusLine().getStatusCode());
			response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		tracerPostprocessing(tracingScope);
    }



	private Scope tracerPreprocessing(CartBean cartBean, Span span) {
		for (int i = 0; i < cartBean.getBooks().size(); i++) {
			span.setTag("bookTitle[" + "[", cartBean.getBooks().get(i).getTitle());
		}
		int orderid = new Random().nextInt(9000) + 1000;
		span.setBaggageItem("orderId", "" + orderid);
		return activateSpan(span);
    }

	private void tracerPostprocessing(Scope tracingScope) {
		finishTrace(tracingScope);
    }
}
