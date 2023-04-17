package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jmeter.plugins.webdriver.proxy.ProxyFactory;
import com.googlecode.jmeter.plugins.webdriver.proxy.ProxyHostPort;
import com.googlecode.jmeter.plugins.webdriver.proxy.ProxyType;

import kg.apc.jmeter.JMeterPluginsUtils;

public abstract class WebDriverConfig<T extends WebDriver> extends ConfigTestElement
		implements LoopIterationListener, ThreadListener {

	private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverConfig.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * This is the key used to store a WebDriver instance in the
	 * {@link org.apache.jmeter.threads.JMeterVariables} object.
	 */
	public static final String BROWSER = "Browser";

	// Constants for shared settings
	private static final String DRIVER_PATH = "WebDriverConfig.driver_path";
	private static final String ACCEPT_INSECURE_CERTS = "WebDriverConfig.acceptinsecurecerts";
    private static final String ENABLE_HEADLESS = "WebDriverConfig.headless";
	private static final String MAXIMIZE_WINDOW = "WebDriverConfig.maximize_browser";
	private static final String CUSTOM_CAPABILITIES = "WebDriverConfig.custom_capabilites";

	/*
	 * THE FOLLOWING CONFIGS ARE EXPERIMENTAL AND ARE SUBJECT TO CHANGE/REMOVAL.
	 */
	private static final String RECREATE_ON_ITERATION_START = "WebDriverConfig.reset_per_iteration";
	private static final String DEV_MODE = "WebDriverConfig.dev_mode";

	// Constants for Chrome
	private static final String CHROME_ADDITIONAL_ARGS = "ChromeDriverConfig.additional_args";
	private static final String CHROME_BINARY_PATH = "ChromeDriverConfig.binary_path";

	// Constants for Edge
	private static final String EDGE_ADDITIONAL_ARGS = "EdgeDriverConfig.additional_args";
	private static final String EDGE_BINARY_PATH = "EdgeDriverConfig.binary_path";

	// Constants for Firefox
    private static final String GENERAL_USERAGENT_OVERRIDE = "FirefoxDriverConfig.general.useragent.override";
    private static final String ENABLE_USERAGENT_OVERRIDE = "FirefoxDriverConfig.general.useragent.override.enabled";
    private static final String ENABLE_NTML = "FirefoxDriverConfig.network.negotiate-auth.allow-insecure-ntlm-v1";
    private static final String EXTENSIONS_TO_LOAD = "FirefoxDriverConfig.general.extensions";
    private static final String PREFERENCES = "FirefoxDriverConfig.general.preferences";

    // Constants for IE
    private static final String FILE_UPLOAD_DIALOG_TIMEOUT = "InternetExplorerDriverConfig.file_upload_dialog_timeout";
    private static final String ENSURE_CLEAN_SESSION = "InternetExplorerDriverConfig.ensure_clean_session";
    private static final String IGNORE_PROTECTED_MODE = "InternetExplorerDriverConfig.ignore_protected_mode";
    private static final String SILENT = "InternetExplorerDriverConfig.silent";
	private static final String INITIAL_IE_URL = "InternetExplorerDriverConfig.initial_browser_url";

	// Constants for Proxy
	private static final String PROXY_PAC_URL = "WebDriverConfig.proxy_pac_url";
	private static final String HTTP_HOST = "WebDriverConfig.http_host";
	private static final String HTTP_PORT = "WebDriverConfig.http_port";
	private static final String USE_HTTP_FOR_ALL_PROTOCOLS = "WebDriverConfig.use_http_for_all_protocols";
	private static final String HTTPS_HOST = "WebDriverConfig.https_host";
	private static final String HTTPS_PORT = "WebDriverConfig.https_port";
	private static final String FTP_HOST = "WebDriverConfig.ftp_host";
	private static final String FTP_PORT = "WebDriverConfig.ftp_port";
	private static final String SOCKS_HOST = "WebDriverConfig.socks_host";
	private static final String SOCKS_PORT = "WebDriverConfig.socks_port";
	private static final String NO_PROXY = "WebDriverConfig.no_proxy";
	private static final String PROXY_TYPE = "WebDriverConfig.proxy_type";

	/**
	 * Ideally we would have stored the WebDriver instances in the JMeterVariables
	 * object, however the JMeterVariables is cleared BEFORE threadFinished()
	 * callback is called (hence would never be able to quit the WebDriver).
	 */
	// PATCH: Added changes to stow the WebDriver in both places, regardless. This
	// is so other samplers who might
	// be interested in leveraging the WebDriver instance can get a handle to it
	// without too much trouble.
	private static final Map<String, WebDriver> webdrivers = new ConcurrentHashMap<String, WebDriver>();
	Map<String, WebDriver> getThreadBrowsers() {
		return webdrivers;
	}
	void clearThreadBrowsers() {
		webdrivers.clear();
	}
	protected T getThreadBrowser() {
		return (T) webdrivers.get(currentThreadName());
	}
	protected T removeThreadBrowser() {
		return (T) webdrivers.remove(currentThreadName());
	}


	private static String browserName;
	public static void setBrowserName(String name) {
		browserName = name;
	}
	public static String getBrowserName() {
		return browserName;
	}

	private final transient ProxyFactory proxyFactory;
	protected WebDriverConfig() {
		this(ProxyFactory.getInstance());
	}
	protected WebDriverConfig(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	@Override
	public void threadStarted() {
		// don't create new browser if there is one there already
		if (hasThreadBrowser()) {
			LOGGER.warn("Thread: " + currentThreadName() + " already has a WebDriver(" + getThreadBrowser()
					+ ") associated with it. ThreadGroup can only contain a single WebDriverConfig.");
			return;
		}

		// create new browser instance
		final T browser = getPreparedBrowser();
		setThreadBrowser(browser);

		// ensures the browser will quit when JVM exits (especially important in
		// devMode)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				quitBrowser(browser);
			}
		});
	}

	protected boolean hasThreadBrowser() {
		if (webdrivers.containsKey(currentThreadName())) {
			WebDriver browser = webdrivers.get(currentThreadName());
			try {
				browser.getCurrentUrl();
				return true;
			} catch (Exception ex) {
				LOGGER.warn("Old browser object is inaccessible, will create new", ex);
				webdrivers.remove(currentThreadName());
			}
		}
		return false;
	}

	protected void setThreadBrowser(T browser) {
		if (browser != null) {
			webdrivers.put(currentThreadName(), browser);
			// Make sure we stow the object in both places, in case someone wants a copy
			if (getThreadContext().getVariables() != null) {
				getThreadContext().getVariables().putObject(WebDriverConfig.BROWSER, browser);
			}
		}
	}

	protected String currentThreadName() {
		return Thread.currentThread().getName();
	}

	@Override
	public void iterationStart(LoopIterationEvent loopIterationEvent) {
		if (isRecreateBrowserOnIterationStart() && !isDevMode()) {
			final T browser = getThreadBrowser();
			quitBrowser(browser);
			setThreadBrowser(getPreparedBrowser());
			LOGGER.info("Created browser object: " + browser);
		}
	}

	/**
	 * Quits browser at the end of the tests. This will be envoked per
	 * thread/browser instance created.
	 *
	 * @param browser is the browser instance to quit. Will not quit if argument is
	 *                null.
	 */
	protected void quitBrowser(final T browser) {
		if (browser != null) {
			try {
				browser.quit();
			} catch (SessionNotCreatedException e) {
				LOGGER.warn("Attempting to quit browser instance that has already exited.");
			}
		}
	}

	private T getPreparedBrowser() {
		T browser = createBrowser();
		if ((isBrowserMaximized()) && ((getBrowserName() == "firefox") || (getBrowserName() == "internet explorer"))) {
			browser.manage().window().maximize();
		}
		return browser;
	}

	/**
	 * Creates a new browser at the start of the tests. This method will always
	 * return a new instance of a {@link WebDriver} class and is called per thread.
	 *
	 * @return a new {@link WebDriver} object.
	 */
	protected abstract T createBrowser();

	/**
	 * Call this method to create a {@link Proxy} instance for use when creating a
	 * {@link org.openqa.selenium.WebDriver} instance. The values/settings of the
	 * proxy depends entirely on the values set on this config instance.
	 *
	 * @return a {@link Proxy}
	 */
	public Proxy createProxy() {
		switch (getProxyType()) {
		case PROXY_PAC:
			return proxyFactory.getConfigUrlProxy(getProxyPacUrl());
		case DIRECT:
			return proxyFactory.getDirectProxy();
		case AUTO_DETECT:
			return proxyFactory.getAutodetectProxy();
		case MANUAL:
			if (isUseHttpSettingsForAllProtocols()) {
				ProxyHostPort proxy = new ProxyHostPort(getHttpHost(), getHttpPort());
				return proxyFactory.getManualProxy(proxy, proxy, proxy, proxy, getNoProxyHost());
			}
			ProxyHostPort http = new ProxyHostPort(getHttpHost(), getHttpPort());
			ProxyHostPort https = new ProxyHostPort(getHttpsHost(), getHttpsPort());
			ProxyHostPort ftp = new ProxyHostPort(getFtpHost(), getFtpPort());
			ProxyHostPort socks = new ProxyHostPort(getSocksHost(), getSocksPort());
			return proxyFactory.getManualProxy(http, https, ftp, socks, getNoProxyHost());
		default:
			return proxyFactory.getSystemProxy();
		}
	}

	@Override
	public void threadFinished() {
		if (!isDevMode()) {
			final T browser = removeThreadBrowser();
			quitBrowser(browser);
		}
	}

	protected ChromeOptions createChromeOptions() {
		ChromeOptions options = new ChromeOptions();

		// Custom Chrome capabilities
		// Arguments
		if (isBrowserMaximized()) {
			options.addArguments("--start-maximized");
		}
		if (isHeadless()) {
			options.addArguments("--headless=new");
		}

		String additionalArgs = trimmed(getChromeAdditionalArgs());
		if (null != additionalArgs && !additionalArgs.isEmpty()) {
			options.addArguments(additionalArgs.split("\\s+"));
		}

		// Starting browser in a specified location
		String binaryPath = trimmed(getChromeBinaryPath());
		if (null != binaryPath && !binaryPath.isEmpty()) {
			options.setBinary(binaryPath);
		}

		// Capabilities shared by all browsers
		setSharedCaps(options);
		combineCustomCapabilities(options);

		return options;
	}

	protected EdgeOptions createEdgeOptions() {
		EdgeOptions options = new EdgeOptions();

		// Custom Edge capabilities
		// Arguments
		if (isBrowserMaximized()) {
			options.addArguments("--start-maximized");
		}
		if (isHeadless()) {
			options.addArguments("--headless=new");
		}

		String additionalArgs = trimmed(getEdgeAdditionalArgs());
		if (null != additionalArgs && !additionalArgs.isEmpty()) {
			options.addArguments(additionalArgs.split("\\s+"));
		}

		// Starting browser in a specified location
		String binaryPath = trimmed(getEdgeBinaryPath());
		if (null != binaryPath && !binaryPath.isEmpty()) {
			options.setBinary(binaryPath);
		}

		// Capabilities shared by all browsers
		setSharedCaps(options);
		combineCustomCapabilities(options);

		return options;
	}

	protected FirefoxOptions createFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();

		// Custom Firefox capabilities
		if (isHeadless()) {
			options.addArguments("--headless");
		}
        options.setProfile(createProfile());

		// Capabilities shared by all browsers
		setSharedCaps(options);
		combineCustomCapabilities(options);

		return options;
	}

    private FirefoxProfile createProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("app.update.enabled", false);

        String userAgentOverride = getUserAgentOverride();
        if (StringUtils.isNotEmpty(userAgentOverride)) {
            profile.setPreference("general.useragent.override", userAgentOverride);
        }

        String ntlmOverride = getNtlmSetting();
        if (StringUtils.isNotEmpty(ntlmOverride)) {
            profile.setPreference("network.negotiate-auth.allow-insecure-ntlm-v1", true);
        }

        addExtensions(profile);
        setPreferences(profile);

        return profile;
    }

    private void addExtensions(FirefoxProfile profile) {
        JMeterProperty property = getProperty(EXTENSIONS_TO_LOAD);
        if (property instanceof NullProperty) {
            return;
        }
        CollectionProperty rows = (CollectionProperty) property;
        for (int i = 0; i < rows.size(); i++) {
            ArrayList row = (ArrayList) rows.get(i).getObjectValue();
            String filename = ((JMeterProperty) row.get(0)).getStringValue();
            profile.addExtension(new File(filename));
        }
    }

    private void setPreferences(FirefoxProfile profile) {
        JMeterProperty property = getProperty(PREFERENCES);
        if (property instanceof NullProperty) {
            return;
        }
        CollectionProperty rows = (CollectionProperty) property;
        for (int i = 0; i < rows.size(); i++) {
            ArrayList row = (ArrayList) rows.get(i).getObjectValue();
            String name = ((JMeterProperty) row.get(0)).getStringValue();
            String value = ((JMeterProperty) row.get(1)).getStringValue();
            switch (value) {
                case "true":
                    profile.setPreference(name, true);
                    break;
                case "false":
                    profile.setPreference(name, false);
                    break;
                default:
                    profile.setPreference(name, value);
                    break;
            }
        }
    }

    InternetExplorerOptions createIEOptions() {
        InternetExplorerOptions options = new InternetExplorerOptions();

        // Custom IE capabilities
        // Settings to launch Microsoft Edge in IE mode
        // As of v4.5.0, IE Driver will automatically locate Edge on the system.
        options.attachToEdgeChrome();
        options.withInitialBrowserUrl(getInitialIeUrl());

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
        setSharedCaps(options);
		combineCustomCapabilities(options);

        return options;
    }

	public void setSharedCaps(AbstractDriverOptions<?> caps) {
	    // Capabilities shared by all browsers
		caps.setAcceptInsecureCerts(isAcceptInsecureCerts());
		caps.setProxy(createProxy());
	}

	public void combineCustomCapabilities(MutableCapabilities baseCapabilities) {
		String customCaps = getCustomCapabilities();
// Cannot use !(customCaps.isBlank()) as isBlank requires Java 11 and JMeter targets Java 8.
		if ((customCaps != null) && !(customCaps.isEmpty())) {
			try {
				Map<String, Object> customCapsJson = mapper.readValue(customCaps, LinkedHashMap.class);
				customCapsJson.keySet().stream().forEach(key -> baseCapabilities.setCapability(key, customCapsJson.get(key)));
			} catch (JsonProcessingException e) {
				LOGGER.error("Unable to parse custom capabilities JSON: " + e.getMessage());
			}
		}
	}

	public String getChromeBinaryPath() {
		return getPropertyAsString(CHROME_BINARY_PATH);
	}
	public void setChromeBinaryPath(String binaryPath) {
		setProperty(CHROME_BINARY_PATH, binaryPath);
	}

	public String getEdgeBinaryPath() {
		return getPropertyAsString(EDGE_BINARY_PATH);
	}
	public void setEdgeBinaryPath(String binaryPath) {
		setProperty(EDGE_BINARY_PATH, binaryPath);
	}

	public String getDriverPath() {
		return getPropertyAsString(DRIVER_PATH);
	}
	public void setDriverPath(String path) {
		setProperty(DRIVER_PATH, path);
	}

	public String getChromeAdditionalArgs() {
		return getPropertyAsString(CHROME_ADDITIONAL_ARGS);
	}
	public void setChromeAdditionalArgs(String additionalArgs) {
		setProperty(CHROME_ADDITIONAL_ARGS, additionalArgs);
	}

	public String getEdgeAdditionalArgs() {
		return getPropertyAsString(EDGE_ADDITIONAL_ARGS);
	}
	public void setEdgeAdditionalArgs(String additionalArgs) {
		setProperty(EDGE_ADDITIONAL_ARGS, additionalArgs);
	}

	public String getFtpHost() {
		return getPropertyAsString(FTP_HOST);
	}
	public void setFtpHost(String host) {
		setProperty(FTP_HOST, host);
	}

	public int getFtpPort() {
		return getPropertyAsInt(FTP_PORT);
	}
	public void setFtpPort(int port) {
		setProperty(FTP_PORT, port);
	}

	public String getHttpHost() {
		return getPropertyAsString(HTTP_HOST);
	}
	public void setHttpHost(String host) {
		setProperty(HTTP_HOST, host);
	}

	public int getHttpPort() {
		return getPropertyAsInt(HTTP_PORT);
	}
	public void setHttpPort(int port) {
		setProperty(HTTP_PORT, port);
	}

	public String getHttpsHost() {
		return getPropertyAsString(HTTPS_HOST);
	}
	public void setHttpsHost(String httpsHost) {
		setProperty(HTTPS_HOST, httpsHost);
	}

	public int getHttpsPort() {
		return getPropertyAsInt(HTTPS_PORT);
	}
	public void setHttpsPort(int port) {
		setProperty(HTTPS_PORT, port);
	}

	public String getNoProxyHost() {
		return getPropertyAsString(NO_PROXY);
	}
	public void setNoProxyHost(String noProxyHostList) {
		setProperty(NO_PROXY, noProxyHostList);
	}

	public String getProxyPacUrl() {
		return getPropertyAsString(PROXY_PAC_URL);
	}
	public void setProxyPacUrl(String pacUrl) {
		setProperty(PROXY_PAC_URL, pacUrl);
	}

	public ProxyType getProxyType() {
		return ProxyType.valueOf(getPropertyAsString(PROXY_TYPE, ProxyType.SYSTEM.name()));
	}
	public void setProxyType(ProxyType type) {
		setProperty(PROXY_TYPE, type.name());
	}

	public String getSocksHost() {
		return getPropertyAsString(SOCKS_HOST);
	}
	public void setSocksHost(String host) {
		setProperty(SOCKS_HOST, host);
	}

	public int getSocksPort() {
		return getPropertyAsInt(SOCKS_PORT);
	}
	public void setSocksPort(int port) {
		setProperty(SOCKS_PORT, port);
	}

	public boolean isAcceptInsecureCerts() {
		return getPropertyAsBoolean(ACCEPT_INSECURE_CERTS);
	}
	public void setAcceptInsecureCerts(boolean enabled) {
		setProperty(ACCEPT_INSECURE_CERTS, enabled);
	}

	public String getCustomCapabilities() {
		// this is stringified map of json data
		return getPropertyAsString(CUSTOM_CAPABILITIES);
	}

	public void setCustomCapabilities(String customCapabilities) {
		// this is stringified map of json data
		setProperty(CUSTOM_CAPABILITIES, customCapabilities);
	}

	public boolean isUseHttpSettingsForAllProtocols() {
		return getPropertyAsBoolean(USE_HTTP_FOR_ALL_PROTOCOLS, true);
	}
	public void setUseHttpSettingsForAllProtocols(boolean override) {
		setProperty(USE_HTTP_FOR_ALL_PROTOCOLS, override);
	}

    public boolean isHeadless() {
        return getPropertyAsBoolean(ENABLE_HEADLESS);
    }
    public void setHeadless(boolean headless) {
        setProperty(ENABLE_HEADLESS, headless);
    }

	public boolean isBrowserMaximized() {
		return getPropertyAsBoolean(MAXIMIZE_WINDOW, false);
	}
	public void setBrowserMaximized(boolean state) {
		setProperty(MAXIMIZE_WINDOW, state);
	}

	public boolean isDevMode() {
		return getPropertyAsBoolean(DEV_MODE);
	}
	public void setDevMode(boolean devMode) {
		setProperty(DEV_MODE, devMode);
	}

	public boolean isRecreateBrowserOnIterationStart() {
		return getPropertyAsBoolean(RECREATE_ON_ITERATION_START);
	}
	public void setRecreateBrowserOnIterationStart(boolean recreate) {
		setProperty(RECREATE_ON_ITERATION_START, recreate);
	}

    public boolean isUserAgentOverridden() {
        return getPropertyAsBoolean(ENABLE_USERAGENT_OVERRIDE);
    }
    public void setUserAgentOverridden(boolean userAgentOverridden) {
        setProperty(ENABLE_USERAGENT_OVERRIDE, userAgentOverridden);
    }

    public String getUserAgentOverride() {
        return getPropertyAsString(GENERAL_USERAGENT_OVERRIDE);
    }
    public void setUserAgentOverride(String userAgent) {
        setProperty(GENERAL_USERAGENT_OVERRIDE, userAgent);
    }

    public JMeterProperty getExtensions() {
        return getProperty(EXTENSIONS_TO_LOAD);
    }
    public void setExtensions(PowerTableModel model) {
        CollectionProperty prop = JMeterPluginsUtils.tableModelRowsToCollectionProperty(model, EXTENSIONS_TO_LOAD);
        setProperty(prop);
    }

    public String getNtlmSetting() {
        return getPropertyAsString(ENABLE_NTML);
    }
    public void setNtlmSetting(boolean ntlm) {
        setProperty(ENABLE_NTML, ntlm);
    }

    public JMeterProperty getPreferences() {
        return getProperty(PREFERENCES);
    }
    public void setPreferences(PowerTableModel model) {
        CollectionProperty prop = JMeterPluginsUtils.tableModelRowsToCollectionProperty(model, PREFERENCES);
        setProperty(prop);
    }

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

	public String getInitialIeUrl() {
		return getPropertyAsString(INITIAL_IE_URL);
	}
	public void setInitialIeUrl(String webUrl) {
		setProperty(INITIAL_IE_URL, webUrl);
	}

	private String trimmed(String str) {
		return null == str ? null : str.trim();
	}
}
