package com.googlecode.jmeter.plugins.webdriver.config.gui;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.RemoteBrowser;
import com.googlecode.jmeter.plugins.webdriver.config.RemoteDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

public class RemoteDriverConfigGui extends WebDriverConfigGui {

	private static final long serialVersionUID = 100L;

	@Override
	String browserName() {
		WebDriverConfig.setBrowserName("Remote");
	    return "Remote";
	}

	@Override
	public String getStaticLabel() {
		return JMeterPluginsUtils.prefixLabel("Remote Driver Config");
	}

	@Override
	protected boolean isBrowser() {
		return false;
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
		RemoteDriverConfig element = new RemoteDriverConfig();
		modifyTestElement(element);
		return element;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		super.modifyTestElement(element);
		if (element instanceof RemoteDriverConfig) {
			RemoteDriverConfig config = (RemoteDriverConfig) element;
			config.setSeleniumGridUrl(remoteSeleniumGridText.getText());
			config.setSelectedBrowser((RemoteBrowser) browserCapabilitiesComboBox.getSelectedItem());
			config.setLocalFileDetector(localFileDetector.isSelected());
		}
	}

	@Override
	public void clearGui() {
		super.clearGui();
		remoteSeleniumGridText.setText(StringUtils.EMPTY);
		browserCapabilitiesComboBox.setSelectedIndex(1);
		localFileDetector.setSelected(false);
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		if (element instanceof RemoteDriverConfig) {
			RemoteDriverConfig config = (RemoteDriverConfig) element;
			remoteSeleniumGridText.setText(config.getSeleniumGridUrl());
			browserCapabilitiesComboBox.setSelectedItem(config.getSelectedBrowser());
			localFileDetector.setSelected(config.isLocalFileDectedor());
		}
	}
}