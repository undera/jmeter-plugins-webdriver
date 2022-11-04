package com.googlecode.jmeter.plugins.webdriver.config.gui;

import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kg.apc.jmeter.JMeterPluginsUtils;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import com.googlecode.jmeter.plugins.webdriver.config.InternetExplorerDriverConfig;

public class InternetExplorerDriverConfigGui extends WebDriverConfigGui {

    private static final long serialVersionUID = 100L;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();
    private static final int Default_FileUploadDialogTimeout = 1000;

    static {
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    JFormattedTextField fileUploadDialogTiemout;

    JCheckBox ensureCleanSession;

    JCheckBox ignoreProtectedMode;

    JCheckBox silent;

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
            fileUploadDialogTiemout.setText(String.valueOf(config.getFileUploadDialogTimeout()));
            ensureCleanSession.setSelected(config.isEnsureCleanSession());
            silent.setSelected(config.isSilent());
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
            config.setFileUploadDialogTimeout(Integer.parseInt(fileUploadDialogTiemout.getText()));
            config.setEnsureCleanSession(ensureCleanSession.isSelected());
            config.setIgnoreProtectedMode(ignoreProtectedMode.isSelected());
            config.setSilent(silent.isSelected());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        fileUploadDialogTiemout.setText(String.valueOf(Default_FileUploadDialogTimeout));
        ensureCleanSession.setSelected(false);
        ignoreProtectedMode.setSelected(false);
        silent.setSelected(false);
    }

    @Override
    protected JPanel createOptionsPanel() {
        JPanel panel = new VerticalPanel();

        // fileUploadDialogTimeout
        final JPanel fileUploadDialogTimeoutPanel = new HorizontalPanel();
        final JLabel fileUploadDialogTimeoutLabel = new JLabel("Wait for File Upload Dialog up to (ms)");
        fileUploadDialogTimeoutPanel.add(fileUploadDialogTimeoutLabel);
        fileUploadDialogTiemout = new JFormattedTextField(NUMBER_FORMAT);
        fileUploadDialogTiemout.setText(String.valueOf(Default_FileUploadDialogTimeout));
        fileUploadDialogTimeoutPanel.add(fileUploadDialogTiemout);
        panel.add(fileUploadDialogTimeoutPanel);

        ensureCleanSession = new JCheckBox("Ensure Clean Session");
        ensureCleanSession.setSelected(false);
        panel.add(ensureCleanSession);

        ignoreProtectedMode = new JCheckBox("Ignore Protected Mode Settings");
        ignoreProtectedMode.setSelected(false);
        panel.add(ignoreProtectedMode);

        silent = new JCheckBox("Silent");
        silent.setSelected(false);
        panel.add(silent);

        return panel;
    }

    @Override
    protected String browserName() {
        return "Internet Explorer";
    }

    @Override
    protected String getWikiPage() {
        return "InternetExplorerConfig";
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