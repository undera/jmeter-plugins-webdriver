package com.googlecode.jmeter.plugins.webdriver.config.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.gui.util.VerticalPanel;
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
			errorMsg.setText("The selenium grid URL is malformed");
		} else {
			errorMsg.setText("");
		}
	}

	private boolean isValidUrl(String urlStr) {
		try {
			new URL(urlStr);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}