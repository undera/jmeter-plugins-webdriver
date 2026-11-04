package com.googlecode.jmeter.plugins.webdriver.config.gui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.jmeter.plugins.webdriver.config.ChromeDriverConfig;

import kg.apc.emulators.TestJMeterUtils;

public class ChromeDriverConfigGuiTest {

    private ChromeDriverConfigGui gui;

    @BeforeClass
    public static void setupJMeterEnv() {
        TestJMeterUtils.createJmeterEnv();
    }

    @Before
    public void createConfig() {
        gui = new ChromeDriverConfigGui();
    }

    @Test
    public void shouldReturnStaticLabel() {
        assertThat(gui.getStaticLabel(), containsString("Chrome Driver Config"));
    }

    @Test
    public void shouldReturnWikiPage() {
        assertThat(gui.getWikiPage(), is("https://github.com/undera/jmeter-plugins-webdriver"));
    }

    @Test
    public void shouldReturnCanonicalClassNameAsLabelResource() {
        assertThat(gui.getLabelResource(), is(gui.getClass().getCanonicalName()));
    }

    @Test
    public void shouldReturnChromeDriverConfig() {
        assertThat(gui.createTestElement(), is(instanceOf(ChromeDriverConfig.class)));
    }

    @Test
    public void shouldSetChromeDriverPath() {
        gui.driverPath.setText("chromedriver");
        final ChromeDriverConfig testElement = (ChromeDriverConfig) gui.createTestElement();
        assertThat(testElement.getDriverPath(), is("chromedriver"));
    }

    @Test
    public void shouldSetHeadlessEnabled() {
        gui.headless.setSelected(true);
        final ChromeDriverConfig testElement = (ChromeDriverConfig) gui.createTestElement();
        assertThat(testElement.isHeadless(), is(true));
    }

    @Test
    public void shouldSetInsecureCertsEnabled() {
        gui.acceptInsecureCerts.setSelected(true);
        final ChromeDriverConfig testElement = (ChromeDriverConfig) gui.createTestElement();
        assertThat(testElement.isAcceptInsecureCerts(), is(true));
    }

    @Test
    public void shouldSetAdditionalArgs() {
        gui.chromeAdditionalArgs.setText("additionalArgs");
        final ChromeDriverConfig testElement = (ChromeDriverConfig) gui.createTestElement();
        assertThat(testElement.getChromeAdditionalArgs(), is("additionalArgs"));
    }

    @Test
    public void shouldSetCommandLineOptions() {
        gui.chromeDisableGpu.setSelected(true);
        gui.chromeDisableDevShmUsage.setSelected(true);
        gui.chromeDisableExtensions.setSelected(true);
        gui.chromeDisableBackgroundNetworking.setSelected(true);
        gui.chromeMetricsRecordingOff.setSelected(true);
        gui.chromeMuteAudio.setSelected(true);
        gui.chromeNoSandbox.setSelected(true);

        final ChromeDriverConfig testElement = (ChromeDriverConfig) gui.createTestElement();

        assertThat(testElement.isDisableGpu(), is(true));
        assertThat(testElement.isDisableDevShmUsage(), is(true));
        assertThat(testElement.isDisableExtensions(), is(true));
        assertThat(testElement.isDisableBackgroundNetworking(), is(true));
        assertThat(testElement.isMetricsRecordingOff(), is(true));
        assertThat(testElement.isMuteAudio(), is(true));
        assertThat(testElement.isNoSandboxEnabled(), is(true));
    }

    @Test
    public void shouldResetValuesOnClearGui() {
        gui.driverPath.setText("path");
        gui.chromeBinaryPath.setText("path/binary");
        gui.headless.setSelected(true);
        gui.acceptInsecureCerts.setSelected(true);
        gui.chromeAdditionalArgs.setText("additional");
        gui.chromeDisableGpu.setSelected(true);
        gui.chromeDisableDevShmUsage.setSelected(true);
        gui.chromeDisableExtensions.setSelected(true);
        gui.chromeDisableBackgroundNetworking.setSelected(true);
        gui.chromeMetricsRecordingOff.setSelected(true);
        gui.chromeMuteAudio.setSelected(true);
        gui.chromeNoSandbox.setSelected(true);

        gui.clearGui();

        assertThat(gui.driverPath.getText(), is("path to driver.exe of the relevant browser"));
        assertThat(gui.headless.isSelected(), is(false));
        assertThat(gui.acceptInsecureCerts.isSelected(), is(false));
        assertThat(gui.chromeAdditionalArgs.getText(), is(""));
        assertThat(gui.chromeDisableGpu.isSelected(), is(false));
        assertThat(gui.chromeDisableDevShmUsage.isSelected(), is(false));
        assertThat(gui.chromeDisableExtensions.isSelected(), is(false));
        assertThat(gui.chromeDisableBackgroundNetworking.isSelected(), is(false));
        assertThat(gui.chromeMetricsRecordingOff.isSelected(), is(false));
        assertThat(gui.chromeMuteAudio.isSelected(), is(false));
        assertThat(gui.chromeNoSandbox.isSelected(), is(false));
    }

    @Test
    public void shouldSetChromeDriverPathOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setDriverPath("chromedriver.path");
        gui.configure(config);

        assertThat(gui.driverPath.getText(), is(config.getDriverPath()));
    }

    @Test
    public void shouldSetBinaryPathOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setChromeBinaryPath("chromedriver.binary_path");
        gui.configure(config);

        assertThat(gui.chromeBinaryPath.getText(), is(config.getChromeBinaryPath()));
    }

    @Test
    public void shouldSetHeadlessEnabledOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setHeadless(true);
        gui.configure(config);

        assertThat(gui.headless.isSelected(), is(config.isHeadless()));
    }

    @Test
    public void shouldSetInsecureCertsEnabledOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setAcceptInsecureCerts(true);
        gui.configure(config);

        assertThat(gui.acceptInsecureCerts.isSelected(), is(config.isAcceptInsecureCerts()));
    }

    @Test
    public void shouldSetCommandLineOptionsOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setDisableGpu(true);
        config.setDisableDevShmUsage(true);
        config.setDisableExtensions(true);
        config.setDisableBackgroundNetworking(true);
        config.setMetricsRecordingOff(true);
        config.setMuteAudio(true);
        config.setNoSandboxEnabled(true);
        gui.configure(config);

        assertThat(gui.chromeDisableGpu.isSelected(), is(config.isDisableGpu()));
        assertThat(gui.chromeDisableDevShmUsage.isSelected(), is(config.isDisableDevShmUsage()));
        assertThat(gui.chromeDisableExtensions.isSelected(), is(config.isDisableExtensions()));
        assertThat(gui.chromeDisableBackgroundNetworking.isSelected(), is(config.isDisableBackgroundNetworking()));
        assertThat(gui.chromeMetricsRecordingOff.isSelected(), is(config.isMetricsRecordingOff()));
        assertThat(gui.chromeMuteAudio.isSelected(), is(config.isMuteAudio()));
        assertThat(gui.chromeNoSandbox.isSelected(), is(config.isNoSandboxEnabled()));
    }

    @Test
	public void shouldEnableProxyAndBrowser() throws Exception {
		assertThat(gui.isBrowser(), is(true));
		assertThat(gui.isProxyEnabled(), is(true));
	}

    @Test
    public void shouldSetAdditionalArgsOnConfigure() {
        ChromeDriverConfig config = new ChromeDriverConfig();
        config.setChromeAdditionalArgs("chromedriver.additional_args");
        gui.configure(config);
       assertThat(gui.chromeAdditionalArgs.getText(), is(config.getChromeAdditionalArgs()));
    }
}
