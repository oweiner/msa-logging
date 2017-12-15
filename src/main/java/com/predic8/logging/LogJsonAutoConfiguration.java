package com.predic8.logging;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class LogJsonAutoConfiguration {
	@Bean
	public Counter requests() {
		return Counter
			.build()
			.name("requests_total")
			.help("Total requests.")
			.register();
	}

	@Bean
	public Histogram latency() {
		return Histogram
			.build()
			.name("requests_latency_seconds")
			.help("Request latency in seconds.")
			.register();
	}

	@Bean
	public LogJsonHandlerInterceptor logJsonHandlerInterceptor(Counter requests, Histogram latency) {
		return new LogJsonHandlerInterceptor(requests, latency);
	}

	@Configuration
	static class WebConfig extends WebMvcConfigurerAdapter {
		private final LogJsonHandlerInterceptor logJsonHandlerInterceptor;

		WebConfig(LogJsonHandlerInterceptor logJsonHandlerInterceptor) {
			this.logJsonHandlerInterceptor = logJsonHandlerInterceptor;
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(logJsonHandlerInterceptor);
		}
	}
}