package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternetExplorerDriverConfig extends WebDriverConfig<InternetExplorerDriver> {

    private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverConfig.class);

    private static final Map<String, InternetExplorerDriverService> services = new ConcurrentHashMap<String, InternetExplorerDriverService>();

    Map<String, InternetExplorerDriverService> getServices() {
        return services;
    }

    @Override
    protected InternetExplorerDriver createBrowser() {
        final InternetExplorerDriverService service = getThreadService();
        InternetExplorerOptions ieOptions = createIEOptions();
        return service != null ? new InternetExplorerDriver(service, ieOptions) : null;
    }

    @Override
    public void quitBrowser(final InternetExplorerDriver browser) {
        super.quitBrowser(browser);
        final InternetExplorerDriverService service = services.remove(currentThreadName());
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }

    private InternetExplorerDriverService getThreadService() {
        InternetExplorerDriverService service = services.get(currentThreadName());
        if (service != null) {
            return service;
        }
        try {
            service = new InternetExplorerDriverService.Builder().usingDriverExecutable(new File(getDriverPath())).build();
            service.start();
            services.put(currentThreadName(), service);
        } catch (IOException e) {
            LOGGER.error("Failed to start IE service");
            service = null;
        }
        return service;
    }
}