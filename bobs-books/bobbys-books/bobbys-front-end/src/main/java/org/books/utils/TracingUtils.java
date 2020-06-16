// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public class TracingUtils {

	static {
		String tracingHost = System.getProperty("TRACING_HOST", "jaeger-collector.istio-system");
		String tracingPort = System.getProperty("TRACING_PORT", "14268");
		String tracingPath = System.getProperty("TRACING_PATH", "api/traces");
		tracingEndPoint = String.format("http://%s:%s/%s", tracingHost, tracingPort, tracingPath);
	}

	private static Logger logger = LoggerFactory.getLogger(TracingUtils.class);
	private static final String tracingEndPoint;

	public static Tracer getTracer() {
		logger.info("[front end] getTracer...");
		Configuration config = new Configuration("bobbys-front-end");
		Configuration.SenderConfiguration senderConfiguration = new Configuration.SenderConfiguration()
				.withEndpoint(tracingEndPoint);
		Configuration.ReporterConfiguration reporterConfiguration = new Configuration.ReporterConfiguration()
				.withFlushInterval(1000).withMaxQueueSize(65000).withSender(senderConfiguration);
		config = config.withReporter(reporterConfiguration)
				.withSampler(new Configuration.SamplerConfiguration().withType("const").withParam(1));
		JaegerTracer.Builder builder = config.getTracerBuilder();
		B3TextMapCodec b3Injector = new B3TextMapCodec.Builder().withBaggagePrefix("baggage-").build();
		B3TextMapCodec b3Extractor = new B3TextMapCodec.Builder().withBaggagePrefix("baggage-").build();
		builder.registerInjector(Format.Builtin.HTTP_HEADERS, b3Injector);
		builder.registerExtractor(Format.Builtin.HTTP_HEADERS, b3Extractor);
		return builder.build();
	}

	private final static String[] tracingHeaderKeys = { //
			"x-request-id", //
			"x-b3-traceid", //
			"x-b3-spanid", //
			"x-b3-parentspanid", //
			"x-b3-sampled", //
			"x-b3-flags", //
			"x-ot-span-context" //
	};

	public static Span buildSpan(String operationName, HttpServletRequest httpRequest) {
		Tracer tracer = GlobalTracer.get();
		final Map<String, String> headers = new HashMap<>();
		if (httpRequest != null) {
			headers.putAll(extractTracingHeadersFromRequest(httpRequest));
		}

		Tracer.SpanBuilder spanBuilder;
		try {
			TextMap carrier = new TextMapAdapter(headers);
			SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, carrier);
			if (parentSpan == null) {
				spanBuilder = tracer.buildSpan(operationName);
			} else {
				spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpan);
			}
		} catch (IllegalArgumentException e) {
			spanBuilder = tracer.buildSpan(operationName);
		}
		return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT).start();
	}


	private static Map<String, String> extractTracingHeadersFromRequest(HttpServletRequest httpRequest) {
		Map<String, String> tracingHeaders = new HashMap<>();
		for (String key : tracingHeaderKeys) {
			String headerValue = httpRequest.getHeader(key);
			if (headerValue != null && !headerValue.isEmpty()) {
				tracingHeaders.put(key, headerValue);
				logger.info("extractTracingHeadersFromRequest put" + " key:" + key + " value:" + headerValue);
			}
		}
		return tracingHeaders;
	}

	public static void finishTrace(Scope scope) {
		GlobalTracer.get().activeSpan().finish();
		scope.close();
	}

	public static Scope activateSpan(Span span) {
		return GlobalTracer.get().activateSpan(span);
	}


	public static Tracer tracer() {
		return GlobalTracer.get();
	}

	
	public static ApacheHttpRequestBuilderCarrier apacheHttpRequestBuilderCarrier(HttpMessage request) {
		return new ApacheHttpRequestBuilderCarrier(request);
	}

	public static class ApacheHttpRequestBuilderCarrier implements io.opentracing.propagation.TextMap {
		private final HttpMessage httpRequestMethod;

		ApacheHttpRequestBuilderCarrier(HttpMessage httpRequestMethod) {
			this.httpRequestMethod = httpRequestMethod;
		}

		@Override
		public Iterator<Map.Entry<String, String>> iterator() {
			throw new UnsupportedOperationException("carrier is write-only");
		}

		@Override
		public void put(String key, String value) {
			httpRequestMethod.addHeader(key, value);
		}

	}

}
