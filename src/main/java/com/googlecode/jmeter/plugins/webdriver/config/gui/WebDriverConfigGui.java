package com.googlecode.jmeter.plugins.webdriver.config.gui;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;

import com.googlecode.jmeter.plugins.webdriver.config.RemoteBrowser;
import com.googlecode.jmeter.plugins.webdriver.config.WebDriverConfig;
import com.googlecode.jmeter.plugins.webdriver.proxy.ProxyType;

import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.Grid;

public abstract class WebDriverConfigGui extends AbstractConfigGui implements FocusListener, ItemListener {

	private static final long serialVersionUID = 100L;

    private static final int Default_FileUploadDialogTimeout = 1000;

	private static final String DEFAULT_NO_PROXY_LIST = "localhost";
	private static final int DEFAULT_PROXY_PORT = 8080;
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();
	private static final int PROXY_FIELD_INDENT = 28;
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		NUMBER_FORMAT.setGroupingUsed(false);
	}

	// Shared variables
	JTextField driverPath;
	JCheckBox acceptInsecureCerts;
	JCheckBox maximizeBrowser;
	JCheckBox headless;
	JCheckBox devMode;
	JCheckBox recreateBrowserOnIterationStart;

	// Remote variables
	JTextField remoteSeleniumGridText;
	JComboBox<?> browserCapabilitiesComboBox;
	JCheckBox localFileDetector;
	JLabel RemoteErrorMsg;

	// Chrome variables
	JTextField chromeAdditionalArgs;
	JTextField chromeBinaryPath;

	// Edge variables
	JTextField edgeAdditionalArgs;
	JTextField edgeBinaryPath;

	// Firefox variables
	JTextField userAgentOverrideText;
	JCheckBox userAgentOverrideCheckbox;
	JCheckBox ntlmOverrideCheckbox;
	private Grid extensions;
	private Grid preferences;

	// InternetExporer variables
	JFormattedTextField fileUploadDialogTimeout;
    JCheckBox ensureCleanSession;
    JCheckBox ignoreProtectedMode;
    JCheckBox silent;
	JTextField initialBrowserUrl;
	JLabel IEerrorMsg;

	// Proxy variables
	JRadioButton autoDetectProxy;
	JRadioButton directProxy; // synonymous with no proxy
	JTextField ftpProxyHost;
	JFormattedTextField ftpProxyPort;
	JTextField httpProxyHost;
	JFormattedTextField httpProxyPort;
	JTextField httpsProxyHost;
	JFormattedTextField httpsProxyPort;
	JRadioButton manualProxy;
	JTextArea noProxyList;
	JTextField pacUrl;
	JRadioButton pacUrlProxy;
	JTextField socksProxyHost;
	JFormattedTextField socksProxyPort;
	JRadioButton systemProxy;
	JCheckBox useHttpSettingsForAllProtocols;
	JTextArea customCapabilitiesTextArea;

	public static final String WIKIPAGE = "https://github.com/undera/jmeter-plugins-webdriver";
	public String getWikiPage() {
		return WIKIPAGE;
	}

	abstract String browserName();

	protected boolean isBrowser() {
		return false;
	}

	protected boolean isProxyEnabled() {
		return false;
	}

	public WebDriverConfigGui() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());
		add(JMeterPluginsUtils.addHelpLinkToPanel(makeTitlePanel(), getWikiPage()), BorderLayout.NORTH);

		final JTabbedPane tabbedPane = new JTabbedPane();
		switch(browserName()) {
		case "chrome":
			tabbedPane.add("Driver", createMainPanel());
			tabbedPane.add("Options", crteChromeOptionsPanel());
			break;

		case "edge":
			tabbedPane.add("Driver", createMainPanel());
			tabbedPane.add("Options", crteEdgeOptionsPanel());
			break;

		case "firefox":
			tabbedPane.add("Driver", createMainPanel());
			tabbedPane.add("Options", crteFirefoxOptionsPanel());
			break;

		case "internet explorer":
			tabbedPane.add("Driver", createMainPanel());
			tabbedPane.add("Options", crteIEOptionsPanel());
			break;

		case "HtmlUnit":
			tabbedPane.add("HtmlUnit", createMainPanel());
			break;

		case "Remote":
			tabbedPane.add("Remote", createMainPanel());
			tabbedPane.add("Chrome", crteChromeOptionsPanel());
			tabbedPane.add("Edge", crteEdgeOptionsPanel());
			tabbedPane.add("Firefox", crteFirefoxOptionsPanel());
			tabbedPane.add("IE", crteIEOptionsPanel());
			break;

		default:
			break;
		}

		// Proxy tab
		if (isProxyEnabled()) {
			tabbedPane.add("Proxy", createProxyPanel());
		}
		tabbedPane.add("Capabilities Management", createCustomCapabilities());
		add(tabbedPane, BorderLayout.CENTER);
	}

	protected JPanel createMainPanel() {
		JPanel panel = new VerticalPanel();

		if (isBrowser()) {
			JPanel driverPanel = new HorizontalPanel();
			JLabel driverLabel = new JLabel("Path to Driver");
			driverPanel.add(driverLabel);
			driverPath = new JTextField();
			driverPanel.add(driverPath);
			panel.add(driverPanel);

			devMode = new JCheckBox("Development Mode (keep browser opened on error)");
			devMode.setSelected(false);
			panel.add(devMode);
		}

		if ((browserName().equals("Remote"))) {
			JLabel remoteUrlLabel = new JLabel();
			remoteUrlLabel.setText("Selenium Grid URL");
			panel.add(remoteUrlLabel);
			remoteSeleniumGridText = new JTextField();
			remoteSeleniumGridText.setEnabled(true);
			remoteSeleniumGridText.addFocusListener((FocusListener) this);
			panel.add(remoteSeleniumGridText);

			panel.add(RemoteErrorMsg = new JLabel());
			RemoteErrorMsg.setForeground(Color.red);

			JLabel browserCapabilitiesLabel = new JLabel();
			browserCapabilitiesLabel.setText("Remote Browser Capability");
			panel.add(browserCapabilitiesLabel);
			browserCapabilitiesComboBox = new JComboBox<Object>(RemoteBrowser.values());
			panel.add(browserCapabilitiesComboBox);

			localFileDetector = new JCheckBox("Local File Detector");
			panel.add(localFileDetector);
		}

		acceptInsecureCerts = new JCheckBox("Accept Insecure Certs");
		panel.add(acceptInsecureCerts);

		if (!(browserName().equals("HtmlUnit"))) {
			if (!(browserName().equals("internet explorer"))) {
				headless = new JCheckBox("Headless");
				headless.setSelected(false);
				headless.setEnabled(true);
				panel.add(headless);
			}
			maximizeBrowser = new JCheckBox("Maximize browser window");
			maximizeBrowser.setSelected(true);
			panel.add(maximizeBrowser);
		}

		recreateBrowserOnIterationStart = new JCheckBox("Create a new Browser at the start of each iteration");
		recreateBrowserOnIterationStart.setSelected(false);
		panel.add(recreateBrowserOnIterationStart);

		return panel;
	}

	private JPanel crteChromeOptionsPanel() {
		final JPanel browserPanel = new VerticalPanel();

		final JPanel binaryPathPanel = new HorizontalPanel();
		final JLabel binaryPathLabel = new JLabel("Binary (if in non-standard location)");
		chromeBinaryPath = new JTextField("");
		binaryPathPanel.add(binaryPathLabel);
		binaryPathPanel.add(chromeBinaryPath);
		browserPanel.add(binaryPathPanel);

		final JPanel additionalArgsPanel = new HorizontalPanel();
		final JLabel additionalArgsLabel = new JLabel("Additional arguments");
		chromeAdditionalArgs = new JTextField();
		additionalArgsPanel.add(additionalArgsLabel);
		additionalArgsPanel.add(chromeAdditionalArgs);
		browserPanel.add(additionalArgsPanel);

		return browserPanel;
	}

	private JPanel crteEdgeOptionsPanel() {
		final JPanel browserPanel = new VerticalPanel();

		final JPanel binaryPathPanel = new HorizontalPanel();
		final JLabel binaryPathLabel = new JLabel("Binary (if in non-standard location)");
		edgeBinaryPath = new JTextField("");
		binaryPathPanel.add(binaryPathLabel);
		binaryPathPanel.add(edgeBinaryPath);
		browserPanel.add(binaryPathPanel);

		final JPanel additionalArgsPanel = new HorizontalPanel();
		final JLabel additionalArgsLabel = new JLabel("Additional arguments");
		edgeAdditionalArgs = new JTextField();
		additionalArgsPanel.add(additionalArgsLabel);
		additionalArgsPanel.add(edgeAdditionalArgs);
		browserPanel.add(additionalArgsPanel);

		return browserPanel;
	}

	private JPanel crteFirefoxOptionsPanel() {
		final JPanel browserPanel = new VerticalPanel();

		userAgentOverrideCheckbox = new JCheckBox("Override User Agent");
		userAgentOverrideCheckbox.setSelected(false);
		userAgentOverrideCheckbox.setEnabled(true);
		userAgentOverrideCheckbox.addItemListener(this);
		browserPanel.add(userAgentOverrideCheckbox);

		userAgentOverrideText = new JTextField();
		userAgentOverrideText.setEnabled(false);
		browserPanel.add(userAgentOverrideText);

		ntlmOverrideCheckbox = new JCheckBox("Enable NTLM");
		ntlmOverrideCheckbox.setSelected(false);
		ntlmOverrideCheckbox.setEnabled(true);
		ntlmOverrideCheckbox.addItemListener(this);
		browserPanel.add(ntlmOverrideCheckbox);

		extensions = new Grid("Load Extensions", new String[] { "Path to XPI File" }, new Class[] { String.class },
				new String[] { "" });
		browserPanel.add(extensions);

		preferences = new Grid("Set Preferences", new String[] { "Name", "Value" },
				new Class[] { String.class, String.class }, new String[] { "", "" });
		browserPanel.add(preferences);

		return browserPanel;
	}

	private JPanel crteIEOptionsPanel() {
		final JPanel browserPanel = new VerticalPanel();

		// Initial URL
		JLabel initialUrlLabel = new JLabel();
		initialUrlLabel.setText("Initial Browser URL");
		browserPanel.add(initialUrlLabel);
		initialBrowserUrl = new JTextField();
		initialBrowserUrl.setEnabled(true);
		initialBrowserUrl.addFocusListener((FocusListener) this);
		browserPanel.add(initialBrowserUrl);

		browserPanel.add(IEerrorMsg = new JLabel());
		IEerrorMsg.setForeground(Color.red);

		// fileUploadDialogTimeout
        JPanel fileUploadDialogTimeoutPanel = new HorizontalPanel();
        JLabel fileUploadDialogTimeoutLabel = new JLabel("Wait for File Upload Dialog up to (ms)");
        fileUploadDialogTimeoutPanel.add(fileUploadDialogTimeoutLabel);
        fileUploadDialogTimeout = new JFormattedTextField(NUMBER_FORMAT);
        fileUploadDialogTimeout.setText(String.valueOf(Default_FileUploadDialogTimeout));
        fileUploadDialogTimeoutPanel.add(fileUploadDialogTimeout);
        browserPanel.add(fileUploadDialogTimeoutPanel);

        ensureCleanSession = new JCheckBox("Ensure Clean Session");
        ensureCleanSession.setSelected(false);
        browserPanel.add(ensureCleanSession);

        ignoreProtectedMode = new JCheckBox("Ignore Protected Mode Settings");
        ignoreProtectedMode.setSelected(false);
        browserPanel.add(ignoreProtectedMode);

        silent = new JCheckBox("Silent");
        silent.setSelected(false);
        browserPanel.add(silent);

		return browserPanel;
	}

	protected JPanel createProxyPanel() {
		JPanel mainPanel = new VerticalPanel();
		ButtonGroup group = new ButtonGroup();

		// Direct proxy
		directProxy = new JRadioButton("No proxy");
		group.add(directProxy);
		mainPanel.add(directProxy);

		// Auto-detect proxy
		autoDetectProxy = new JRadioButton("Auto-detect proxy settings for this network");
		group.add(autoDetectProxy);
		mainPanel.add(autoDetectProxy);

		// System proxy
		systemProxy = new JRadioButton("Use system proxy settings");
		group.add(systemProxy);
		mainPanel.add(systemProxy);

		createManualProxy(mainPanel, group);
		createPacUrlProxy(mainPanel, group);

		systemProxy.setSelected(true);
		return mainPanel;
	}

	private void createManualProxy(JPanel panel, ButtonGroup group) {
		manualProxy = new JRadioButton("Manual proxy configuration");
		group.add(manualProxy);
		panel.add(manualProxy);

		manualProxy.addItemListener(this);

		JPanel manualPanel = new VerticalPanel();
		manualPanel.setBorder(BorderFactory.createEmptyBorder(0, PROXY_FIELD_INDENT, 0, 0));

		httpProxyHost = new JTextField();
		httpProxyPort = new JFormattedTextField(NUMBER_FORMAT);
		httpProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		manualPanel.add(createProxyHostAndPortPanel(httpProxyHost, httpProxyPort, "HTTP Proxy:"));
		useHttpSettingsForAllProtocols = new JCheckBox("Use HTTP proxy server for all protocols");
		useHttpSettingsForAllProtocols.setSelected(true);
		useHttpSettingsForAllProtocols.setEnabled(false);
		useHttpSettingsForAllProtocols.addItemListener(this);
		manualPanel.add(useHttpSettingsForAllProtocols);

		httpsProxyHost = new JTextField();
		httpsProxyPort = new JFormattedTextField(NUMBER_FORMAT);
		httpsProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		manualPanel.add(createProxyHostAndPortPanel(httpsProxyHost, httpsProxyPort, "SSL Proxy:"));

		ftpProxyHost = new JTextField();
		ftpProxyPort = new JFormattedTextField(NUMBER_FORMAT);
		ftpProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		manualPanel.add(createProxyHostAndPortPanel(ftpProxyHost, ftpProxyPort, "FTP Proxy:"));

		socksProxyHost = new JTextField();
		socksProxyPort = new JFormattedTextField(NUMBER_FORMAT);
		socksProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		manualPanel.add(createProxyHostAndPortPanel(socksProxyHost, socksProxyPort, "SOCKS Proxy:"));

		manualPanel.add(createNoProxyPanel());

		panel.add(manualPanel);
	}

	private JPanel createProxyHostAndPortPanel(JTextField proxyHost, JTextField proxyPort, String label) {
		JPanel httpPanel = new HorizontalPanel();
		JLabel httpProxyHostLabel = new JLabel(label);
		httpPanel.add(httpProxyHostLabel);
		httpPanel.add(proxyHost);
		proxyHost.setEnabled(false);
		JLabel httpProxyPortLabel = new JLabel("Port:");
		httpPanel.add(httpProxyPortLabel);
		httpPanel.add(proxyPort);
		proxyPort.setEnabled(false);
		return httpPanel;
	}

	private JPanel createNoProxyPanel() {
		JPanel noProxyPanel = new VerticalPanel();
		JLabel noProxyListLabel = new JLabel("No Proxy for:");
		noProxyPanel.add(noProxyListLabel);

		noProxyList = new JTextArea(3, 10);
		noProxyList.setText(DEFAULT_NO_PROXY_LIST);
		noProxyList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		noProxyList.setEnabled(false);
		noProxyPanel.add(noProxyList);

		JLabel noProxyExample = new JLabel("Example: .jmeter.org, .com.au, 192.168.1.0/24");
		noProxyPanel.add(noProxyExample);

		return noProxyPanel;
	}

	private JPanel createCustomCapabilities() {
		JPanel capabilitiesPanel = new VerticalPanel();
		JLabel parsedJsonStatusLabel = new JLabel();
		final JLabel capabilityEditorLabel = new JLabel("Capabilities JSON Editor");
		customCapabilitiesTextArea = new JTextArea();
		JLabel previewLabel = new JLabel("Capabilities Preview");
		// TODO: would be nice to merge in ALL capabilities into previewPane,
		//  similar to how they would be used in webdriver network request,
		//  but currently Browser related capabilities are not exposed from WebDriverConfig.java
		final JTextArea previewTextArea = new JTextArea();
		previewTextArea.setEditable(false);
		previewTextArea.setWrapStyleWord(true);
		previewTextArea.setLineWrap(true);
		previewTextArea.setVisible(true);
		previewTextArea.setBackground(Color.BLACK);
		Map<String, Object> customCapabilitiesJsonMap = new LinkedHashMap<>();

		customCapabilitiesTextArea.getDocument().addDocumentListener(new DocumentListener() {
			private void doUpdate() {
				if (customCapabilitiesTextArea.getText().trim().isEmpty()) {
					previewTextArea.setText("");
					parsedJsonStatusLabel.setVisible(false);
				} else {
					parsedJsonStatusLabel.setText("Status: OK");
					parsedJsonStatusLabel.setForeground(Color.GREEN);
					parsedJsonStatusLabel.setVisible(true);
					previewTextArea.setForeground(Color.GREEN);

					try {
						LinkedHashMap parsedJson = mapper.readValue(customCapabilitiesTextArea.getText(), LinkedHashMap.class);
						customCapabilitiesJsonMap.clear();
						customCapabilitiesJsonMap.putAll(parsedJson);
						previewTextArea.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(customCapabilitiesJsonMap));
					} catch (JsonProcessingException e) {
						parsedJsonStatusLabel.setText("Status: ERROR");
						parsedJsonStatusLabel.setForeground(Color.RED);
						previewTextArea.setText(e.getMessage());
						previewTextArea.setForeground(Color.RED);
					}
				}
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				doUpdate();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				doUpdate();
			}

			@Override
			public void changedUpdate(DocumentEvent e) { }
		});

		capabilitiesPanel.add(capabilityEditorLabel);
		capabilitiesPanel.add(customCapabilitiesTextArea);
		capabilitiesPanel.add(parsedJsonStatusLabel);
		capabilitiesPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		capabilitiesPanel.add(previewLabel);
		capabilitiesPanel.add(previewTextArea);

		return capabilitiesPanel;
	}

	private void createPacUrlProxy(JPanel panel, ButtonGroup group) {
		pacUrlProxy = new JRadioButton("Automatic proxy configuration URL");
		group.add(pacUrlProxy);
		panel.add(pacUrlProxy);

		pacUrlProxy.addItemListener(this);

		JPanel pacUrlPanel = new HorizontalPanel();
		pacUrl = new JTextField();
		pacUrl.setEnabled(false);
		pacUrlPanel.add(pacUrl, BorderLayout.CENTER);
		pacUrlPanel.setBorder(BorderFactory.createEmptyBorder(0, PROXY_FIELD_INDENT, 0, 0));
		panel.add(pacUrlPanel);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Nothing to do
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getComponent().equals(remoteSeleniumGridText)) {
			if (!isValidUrl(remoteSeleniumGridText.getText())) {
				RemoteErrorMsg.setText("The selenium grid URL is malformed");
			} else {
			RemoteErrorMsg.setText("");
			}
		}
		if (e.getComponent().equals(initialBrowserUrl)) {
			if (!isValidUrl(initialBrowserUrl.getText())) {
				IEerrorMsg.setText("The URL is malformed");
			} else {
				IEerrorMsg.setText("");
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent itemEvent) {
		if (itemEvent.getSource() == pacUrlProxy) {
			pacUrl.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
		} else if (itemEvent.getSource() == manualProxy) {
			httpProxyHost.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
			httpProxyPort.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
			useHttpSettingsForAllProtocols.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
			noProxyList.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
			enableOtherProtocolsOnlyIfManualProxySelectedAndUseHttpSettingsIsNotSelected();
		} else if (itemEvent.getSource() == useHttpSettingsForAllProtocols) {
			enableOtherProtocolsOnlyIfManualProxySelectedAndUseHttpSettingsIsNotSelected();
		} else if (itemEvent.getSource() == userAgentOverrideCheckbox) {
			userAgentOverrideText.setEnabled(userAgentOverrideCheckbox.isSelected());
		}
	}

	private void enableOtherProtocolsOnlyIfManualProxySelectedAndUseHttpSettingsIsNotSelected() {
		final boolean enabledState = !useHttpSettingsForAllProtocols.isSelected() && manualProxy.isSelected();
		httpsProxyHost.setEnabled(enabledState);
		httpsProxyPort.setEnabled(enabledState);
		ftpProxyHost.setEnabled(enabledState);
		ftpProxyPort.setEnabled(enabledState);
		socksProxyHost.setEnabled(enabledState);
		socksProxyPort.setEnabled(enabledState);
	}

	@Override
	public void clearGui() {
		super.clearGui();

		acceptInsecureCerts.setSelected(false);
		recreateBrowserOnIterationStart.setSelected(false);

		// Browser common options
		if (isBrowser()) {
			driverPath.setText("path to driver.exe of the relevant browser");
			devMode.setSelected(false);
		}

		if (!(browserName().equals("HtmlUnit"))) {
			if (!(browserName().equals("internet explorer"))) {
				headless.setSelected(false);
			}
			maximizeBrowser.setSelected(true);
		}

		if ((browserName().equals("chrome")) || (browserName().equals("Remote"))) {
			chromeAdditionalArgs.setText("");
			chromeBinaryPath.setText("");
		}

		if ((browserName().equals("edge")) || (browserName().equals("Remote"))) {
			edgeAdditionalArgs.setText("");
			edgeBinaryPath.setText("");
		}

		if ((browserName().equals("firefox")) || (browserName().equals("Remote"))) {
			userAgentOverrideCheckbox.setSelected(false);
			userAgentOverrideText.setText("");
			ntlmOverrideCheckbox.setSelected(false);
			extensions.getModel().clearData();
			preferences.getModel().clearData();
		}

		if ((browserName().equals("internet explorer")) || (browserName().equals("Remote"))) {
	        fileUploadDialogTimeout.setText(String.valueOf(Default_FileUploadDialogTimeout));
	        ensureCleanSession.setSelected(false);
	        ignoreProtectedMode.setSelected(false);
	        silent.setSelected(false);
	        // Set a default initial page that is valid otherwise IeDriver may hang on startup...
	        initialBrowserUrl.setText("https://www.bing.com/");
		}
		customCapabilitiesTextArea.setText("");

		// Proxy
		clearProxy();
	}

	private void clearProxy() {
		systemProxy.setSelected(true);
		pacUrl.setText("");
		httpProxyHost.setText("");
		httpProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		useHttpSettingsForAllProtocols.setSelected(true);
		httpsProxyHost.setText("");
		httpsProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		ftpProxyHost.setText("");
		ftpProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		socksProxyHost.setText("");
		socksProxyPort.setText(String.valueOf(DEFAULT_PROXY_PORT));
		noProxyList.setText(DEFAULT_NO_PROXY_LIST);
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		if (element instanceof WebDriverConfig) {
			WebDriverConfig<?> webDriverConfig = (WebDriverConfig<?>) element;

			acceptInsecureCerts.setSelected(webDriverConfig.isAcceptInsecureCerts());
			recreateBrowserOnIterationStart.setSelected(webDriverConfig.isRecreateBrowserOnIterationStart());
			customCapabilitiesTextArea.setText(webDriverConfig.getCustomCapabilities());

			// Commmon browser configs
			if (isBrowser()) {
				driverPath.setText(webDriverConfig.getDriverPath());
				devMode.setSelected(webDriverConfig.isDevMode());
			}

			if (!(browserName().equals("HtmlUnit"))) {
				if (!(browserName().equals("internet explorer"))) {
					headless.setSelected(webDriverConfig.isHeadless());
				}
				maximizeBrowser.setSelected(webDriverConfig.isBrowserMaximized());
			}

			// Chrome configs
			if ((browserName().equals("chrome")) || (browserName().equals("Remote"))) {
				chromeAdditionalArgs.setText(webDriverConfig.getChromeAdditionalArgs());
				chromeBinaryPath.setText(webDriverConfig.getChromeBinaryPath());
			}

			// Edge configs
			if ((browserName().equals("edge")) || (browserName().equals("Remote"))) {
				edgeAdditionalArgs.setText(webDriverConfig.getEdgeAdditionalArgs());
				edgeBinaryPath.setText(webDriverConfig.getEdgeBinaryPath());
			}

			// Firefox configs
			if ((browserName().equals("firefox")) || (browserName().equals("Remote"))) {				
				userAgentOverrideCheckbox.setSelected(webDriverConfig.isUserAgentOverridden());
				userAgentOverrideText.setText(webDriverConfig.getUserAgentOverride());
				userAgentOverrideText.setEnabled(webDriverConfig.isUserAgentOverridden());
				JMeterProperty ext = webDriverConfig.getExtensions();
				if (!(ext instanceof NullProperty)) {
					JMeterPluginsUtils.collectionPropertyToTableModelRows((CollectionProperty) ext, extensions.getModel());
				}
				JMeterProperty pref = webDriverConfig.getPreferences();
				if (!(ext instanceof NullProperty)) {
					JMeterPluginsUtils.collectionPropertyToTableModelRows((CollectionProperty) pref,
							preferences.getModel());
				}
			}

			// IE configs
			if ((browserName().equals("internet explorer")) || (browserName().equals("Remote"))) {
	            fileUploadDialogTimeout.setText(String.valueOf(webDriverConfig.getFileUploadDialogTimeout()));
	            ensureCleanSession.setSelected(webDriverConfig.isEnsureCleanSession());
	            ignoreProtectedMode.setSelected(webDriverConfig.isIgnoreProtectedMode());
	            silent.setSelected(webDriverConfig.isSilent());
	            initialBrowserUrl.setText(webDriverConfig.getInitialIeUrl());
			}

			// Proxy
			configureProxy(webDriverConfig);
		}
	}

	private void configureProxy(WebDriverConfig<?> webDriverConfig) {
		switch (webDriverConfig.getProxyType()) {
		case DIRECT:
			directProxy.setSelected(true);
			break;
		case AUTO_DETECT:
			autoDetectProxy.setSelected(true);
			break;
		case MANUAL:
			manualProxy.setSelected(true);
			break;
		case PROXY_PAC:
			pacUrlProxy.setSelected(true);
			break;
		default:
			systemProxy.setSelected(true); // fallback to system proxy
		}
		pacUrl.setText(webDriverConfig.getProxyPacUrl());
		httpProxyHost.setText(webDriverConfig.getHttpHost());
		httpProxyPort.setText(String.valueOf(webDriverConfig.getHttpPort()));
		useHttpSettingsForAllProtocols.setSelected(webDriverConfig.isUseHttpSettingsForAllProtocols());
		httpsProxyHost.setText(webDriverConfig.getHttpsHost());
		httpsProxyPort.setText(String.valueOf(webDriverConfig.getHttpsPort()));
		ftpProxyHost.setText(webDriverConfig.getFtpHost());
		ftpProxyPort.setText(String.valueOf(webDriverConfig.getFtpPort()));
		socksProxyHost.setText(webDriverConfig.getSocksHost());
		socksProxyPort.setText(String.valueOf(webDriverConfig.getSocksPort()));
		noProxyList.setText(webDriverConfig.getNoProxyHost());
	}

	@Override
	public void modifyTestElement(TestElement element) {
		configureTestElement(element);
		if (element instanceof WebDriverConfig) {
			WebDriverConfig<?> webDriverConfig = (WebDriverConfig<?>) element;

			webDriverConfig.setAcceptInsecureCerts(acceptInsecureCerts.isSelected());
			webDriverConfig.setRecreateBrowserOnIterationStart(recreateBrowserOnIterationStart.isSelected());

			// Common browser elements
			if (isBrowser()) {
				webDriverConfig.setDriverPath(driverPath.getText());
				webDriverConfig.setDevMode(devMode.isSelected());
			}

			if (!(browserName().equals("HtmlUnit"))) {
				if (!(browserName().equals("internet explorer"))) {
					webDriverConfig.setHeadless(headless.isSelected());
				}
				webDriverConfig.setBrowserMaximized(maximizeBrowser.isSelected());
			}

			// Chrome elements
			if ((browserName().equals("chrome")) || (browserName().equals("Remote"))) {
				webDriverConfig.setChromeAdditionalArgs(chromeAdditionalArgs.getText());
				webDriverConfig.setChromeBinaryPath(chromeBinaryPath.getText());
			}

			// Edge elements
			if ((browserName().equals("edge")) || (browserName().equals("Remote"))) {
				webDriverConfig.setEdgeAdditionalArgs(edgeAdditionalArgs.getText());
				webDriverConfig.setEdgeBinaryPath(edgeBinaryPath.getText());
			}

			// Firefox elements
			if ((browserName().equals("firefox")) || (browserName().equals("Remote"))) {
				webDriverConfig.setUserAgentOverridden(userAgentOverrideCheckbox.isSelected());
				webDriverConfig.setNtlmSetting(ntlmOverrideCheckbox.isSelected());
				if (userAgentOverrideCheckbox.isSelected()) {
					webDriverConfig.setUserAgentOverride(userAgentOverrideText.getText());
				}
				webDriverConfig.setExtensions(extensions.getModel());
				webDriverConfig.setPreferences(preferences.getModel());
			}

			// IE elements
			if ((browserName().equals("internet explorer")) || (browserName().equals("Remote"))) {
				webDriverConfig.setFileUploadDialogTimeout(Integer.parseInt(fileUploadDialogTimeout.getText()));
				webDriverConfig.setEnsureCleanSession(ensureCleanSession.isSelected());
				webDriverConfig.setIgnoreProtectedMode(ignoreProtectedMode.isSelected());
				webDriverConfig.setSilent(silent.isSelected());
				webDriverConfig.setInitialIeUrl(initialBrowserUrl.getText());
			}

			// Proxy
			modifyProxy(webDriverConfig);

			webDriverConfig.setCustomCapabilities(customCapabilitiesTextArea.getText());
		}
	}

	private void modifyProxy(WebDriverConfig<?> webDriverConfig) {
		if (directProxy.isSelected()) {
			webDriverConfig.setProxyType(ProxyType.DIRECT);
		} else if (autoDetectProxy.isSelected()) {
			webDriverConfig.setProxyType(ProxyType.AUTO_DETECT);
		} else if (pacUrlProxy.isSelected()) {
			webDriverConfig.setProxyType(ProxyType.PROXY_PAC);
		} else if (manualProxy.isSelected()) {
			webDriverConfig.setProxyType(ProxyType.MANUAL);
		} else {
			webDriverConfig.setProxyType(ProxyType.SYSTEM); // fallback
		}
		webDriverConfig.setProxyPacUrl(pacUrl.getText());
		webDriverConfig.setHttpHost(httpProxyHost.getText());
		webDriverConfig.setHttpPort(Integer.parseInt(httpProxyPort.getText()));
		webDriverConfig.setUseHttpSettingsForAllProtocols(useHttpSettingsForAllProtocols.isSelected());
		webDriverConfig.setHttpsHost(httpsProxyHost.getText());
		webDriverConfig.setHttpsPort(Integer.parseInt(httpsProxyPort.getText()));
		webDriverConfig.setFtpHost(ftpProxyHost.getText());
		webDriverConfig.setFtpPort(Integer.parseInt(ftpProxyPort.getText()));
		webDriverConfig.setSocksHost(socksProxyHost.getText());
		webDriverConfig.setSocksPort(Integer.parseInt(socksProxyPort.getText()));
		webDriverConfig.setNoProxyHost(noProxyList.getText());
	}

	public boolean isValidUrl(String urlStr) {
		try {
			new URL(urlStr);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}