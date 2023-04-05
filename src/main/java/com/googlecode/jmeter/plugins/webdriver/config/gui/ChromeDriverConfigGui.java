package com.googlecode.jmeter.plugins.webdriver.config.gui;

import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.ChromeDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

public class ChromeDriverConfigGui extends WebDriverConfigGui {

	private static final long serialVersionUID = 100L;

	@Override
	String browserName() {
		WebDriverConfig.setBrowserName("chrome");
	    return "chrome";
	}

	@Override
	public String getStaticLabel() {
		return JMeterPluginsUtils.prefixLabel("Chrome Driver Config");
	}

	@Override
	protected boolean isBrowser() {
		return true;
	}

	@Override
	protected boolean isProxyEnabled() {
		return true;
	}

	@Override
	public String getLabelResource() {
		return getClass().getCanonicalName();
	}

	@Override
	public TestElement createTestElement() {
		ChromeDriverConfig element = new ChromeDriverConfig();
		modifyTestElement(element);
		return element;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		super.modifyTestElement(element);
	}

	@Override
	public void clearGui() {
		super.clearGui();
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
	}
}
