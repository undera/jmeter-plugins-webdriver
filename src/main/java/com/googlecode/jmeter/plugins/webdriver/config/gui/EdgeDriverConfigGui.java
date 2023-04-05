package com.googlecode.jmeter.plugins.webdriver.config.gui;

import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.EdgeDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

public class EdgeDriverConfigGui extends WebDriverConfigGui {

	private static final long serialVersionUID = 100L;

	@Override
	String browserName() {
		WebDriverConfig.setBrowserName("edge");
	    return "edge";
	}

	@Override
	public String getStaticLabel() {
		return JMeterPluginsUtils.prefixLabel("Edge Driver Config");
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
		EdgeDriverConfig element = new EdgeDriverConfig();
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
