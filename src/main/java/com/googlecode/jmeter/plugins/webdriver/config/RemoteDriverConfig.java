package com.googlecode.jmeter.plugins.webdriver.config;

import static com.googlecode.jmeter.plugins.webdriver.config.RemoteCapability.CHROME;
import static com.googlecode.jmeter.plugins.webdriver.config.RemoteCapability.FIREFOX;
import static com.googlecode.jmeter.plugins.webdriver.config.RemoteCapability.INTERNET_EXPLORER;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UselessFileDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDriverConfig extends WebDriverConfig<RemoteWebDriver> {

	private static final long serialVersionUID = 100L;
	private static final String REMOTE_SELENIUM_GRID_URL = "RemoteDriverConfig.general.selenium.grid.url";
	private static final String REMOTE_CAPABILITY = "RemoteDriverConfig.general.selenium.capability";
	private static final String REMOTE_FILE_DETECTOR = "RemoteDriverConfig.general.selenium.file.detector";
	private static final String HEADLESS_ENABLED = "RemoteDriverConfig.chrome.headless_enabled";
	//adding options especially for selenoid
	private static final String VNC_ENABLED = "RemoteDriverConfig.chrome.vnc_enabled";
	private static final String VIDEO_ENABLED = "RemoteDriverConfig.chrome.video_enabled";
	private static final String LOG_ENABLED = "RemoteDriverConfig.chrome.log_enabled";
	private static final String BROWSER_MAXIMIZE = "RemoteDriverConfig.chrome.browser.maximize";

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDriverConfig.class);

	Capabilities createCapabilities() {
		AbstractDriverOptions caps = null;
		switch(getCapability()) {
		case CHROME:
			caps = new ChromeOptions();
			if (isHeadlessEnabled()) {
				((ChromiumOptions<ChromeOptions>) caps).addArguments("--headless");
			} else {
				if(isBrowserMaximized())
					((ChromiumOptions<ChromeOptions>) caps).addArguments("--start-maximized");
				if(isLogEnabled())
					caps.setCapability("enableLog",true);
				if(isVideoEnabled())
					caps.setCapability("enableVideo",true);
				if(isVNCEnabled())
					caps.setCapability("enableVNC",true);
			}
			break;
		case FIREFOX:
			caps = new FirefoxOptions();
			((FirefoxOptions) caps).setProfile(new FirefoxProfile());
			break;
		case INTERNET_EXPLORER:
			// Settings to launch Microsoft Edge in IE mode
			// As of v4.5.0, IE Driver will automatically locate Edge on the remote system.
			caps = new InternetExplorerOptions();
			((InternetExplorerOptions) caps).attachToEdgeChrome();
			((InternetExplorerOptions) caps).ignoreZoomSettings();
			// Set an initial valid page otherwise IeDriver hangs on page load...
			((InternetExplorerOptions) caps).withInitialBrowserUrl("https://www.bing.com/");
			break;
		default:
			throw new IllegalArgumentException("No such capability");
		}
		caps.setProxy(createProxy());
		return caps;
	}

	@Override
	protected RemoteWebDriver createBrowser() {
		try {
			RemoteWebDriver driver = new RemoteWebDriver(new URL(getSeleniumGridUrl()), createCapabilities());
			driver.setFileDetector(createFileDetector());
			LOGGER.debug("Created web driver with " + createFileDetector().getClass().getName());
			return driver;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setSeleniumGridUrl(String seleniumUrl) {
		setProperty(REMOTE_SELENIUM_GRID_URL, seleniumUrl);
	}

	public String getSeleniumGridUrl() {
		return getPropertyAsString(REMOTE_SELENIUM_GRID_URL);
	}

	public RemoteCapability getCapability(){
		return RemoteCapability.valueOf(getPropertyAsString(REMOTE_CAPABILITY));
	}

	public void setCapability(RemoteCapability selectedCapability) {
		setProperty(REMOTE_CAPABILITY, selectedCapability.name());
	}

	public FileDetectorOption getFileDetectorOption() {
		String fileDetectorString = getPropertyAsString(REMOTE_FILE_DETECTOR);
		if (StringUtils.isBlank(fileDetectorString)) {
			LOGGER.warn("No remote file detector configured, reverting to default of useless file detector");
			return FileDetectorOption.USELESS;
		}
		return FileDetectorOption.valueOf(fileDetectorString);
	}

	public void setFileDetectorOption(FileDetectorOption fileDetectorOption) {
		setProperty(REMOTE_FILE_DETECTOR, fileDetectorOption.name());
	}

	protected FileDetector createFileDetector() {
		try {
			return getFileDetectorOption().getClazz().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			LOGGER.warn("Cannot create a file detector of type " + getFileDetectorOption().getClazz().getCanonicalName() + ", reverting to default of useless file detector");
			return new UselessFileDetector();
		}
	}

	public boolean isHeadlessEnabled() {
		return getPropertyAsBoolean(HEADLESS_ENABLED);
	}


	public void setHeadlessEnabled(boolean enabled) {
		setProperty(HEADLESS_ENABLED, enabled);
	}

	public boolean isVNCEnabled() {
		return getPropertyAsBoolean(VNC_ENABLED);
	}

	public void setVNCEnabled(boolean enabled) {
		setProperty(VNC_ENABLED, enabled);
	}

	public boolean isVideoEnabled() {
		return getPropertyAsBoolean(VIDEO_ENABLED);
	}

	public void setVideoEnabled(boolean enabled) {
		setProperty(VIDEO_ENABLED, enabled);
	}
	public boolean isLogEnabled() {
		return getPropertyAsBoolean(LOG_ENABLED);
	}

	public void setLogEnabled(boolean enabled) {
		setProperty(LOG_ENABLED, enabled);
	}
	public boolean isBrowserMaximized() {
		return getPropertyAsBoolean(BROWSER_MAXIMIZE);
	}

	public void setBrowserMaximize(boolean enabled) {
		setProperty(BROWSER_MAXIMIZE, enabled);
	}
}
