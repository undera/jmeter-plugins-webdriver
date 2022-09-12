package com.googlecode.jmeter.plugins.webdriver.config.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kg.apc.jmeter.JMeterPluginsUtils;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.InternetExplorerDriverConfig;

public class InternetExplorerDriverConfigGui extends WebDriverConfigGui {

    private static final long serialVersionUID = 100L;
    JTextField ieServicePath;
    JTextField msEdgePath;

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("Internet Explorer Driver Config");
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if(element instanceof InternetExplorerDriverConfig) {
            InternetExplorerDriverConfig config = (InternetExplorerDriverConfig)element;
            ieServicePath.setText(config.getInternetExplorerDriverPath());
            msEdgePath.setText(config.getMsEdgeDriverPath());
        }
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
        if(element instanceof InternetExplorerDriverConfig) {
            InternetExplorerDriverConfig config = (InternetExplorerDriverConfig)element;
            config.setInternetExplorerDriverPath(ieServicePath.getText());
            config.setMsEdgeDriverPath(msEdgePath.getText());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        ieServicePath.setText("path to IEDriverServer.exe");
        msEdgePath.setText("path to msedge.exe");
    }

    @Override
    protected JPanel createBrowserPanel() {
        return createServicePanel();
    }

    @Override
    protected String browserName() {
        return "Internet Explorer";
    }

    @Override
    protected String getWikiPage() {
        return "InternetExplorerConfig";
    }

    private JPanel createServicePanel() {
        final JPanel browserPanel = new VerticalPanel();
        final JPanel ieServicePanel = new HorizontalPanel();
        final JLabel ieDriverServiceLabel = new JLabel("Path to Internet Explorer Driver");
        ieServicePanel.add(ieDriverServiceLabel);
        final JPanel edgeServicePanel = new HorizontalPanel();
        final JLabel edgeDriverServiceLabel = new JLabel("Path to Edge Executable");
        edgeServicePanel.add(edgeDriverServiceLabel);

        ieServicePath = new JTextField();
        ieServicePanel.add(ieServicePath);
        msEdgePath = new JTextField();
        edgeServicePanel.add(msEdgePath);
        browserPanel.add(ieServicePanel);
        browserPanel.add(edgeServicePanel);
        return browserPanel;
    }

	@Override
	protected boolean isProxyEnabled() {
		return true;
	}

	@Override
	protected boolean isExperimentalEnabled() {
		return true;
	}

}