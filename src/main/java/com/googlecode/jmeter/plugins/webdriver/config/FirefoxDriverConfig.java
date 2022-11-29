package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxDriverConfig extends WebDriverConfig<FirefoxDriver> {

    private static final long serialVersionUID = 100L;
    private static final Logger log = LoggerFactory.getLogger(FirefoxDriverConfig.class);
    
    private static final Map<String, FirefoxDriverService> services = new ConcurrentHashMap<String, FirefoxDriverService>();

	// Used only in unit tests
	private Boolean bUnitTests = false;
	public void enableUnitTests() {
		bUnitTests = true;
	}

    Map<String, FirefoxDriverService> getServices() {
        return services;
    }

    @Override
    protected FirefoxDriver createBrowser() {
        final FirefoxDriverService service = getThreadService();
        FirefoxOptions options = createFirefoxOptions();
        return service != null ? new FirefoxDriver(service, options) : null;
    }

    private FirefoxDriverService getThreadService() {
        FirefoxDriverService service = services.get(currentThreadName());
        if (service != null) {
            return service;
        }
        try {
			if (bUnitTests) {
			    service = GeckoDriverService.createDefaultService();
			} else {
                service = new GeckoDriverService.Builder().usingDriverExecutable(new File(getDriverPath())).build();
			}
            service.start();
            services.put(currentThreadName(), service);
        } catch (IOException e) {
            log.error("Failed to start Firefox service");
            service = null;
        }
        return service;
    }
}
