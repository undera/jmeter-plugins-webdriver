package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternetExplorerDriverConfig extends WebDriverConfig<InternetExplorerDriver> {

    private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverConfig.class);

    private static final String IE_SERVICE_PATH = "InternetExplorerDriverConfig.iedriver_path";
    private static final String EDGE_SERVICE_PATH = "InternetExplorerDriverConfig.edgedriver_path";
    private static final Map<String, InternetExplorerDriverService> services = new ConcurrentHashMap<String, InternetExplorerDriverService>();

    public void setInternetExplorerDriverPath(String path) {
        setProperty(IE_SERVICE_PATH, path);
    }

    public String getInternetExplorerDriverPath() {
        return getPropertyAsString(IE_SERVICE_PATH);
    }

    public void setMsEdgeDriverPath(String path) {
        setProperty(EDGE_SERVICE_PATH, path);
    }

    public String getMsEdgeDriverPath() {
        return getPropertyAsString(EDGE_SERVICE_PATH);
    }

    InternetExplorerOptions createOptions() {
    	InternetExplorerOptions options = new InternetExplorerOptions();
    	options.setCapability(CapabilityType.PROXY, createProxy());
    	options.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
    	// Settings to launch Microsoft Edge in IE mode
    	options.attachToEdgeChrome();
        options.withEdgeExecutablePath(getMsEdgeDriverPath());
    	options.ignoreZoomSettings();
        // To avoid driver construction exception in environments where protected mode is not properly set
        options.introduceFlakinessByIgnoringSecurityDomains();
    	// Set an initial valid page otherwise IeDriver hangs on page load...
    	options.withInitialBrowserUrl("http://www.bing.com");
        return options;
    }

    Map<String, InternetExplorerDriverService> getServices() {
        return services;
    }

    @Override
    protected InternetExplorerDriver createBrowser() {
        final InternetExplorerDriverService service = getThreadService();
    	InternetExplorerOptions ieOptions = createOptions();
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
            service = new InternetExplorerDriverService.Builder().usingDriverExecutable(new File(getInternetExplorerDriverPath())).build();
            service.start();
            services.put(currentThreadName(), service);
        } catch (IOException e) {
            LOGGER.error("Failed to start IE service");
            service = null;
        }
        return service;
    }
}