package com.predic8.logging;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class LogJsonHandlerInterceptor extends HandlerInterceptorAdapter {
	private final Logger log = LoggerFactory.getLogger(LogJsonHandlerInterceptor.class);
	private final Counter requests;
	private final Histogram latency;

	public LogJsonHandlerInterceptor(Counter requests, Histogram latency) {
		this.requests = requests;
		this.latency = latency;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		Histogram.Timer timer = latency.startTimer();
		request.setAttribute("timer", timer);

		requests.inc();

		Map<String, Object> entries = new HashMap<>();
		entries.put("method", request.getMethod());
		entries.put("path", request.getServletPath());

		log.info(appendEntries(entries), "{}");

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
		Histogram.Timer timer = (Histogram.Timer) request.getAttribute("timer");

		Map<String, Object> entries = new HashMap<>();
		entries.put("status_code", response.getStatus());
		entries.put("elapsed_time", timer.observeDuration());

		log.info(appendEntries(entries), "{}");
	}
}