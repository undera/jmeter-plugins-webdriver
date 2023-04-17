package com.googlecode.jmeter.plugins.webdriver.config.gui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.jmeter.plugins.webdriver.config.EdgeDriverConfig;

import kg.apc.emulators.TestJMeterUtils;

public class EdgeDriverConfigGuiTest {

    private EdgeDriverConfigGui gui;

    @BeforeClass
    public static void setupJMeterEnv() {
        TestJMeterUtils.createJmeterEnv();
    }

    @Before
    public void createConfig() {
        gui = new EdgeDriverConfigGui();
    }

    @Test
    public void shouldReturnStaticLabel() {
        assertThat(gui.getStaticLabel(), containsString("Edge Driver Config"));
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
    public void shouldReturnEdgeDriverConfig() {
        assertThat(gui.createTestElement(), is(instanceOf(EdgeDriverConfig.class)));
    }

    @Test
    public void shouldSetEdgeDriverPath() {
        gui.driverPath.setText("edgedriver");
        final EdgeDriverConfig testElement = (EdgeDriverConfig) gui.createTestElement();
        assertThat(testElement.getDriverPath(), is("edgedriver"));
    }

    @Test
    public void shouldSetHeadlessEnabled() {
        gui.headless.setSelected(true);
        final EdgeDriverConfig testElement = (EdgeDriverConfig) gui.createTestElement();
        assertThat(testElement.isHeadless(), is(true));
    }

    @Test
    public void shouldSetInsecureCertsEnabled() {
        gui.acceptInsecureCerts.setSelected(true);
        final EdgeDriverConfig testElement = (EdgeDriverConfig) gui.createTestElement();
        assertThat(testElement.isAcceptInsecureCerts(), is(true));
    }

    @Test
    public void shouldSetAdditionalArgs() {
        gui.edgeAdditionalArgs.setText("additionalArgs");
        final EdgeDriverConfig testElement = (EdgeDriverConfig) gui.createTestElement();
        assertThat(testElement.getEdgeAdditionalArgs(), is("additionalArgs"));
    }

    @Test
    public void shouldResetValuesOnClearGui() {
        gui.driverPath.setText("path");
        gui.edgeBinaryPath.setText("path/binary");
        gui.headless.setSelected(true);
        gui.acceptInsecureCerts.setSelected(true);
        gui.edgeAdditionalArgs.setText("additional");

        gui.clearGui();

        assertThat(gui.driverPath.getText(), is("path to driver.exe of the relevant browser"));
        assertThat(gui.headless.isSelected(), is(false));
        assertThat(gui.acceptInsecureCerts.isSelected(), is(false));
        assertThat(gui.edgeAdditionalArgs.getText(), is(""));
    }

    @Test
    public void shouldSetEdgeDriverPathOnConfigure() {
        EdgeDriverConfig config = new EdgeDriverConfig();
        config.setDriverPath("edgedriver.path");
        gui.configure(config);

        assertThat(gui.driverPath.getText(), is(config.getDriverPath()));
    }

    @Test
    public void shouldSetBinaryPathOnConfigure() {
        EdgeDriverConfig config = new EdgeDriverConfig();
        config.setEdgeBinaryPath("edgedriver.binary_path");
        gui.configure(config);

        assertThat(gui.edgeBinaryPath.getText(), is(config.getEdgeBinaryPath()));
    }

    @Test
    public void shouldSetHeadlessEnabledOnConfigure() {
        EdgeDriverConfig config = new EdgeDriverConfig();
        config.setHeadless(true);
        gui.configure(config);

        assertThat(gui.headless.isSelected(), is(config.isHeadless()));
    }

    @Test
    public void shouldSetInsecureCertsEnabledOnConfigure() {
        EdgeDriverConfig config = new EdgeDriverConfig();
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
        EdgeDriverConfig config = new EdgeDriverConfig();
        config.setEdgeAdditionalArgs("edgedriver.additional_args");
        gui.configure(config);
       assertThat(gui.edgeAdditionalArgs.getText(), is(config.getEdgeAdditionalArgs()));
    }
}
