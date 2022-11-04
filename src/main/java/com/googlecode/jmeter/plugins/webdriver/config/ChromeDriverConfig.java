package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeDriverConfig extends WebDriverConfig<ChromeDriver> {

    private static final long serialVersionUID = 100L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverConfig.class);
    private static final String ADDITIONAL_ARGS = "ChromeDriverConfig.additional_args";
    private static final String BINARY_PATH = "ChromeDriverConfig.binary_path";

    private static final Map<String, ChromeDriverService> services = new ConcurrentHashMap<String, ChromeDriverService>();

    public void setBinaryPath(String binaryPath) {
        setProperty(BINARY_PATH, binaryPath);
    }

    public String getBinaryPath() {
        return getPropertyAsString(BINARY_PATH);
    }

    ChromeOptions createOptions() {
        ChromeOptions options = new ChromeOptions();

        // Custom Chrome capabilities
        // Arguments
        final String additionalArgs = trimmed(getAdditionalArgs());
        if(null != additionalArgs && !additionalArgs.isEmpty()) {
            options.addArguments(additionalArgs.split("\\s+"));
        }

        // Starting browser in a specified location
        final String binaryPath = trimmed(getBinaryPath());
        if(null != binaryPath && !binaryPath.isEmpty()) {
            options.setBinary(binaryPath);
        }

        // Capabilities shared by all browsers
        if(isAcceptInsecureCerts()) {
            options.setCapability("acceptInsecureCerts", true);
        }
        options.setCapability(CapabilityType.PROXY, createProxy());

        return options;
    }

    private String trimmed(String str) {
        return null == str ? null : str.trim();
    }

    Map<String, ChromeDriverService> getServices() {
        return services;
    }

    @Override
    protected ChromeDriver createBrowser() {
        final ChromeDriverService service = getThreadService();
        ChromeOptions options = createOptions();
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

    public String getAdditionalArgs() {
        return getPropertyAsString(ADDITIONAL_ARGS);
    }

    public void setAdditionalArgs(String additionalArgs) {
        setProperty(ADDITIONAL_ARGS, additionalArgs);
    }

}
