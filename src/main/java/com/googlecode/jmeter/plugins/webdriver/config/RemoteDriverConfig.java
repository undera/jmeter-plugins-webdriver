package com.googlecode.jmeter.plugins.webdriver.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDriverConfig extends WebDriverConfig<RemoteWebDriver> {

	private static final long serialVersionUID = 100L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDriverConfig.class);

	private static final String LOCAL_FILE_DETECTOR = "RemoteDriverConfig.general.selenium.file.detector";
	private static final String REMOTE_CAPABILITY = "RemoteDriverConfig.general.selenium.capability";
	private static final String REMOTE_SELENIUM_GRID_URL = "RemoteDriverConfig.general.selenium.grid.url";

	@Override
	protected RemoteWebDriver createBrowser() {
		try {
			RemoteWebDriver driver = new RemoteWebDriver(new URL(getSeleniumGridUrl()), createCapabilities());
			if (isLocalFileDectedor()) {
				driver.setFileDetector(new LocalFileDetector());
			}
			LOGGER.debug("Created remote web driver.");
			return driver;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	Capabilities createCapabilities() {
		// We pass the browser option instance to the remote so it knows which browser to use
		AbstractDriverOptions<?> caps = null;
		switch (getCapability()) {
		case CHROME:
			caps = createChromeOptions();
			break;
		case EDGE:
			caps = createEdgeOptions();
			break;
		case FIREFOX:
			caps = createFirefoxOptions();
			break;
		case INTERNET_EXPLORER:
			caps = createIEOptions();
			break;
		default:
			throw new IllegalArgumentException("No such capability");
		}
		return caps;
	}

	public RemoteCapability getCapability() {
		return RemoteCapability.valueOf(getPropertyAsString(REMOTE_CAPABILITY));
	}
	public void setCapability(RemoteCapability selectedCapability) {
		setProperty(REMOTE_CAPABILITY, selectedCapability.name());
	}

	public String getSeleniumGridUrl() {
		return getPropertyAsString(REMOTE_SELENIUM_GRID_URL);
	}
	public void setSeleniumGridUrl(String seleniumUrl) {
		setProperty(REMOTE_SELENIUM_GRID_URL, seleniumUrl);
	}

	public boolean isLocalFileDectedor() {
		return getPropertyAsBoolean(LOCAL_FILE_DETECTOR);
	}
	public void setLocalFileDetector(boolean enabled) {
		setProperty(LOCAL_FILE_DETECTOR, enabled);
	}
}
