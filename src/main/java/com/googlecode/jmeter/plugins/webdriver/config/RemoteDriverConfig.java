package com.googlecode.jmeter.plugins.webdriver.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UselessFileDetector;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteDriverConfig extends WebDriverConfig<RemoteWebDriver> {

    private static final long serialVersionUID = 100L;
    private static final String REMOTE_SELENIUM_GRID_URL = "RemoteDriverConfig.general.selenium.grid.url";
    private static final String REMOTE_CAPABILITY = "RemoteDriverConfig.general.selenium.capability";
    private static final String REMOTE_FILE_DETECTOR = "RemoteDriverConfig.general.selenium.file.detector";
    private static final String HEADLESS_ENABLED = "RemoteDriverConfig.chrome.headless_enabled";
    private static final String VNC_ENABLED = "RemoteDriverConfig.chrome.vnc_enabled";
    private static final String VIDEO_ENABLED = "RemoteDriverConfig.chrome.video_enabled";
    private static final String LOG_ENABLED = "RemoteDriverConfig.chrome.log_enabled";
    private static final String BROWSER_MAXIMIZE = "RemoteDriverConfig.chrome.browser.maximize";
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
    private static final String BROWSER_LANGUAGE = "RemoteDriverConfig.chrome.browser.language";
    private String browserLanguage = "";

    public RemoteDriverConfig() {
    }

    public RemoteDriverConfig(String browserLanguage) {
        this.setBrowserLanguage(browserLanguage);
    }

    Capabilities createCapabilities() {
        DesiredCapabilities capabilities = RemoteDesiredCapabilitiesFactory.build(this.getCapability());
        capabilities.setCapability("proxy", this.createProxy());
        capabilities.setJavascriptEnabled(true);

        String browserLanguage = this.getBrowserLanguage();

        //chrome
        if (this.getCapability().equals(RemoteCapability.CHROME)) {
            return createChrome(capabilities, browserLanguage, this.isHeadlessEnabled());
        } else if (this.getCapability().equals(RemoteCapability.FIREFOX)) {
            return createFireFox(capabilities, browserLanguage, this.isHeadlessEnabled());
        }

        //其他不做处理
        return capabilities;
    }

    private Capabilities createFireFox(DesiredCapabilities capabilities, String language, boolean headlessEnabled) {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        FirefoxProfile profile = new FirefoxProfile();
        firefoxOptions.setProfile(profile);

        //配置浏览器语言
        if (StringUtils.isNotBlank(language)) {
            firefoxOptions.addArguments(String.format("--lang=%s", language));
        }
        //开启无头模式
        if (headlessEnabled) {
            firefoxOptions.addArguments(new String[]{"--headless"});
            capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
            return capabilities;
        }

        //非无头模式下的配置
        if (this.isBrowserMaximized()) {
            firefoxOptions.addArguments(new String[]{"--start-maximized"});
        }

        if (this.isLogEnabled()) {
            firefoxOptions.setCapability("enableLog", true);
        }

        if (this.isVideoEnabled()) {
            firefoxOptions.setCapability("enableVideo", true);
        }

        if (this.isVNCEnabled()) {
            firefoxOptions.setCapability("enableVNC", true);
        }

        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
        return capabilities;
    }

    private Capabilities createChrome(DesiredCapabilities capabilities, String language, boolean headlessEnabled) {
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置浏览器语言
        if (StringUtils.isNotBlank(language)) {
            chromeOptions.addArguments(String.format("--lang=%s", language));
        }
        //开启无头模式
        if (headlessEnabled) {
            chromeOptions.addArguments(new String[]{"--headless"});
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            return capabilities;
        }

        //非无头模式下的配置
        if (this.isBrowserMaximized()) {
            chromeOptions.addArguments(new String[]{"--start-maximized"});
        }

        if (this.isLogEnabled()) {
            chromeOptions.setCapability("enableLog", true);
        }

        if (this.isVideoEnabled()) {
            chromeOptions.setCapability("enableVideo", true);
        }

        if (this.isVNCEnabled()) {
            chromeOptions.setCapability("enableVNC", true);
        }

        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return capabilities;
    }

    @Override
    protected RemoteWebDriver createBrowser() {
        try {
            RemoteWebDriver driver = new RemoteWebDriver(new URL(this.getSeleniumGridUrl()), this.createCapabilities());
            driver.setFileDetector(this.createFileDetector());
            LOGGER.debug("Created web driver with " + this.createFileDetector().getClass().getName());
            return driver;
        } catch (MalformedURLException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void setBrowserLanguage(String language) {
        this.setProperty(BROWSER_LANGUAGE, language);
    }

    public String getBrowserLanguage() {
        return this.getPropertyAsString(BROWSER_LANGUAGE);
    }

    public void setSeleniumGridUrl(String seleniumUrl) {
        this.setProperty("RemoteDriverConfig.general.selenium.grid.url", seleniumUrl);
    }

    public String getSeleniumGridUrl() {
        return this.getPropertyAsString("RemoteDriverConfig.general.selenium.grid.url");
    }

    public RemoteCapability getCapability() {
        return RemoteCapability.valueOf(this.getPropertyAsString("RemoteDriverConfig.general.selenium.capability"));
    }

    public void setCapability(RemoteCapability selectedCapability) {
        this.setProperty("RemoteDriverConfig.general.selenium.capability", selectedCapability.name());
    }

    public FileDetectorOption getFileDetectorOption() {
        String fileDetectorString = this.getPropertyAsString("RemoteDriverConfig.general.selenium.file.detector");
        if (StringUtils.isBlank(fileDetectorString)) {
            LOGGER.warn("No remote file detector configured, reverting to default of useless file detector");
            return FileDetectorOption.USELESS;
        } else {
            return FileDetectorOption.valueOf(fileDetectorString);
        }
    }

    public void setFileDetectorOption(FileDetectorOption fileDetectorOption) {
        this.setProperty("RemoteDriverConfig.general.selenium.file.detector", fileDetectorOption.name());
    }

    protected FileDetector createFileDetector() {
        try {
            return (FileDetector) this.getFileDetectorOption().getClazz().newInstance();
        } catch (Exception var2) {
            LOGGER.warn("Cannot create a file detector of type " + this.getFileDetectorOption().getClazz().getCanonicalName() + ", reverting to default of useless file detector");
            return new UselessFileDetector();
        }
    }

    public boolean isHeadlessEnabled() {
        return this.getPropertyAsBoolean("RemoteDriverConfig.chrome.headless_enabled");
    }

    public void setHeadlessEnabled(boolean enabled) {
        this.setProperty("RemoteDriverConfig.chrome.headless_enabled", enabled);
    }

    public boolean isVNCEnabled() {
        return this.getPropertyAsBoolean("RemoteDriverConfig.chrome.vnc_enabled");
    }

    public void setVNCEnabled(boolean enabled) {
        this.setProperty("RemoteDriverConfig.chrome.vnc_enabled", enabled);
    }

    public boolean isVideoEnabled() {
        return this.getPropertyAsBoolean("RemoteDriverConfig.chrome.video_enabled");
    }

    public void setVideoEnabled(boolean enabled) {
        this.setProperty("RemoteDriverConfig.chrome.video_enabled", enabled);
    }

    public boolean isLogEnabled() {
        return this.getPropertyAsBoolean("RemoteDriverConfig.chrome.log_enabled");
    }

    public void setLogEnabled(boolean enabled) {
        this.setProperty("RemoteDriverConfig.chrome.log_enabled", enabled);
    }

    @Override
    public boolean isBrowserMaximized() {
        return this.getPropertyAsBoolean("RemoteDriverConfig.chrome.browser.maximize");
    }

    public void setBrowserMaximize(boolean enabled) {
        this.setProperty("RemoteDriverConfig.chrome.browser.maximize", enabled);
    }
}
