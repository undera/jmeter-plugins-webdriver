package com.googlecode.jmeter.plugins.webdriver.config.gui;

import kg.apc.jmeter.JMeterPluginsUtils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;

import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.InternetExplorerDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

public class InternetExplorerDriverConfigGui extends WebDriverConfigGui implements ItemListener, FocusListener {

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
    protected String getWikiPage() {
        return "DirectDriverConfig";
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

    @Override
	public void focusGained(FocusEvent e) {
		// Nothing to do
	}

    @Override
	public void focusLost(FocusEvent e) {
		if (initialBrowserUrl.equals(e.getComponent()) && !isValidUrl(initialBrowserUrl.getText())) {
			IEerrorMsg.setText("The URL is malformed");
		} else {
			IEerrorMsg.setText("");
		}
	}
}
