package com.googlecode.jmeter.plugins.webdriver.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.*;

import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.jmeter.plugins.webdriver.config.RemoteCapability.CHROME;

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

	private static final Logger LOGGER = LoggingManager.getLoggerForClass();

	Capabilities createCapabilities() {
		DesiredCapabilities capabilities = RemoteDesiredCapabilitiesFactory.build(getCapability());
		capabilities.setCapability(CapabilityType.PROXY, createProxy());
		capabilities.setJavascriptEnabled(true);

		if (getCapability().equals(CHROME) && isHeadlessEnabled()) {
			final ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		}
		else if (getCapability().equals(CHROME)) {
			final ChromeOptions chromeOptions = new ChromeOptions();
			if(isBrowserMaximized())
				chromeOptions.addArguments("--start-maximized");
			if(isLogEnabled())
				chromeOptions.setCapability("enableLog",true);
			if(isVideoEnabled())
				chromeOptions.setCapability("enableVideo",true);
			if(isVNCEnabled())
				chromeOptions.setCapability("enableVNC",true);
			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		}

		return capabilities;
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
			return getFileDetectorOption().getClazz().newInstance();
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
