// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.filter;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import org.books.utils.TracingUtils;

import io.opentracing.Tracer;
import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;
import io.opentracing.util.GlobalTracer;

@WebListener
public class OpenTracingContextInitializer implements javax.servlet.ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		Tracer tracer = TracingUtils.getTracer();
		GlobalTracer.registerIfAbsent(tracer);
		ServletContext servletContext = servletContextEvent.getServletContext();
		Dynamic filterRegistration = servletContext.addFilter("jaxrs-spanFinishingFilter", new SpanFinishingFilter());
		filterRegistration.setAsyncSupported(true);
		filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false,
				"*");
		servletContext.addFilter("servlet-tracingFilter", new TracingFilter(tracer));
	}
}
