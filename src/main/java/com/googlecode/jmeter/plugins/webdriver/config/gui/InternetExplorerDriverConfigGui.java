package com.googlecode.jmeter.plugins.webdriver.config.gui;

import kg.apc.jmeter.JMeterPluginsUtils;

import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.InternetExplorerDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

public class InternetExplorerDriverConfigGui extends WebDriverConfigGui {

    private static final long serialVersionUID = 100L;

    @Override
	String browserName() {
		WebDriverConfig.setBrowserName("internet explorer");
	    return "internet explorer";
	}

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("Internet Explorer Driver Config");
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
        InternetExplorerDriverConfig element = new InternetExplorerDriverConfig();
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
