package com.googlecode.jmeter.plugins.webdriver.config.gui;

import com.googlecode.jmeter.plugins.webdriver.config.HtmlUnitDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

import org.apache.jmeter.testelement.TestElement;

public class HtmlUnitDriverConfigGui extends WebDriverConfigGui {

    private static final long serialVersionUID = 100L;

	@Override
	String browserName() {
		WebDriverConfig.setBrowserName("HtmlUnit");
	    return "HtmlUnit";
	}

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("HtmlUnit Driver Config");
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
        HtmlUnitDriverConfig element = new HtmlUnitDriverConfig();
        modifyTestElement(element);
        return element;
    }

    /*
    @Override
    protected JPanel createOptionsPanel() {
        final JPanel browserPanel = new VerticalPanel();

        // ToDo Label
        JLabel experimentalLabel = new JLabel("To Do");
        experimentalLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        browserPanel.add(experimentalLabel);
        return browserPanel;
    }
*/
}
