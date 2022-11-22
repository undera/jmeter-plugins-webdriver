package com.googlecode.jmeter.plugins.webdriver.config.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;

import com.googlecode.jmeter.plugins.webdriver.config.FirefoxDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.Grid;

public class FirefoxDriverConfigGui extends WebDriverConfigGui implements ItemListener {

	private static final long serialVersionUID = 100L;

	@Override
	String browserName() {
		WebDriverConfig.setBrowserName("firefox");
	    return "firefox";
	}

	@Override
	public String getStaticLabel() {
		return JMeterPluginsUtils.prefixLabel("Firefox Driver Config");
	}

	@Override
	protected String getWikiPage() {
		return "FirefoxDriverConfig";
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
		FirefoxDriverConfig element = new FirefoxDriverConfig();
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

	public void itemStateChanged(ItemEvent itemEvent) {
		if (itemEvent.getSource() == userAgentOverrideCheckbox) {
			userAgentOverrideText.setEnabled(userAgentOverrideCheckbox.isSelected());
		}
	}
}
