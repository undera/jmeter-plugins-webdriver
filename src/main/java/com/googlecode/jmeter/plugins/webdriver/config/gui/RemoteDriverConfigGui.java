package com.googlecode.jmeter.plugins.webdriver.config.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.RemoteCapability;
import com.googlecode.jmeter.plugins.webdriver.config.RemoteDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

public class RemoteDriverConfigGui extends WebDriverConfigGui implements ItemListener, FocusListener {

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
	protected String getWikiPage() {
		return "RemoteDriverConfig";
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
			config.setCapability((RemoteCapability) capabilitiesComboBox.getSelectedItem());
			config.setLocalFileDetector(localFileDetector.isSelected());
		}
	}

	@Override
	public void clearGui() {
		super.clearGui();
		remoteSeleniumGridText.setText(StringUtils.EMPTY);
		capabilitiesComboBox.setSelectedIndex(2);
		localFileDetector.setSelected(false);
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		if (element instanceof RemoteDriverConfig) {
			RemoteDriverConfig config = (RemoteDriverConfig) element;
			remoteSeleniumGridText.setText(config.getSeleniumGridUrl());
			capabilitiesComboBox.setSelectedItem(config.getCapability());
			localFileDetector.setSelected(config.isLocalFileDectedor());
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Nothing to do
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (remoteSeleniumGridText.equals(e.getComponent()) && !isValidUrl(remoteSeleniumGridText.getText())) {
			RemoteErrorMsg.setText("The selenium grid URL is malformed");
		} else {
			RemoteErrorMsg.setText("");
		}
		if (initialBrowserUrl.equals(e.getComponent()) && !isValidUrl(initialBrowserUrl.getText())) {
			IEerrorMsg.setText("The URL is malformed");
		} else {
			IEerrorMsg.setText("");
		}
	}
}