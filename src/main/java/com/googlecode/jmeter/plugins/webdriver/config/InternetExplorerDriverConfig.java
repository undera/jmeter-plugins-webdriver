package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
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
    private static final String FILE_UPLOAD_DIALOG_TIMEOUT = "InternetExplorerDriverConfig.file_upload_dialog_timeout";
    private static final String ENSURE_CLEAN_SESSION = "InternetExplorerDriverConfig.ensure_clean_session";
    private static final String IGNORE_PROTECTED_MODE = "InternetExplorerDriverConfig.ignore_protected_mode";
    private static final String SILENT = "InternetExplorerDriverConfig.silent";

    private static final Map<String, InternetExplorerDriverService> services = new ConcurrentHashMap<String, InternetExplorerDriverService>();

    public void setFileUploadDialogTimeout(int timeout) {
        setProperty(FILE_UPLOAD_DIALOG_TIMEOUT, timeout);
    }

    public int getFileUploadDialogTimeout() {
        return getPropertyAsInt(FILE_UPLOAD_DIALOG_TIMEOUT);
    }

    public boolean isEnsureCleanSession() {
        return getPropertyAsBoolean(ENSURE_CLEAN_SESSION, false);
    }

    public void setEnsureCleanSession(boolean state) {
        setProperty(ENSURE_CLEAN_SESSION, state);
    }

    public boolean isIgnoreProtectedMode() {
        return getPropertyAsBoolean(IGNORE_PROTECTED_MODE, false);
    }

    public void setIgnoreProtectedMode(boolean state) {
        setProperty(IGNORE_PROTECTED_MODE, state);
    }

    public boolean isSilent() {
        return getPropertyAsBoolean(SILENT, false);
    }

    public void setSilent(boolean state) {
        setProperty(SILENT, state);
    }

    InternetExplorerOptions createOptions() {
    	InternetExplorerOptions options = new InternetExplorerOptions();

        // Custom IE capabilities
    	// Settings to launch Microsoft Edge in IE mode
        // As of v4.5.0, IE Driver will automatically locate Edge on the system.
    	options.attachToEdgeChrome();
        options.ignoreZoomSettings();	// always set otherwise driver may throw an exception
    	// Set an initial valid page otherwise IeDriver hangs on page load...
        options.withInitialBrowserUrl("https://www.bing.com/");

        // Other options
        options.waitForUploadDialogUpTo(Duration.ofMillis(getFileUploadDialogTimeout()));
        if (isEnsureCleanSession()) {
            options.destructivelyEnsureCleanSession();
        }
        if (isIgnoreProtectedMode()) {
            options.introduceFlakinessByIgnoringSecurityDomains();
        }
        if (isSilent()) {
            options.setCapability("silent", true);
        }

        // Capabilities shared by all browsers
        if(isAcceptInsecureCerts()) {
            options.setCapability("acceptInsecureCerts", true);
        }
        options.setCapability(CapabilityType.PROXY, createProxy());

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