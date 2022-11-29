package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeDriverConfig extends WebDriverConfig<ChromeDriver> {

	private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverConfig.class);

	private static final Map<String, ChromeDriverService> services = new ConcurrentHashMap<String, ChromeDriverService>();
	Map<String, ChromeDriverService> getServices() {
		return services;
	}

	@Override
	protected ChromeDriver createBrowser() {
		final ChromeDriverService service = getThreadService();
		ChromeOptions options = createChromeOptions();
		return service != null ? new ChromeDriver(service, options) : null;
	}

	@Override
	public void quitBrowser(final ChromeDriver browser) {
		super.quitBrowser(browser);
		final ChromeDriverService service = services.remove(currentThreadName());
		if (service != null && service.isRunning()) {
			service.stop();
		}
	}

	private ChromeDriverService getThreadService() {
		ChromeDriverService service = services.get(currentThreadName());
		if (service != null) {
			return service;
		}
		try {
			service = new ChromeDriverService.Builder().usingDriverExecutable(new File(getDriverPath())).build();
			service.start();
			services.put(currentThreadName(), service);
		} catch (IOException e) {
			LOGGER.error("Failed to start chrome service");
			service = null;
		}
		return service;
	}
}
