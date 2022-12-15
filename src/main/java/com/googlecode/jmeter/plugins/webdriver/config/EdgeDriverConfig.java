package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDriverConfig extends WebDriverConfig<EdgeDriver> {

	private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDriverConfig.class);

	private static final Map<String, EdgeDriverService> services = new ConcurrentHashMap<String, EdgeDriverService>();
	Map<String, EdgeDriverService> getServices() {
		return services;
	}

	@Override
	protected EdgeDriver createBrowser() {
		final EdgeDriverService service = getThreadService();
		EdgeOptions options = createEdgeOptions();
		return service != null ? new EdgeDriver(service, options) : null;
	}

	@Override
	public void quitBrowser(final EdgeDriver browser) {
		super.quitBrowser(browser);
		final EdgeDriverService service = services.remove(currentThreadName());
		if (service != null && service.isRunning()) {
			service.stop();
		}
	}

	private EdgeDriverService getThreadService() {
		EdgeDriverService service = services.get(currentThreadName());
		if (service != null) {
			return service;
		}
		try {
			service = new EdgeDriverService.Builder().usingDriverExecutable(new File(getDriverPath())).build();
			service.start();
			services.put(currentThreadName(), service);
		} catch (IOException e) {
			LOGGER.error("Failed to start edge service");
			service = null;
		}
		return service;
	}
}
