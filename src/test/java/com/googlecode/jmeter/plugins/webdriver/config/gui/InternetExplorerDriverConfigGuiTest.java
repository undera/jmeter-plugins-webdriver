package com.googlecode.jmeter.plugins.webdriver.config.gui;

import com.googlecode.jmeter.plugins.webdriver.config.InternetExplorerDriverConfig;
import kg.apc.emulators.TestJMeterUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.event.FocusEvent;

public class InternetExplorerDriverConfigGuiTest {

    private InternetExplorerDriverConfigGui gui;

    @BeforeClass
    public static void setupJMeterEnv() {
        TestJMeterUtils.createJmeterEnv();
    }

    @Before
    public void createConfig() {
        gui = new InternetExplorerDriverConfigGui();
    }

    @Test
    public void shouldReturnStaticLabel() {
        assertThat(gui.getStaticLabel(), containsString("Internet Explorer Driver Config"));
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
    public void shouldReturnInternetExplorerDriverConfig() {
        assertThat(gui.createTestElement(), is(instanceOf(InternetExplorerDriverConfig.class)));
    }

    @Test
    public void shouldSetInternetExplorerDriverPath() {
        gui.driverPath.setText("iedriver");
        final InternetExplorerDriverConfig testElement = (InternetExplorerDriverConfig) gui.createTestElement();
        assertThat(testElement.getDriverPath(), is("iedriver"));
    }

    @Test
    public void shouldSetCleanSessionEnabled() {
        gui.ensureCleanSession.setSelected(true);
        final InternetExplorerDriverConfig testElement = (InternetExplorerDriverConfig) gui.createTestElement();
        assertThat(testElement.isEnsureCleanSession(), is(true));
    }

    @Test
    public void shouldSetProtectedModeEnabled() {
        gui.ignoreProtectedMode.setSelected(true);
        final InternetExplorerDriverConfig testElement = (InternetExplorerDriverConfig) gui.createTestElement();
        assertThat(testElement.isIgnoreProtectedMode(), is(true));
    }

    @Test
    public void shouldSetSilentEnabled() {
        gui.silent.setSelected(true);
        final InternetExplorerDriverConfig testElement = (InternetExplorerDriverConfig) gui.createTestElement();
        assertThat(testElement.isSilent(), is(true));
    }


    @Test
    public void shouldResetValuesOnClearGui() {
        gui.driverPath.setText("path");
        gui.ensureCleanSession.setSelected(true);
        gui.ignoreProtectedMode.setSelected(true);
        gui.silent.setSelected(true);
        gui.initialBrowserUrl.setText("path");
        gui.fileUploadDialogTimeout.setText("300");

        gui.clearGui();

        assertThat(gui.driverPath.getText(), is("path to driver.exe of the relevant browser"));
        assertThat(gui.ensureCleanSession.isSelected(), is(false));
        assertThat(gui.ignoreProtectedMode.isSelected(), is(false));
        assertThat(gui.silent.isSelected(), is(false));
        assertThat(gui.initialBrowserUrl.getText(), is("https://www.bing.com/"));
        assertThat(gui.fileUploadDialogTimeout.getText(), is("1000"));
    }

    @Test
    public void shouldSetInternetExplorerDriverPathOnConfigure() {
        InternetExplorerDriverConfig config = new InternetExplorerDriverConfig();
        config.setDriverPath("iedriver.path");
        gui.configure(config);

        assertThat(gui.driverPath.getText(), is(config.getDriverPath()));
    }

    @Test
    public void shouldSetFileUploadDialogTimeoutOnConfigure() {
        InternetExplorerDriverConfig config = new InternetExplorerDriverConfig();
        config.setFileUploadDialogTimeout(300);
        gui.configure(config);

        assertThat(gui.fileUploadDialogTimeout.getText(), is(String.valueOf(config.getFileUploadDialogTimeout())));
    }

    @Test
    public void shouldSetCleanSessionOnConfigure() {
        InternetExplorerDriverConfig config = new InternetExplorerDriverConfig();
        config.setEnsureCleanSession(true);
        gui.configure(config);

        assertThat(gui.ensureCleanSession.isSelected(), is(config.isEnsureCleanSession()));
    }

    @Test
    public void shouldSetProtectedModeOnConfigure() {
        InternetExplorerDriverConfig config = new InternetExplorerDriverConfig();
        config.setIgnoreProtectedMode(true);
        gui.configure(config);

        assertThat(gui.ignoreProtectedMode.isSelected(), is(config.isIgnoreProtectedMode()));
    }

    @Test
    public void shouldSetSilentOnConfigure() {
        InternetExplorerDriverConfig config = new InternetExplorerDriverConfig();
        config.setSilent(true);
        gui.configure(config);

        assertThat(gui.silent.isSelected(), is(config.isSilent()));
    }

    @Test
	public void shouldFireAMessageWindowWhenTheFocusIsLost() throws Exception {
        gui.initialBrowserUrl.setText("badURL");
        FocusEvent focusEvent = new FocusEvent(gui.initialBrowserUrl, 1);
        gui.focusLost(focusEvent);
        assertEquals("The URL is malformed", gui.IEerrorMsg.getText());
	}

    @Test
	public void shouldNotFireAMessageWindowWhenTheURLIsCorrect() throws Exception {
        gui.initialBrowserUrl.setText("http://my.awesomegrid.com");
        FocusEvent focusEvent = new FocusEvent(gui.initialBrowserUrl, 1);
        gui.focusLost(focusEvent);
        assertEquals("", gui.IEerrorMsg.getText());
	}
}
