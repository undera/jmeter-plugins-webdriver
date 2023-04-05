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
    public void shouldResetValuesOnClearGui() {
        gui.driverPath.setText("path");
        gui.chromeBinaryPath.setText("path/binary");
        gui.headless.setSelected(true);
        gui.acceptInsecureCerts.setSelected(true);
        gui.chromeAdditionalArgs.setText("additional");

        gui.clearGui();

        assertThat(gui.driverPath.getText(), is("path to driver.exe of the relevant browser"));
        assertThat(gui.headless.isSelected(), is(false));
        assertThat(gui.acceptInsecureCerts.isSelected(), is(false));
        assertThat(gui.chromeAdditionalArgs.getText(), is(""));
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
