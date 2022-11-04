package com.googlecode.jmeter.plugins.webdriver.config.gui;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.ChromeDriverConfig;

import kg.apc.jmeter.JMeterPluginsUtils;

public class ChromeDriverConfigGui extends WebDriverConfigGui {

    private static final long serialVersionUID = 100L;
    JTextField additionalArgs;
    JTextField binaryPath;

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("Chrome Driver Config");
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if(element instanceof ChromeDriverConfig) {
            ChromeDriverConfig config = (ChromeDriverConfig)element;
            additionalArgs.setText(config.getAdditionalArgs());
            binaryPath.setText(config.getBinaryPath());
        }
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
        if(element instanceof ChromeDriverConfig) {
            ChromeDriverConfig config = (ChromeDriverConfig)element;
            config.setAdditionalArgs(additionalArgs.getText());
            config.setBinaryPath(binaryPath.getText());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        additionalArgs.setText("");
        binaryPath.setText("");
    }

    @Override
    protected JPanel createOptionsPanel() {
        final JPanel browserPanel = new VerticalPanel();

        final JPanel binaryPathPanel = new HorizontalPanel();
        final JLabel binaryPathLabel = new JLabel("Binary (if in non-standard location)");
        binaryPath = new JTextField("");
        binaryPathPanel.add(binaryPathLabel);
        binaryPathPanel.add(binaryPath);
        browserPanel.add(binaryPathPanel);

        final JPanel additionalArgsPanel = new HorizontalPanel();
        final JLabel additionalArgsLabel = new JLabel("Additional arguments");
        additionalArgs = new JTextField();
        additionalArgsPanel.add(additionalArgsLabel);
        additionalArgsPanel.add(additionalArgs);
        browserPanel.add(additionalArgsPanel);

        return browserPanel;
    }

    @Override
    protected String browserName() {
        return "Chrome";
    }

    @Override
    protected String getWikiPage() {
        return "ChromeDriverConfig";
    }

	@Override
	protected boolean isProxyEnabled() {
		return true;
	}

	@Override
	protected boolean isDirectEnabled() {
		return true;
	}

}
