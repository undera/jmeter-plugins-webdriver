package com.googlecode.jmeter.plugins.webdriver.config.gui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.event.FocusEvent;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.jmeter.plugins.webdriver.config.RemoteBrowser;
import com.googlecode.jmeter.plugins.webdriver.config.RemoteDriverConfig;
import kg.apc.emulators.TestJMeterUtils;

public class RemoteDriverConfigGuiTest {

    private RemoteDriverConfigGui gui;

    @BeforeClass
    public static void setupJMeterEnv() {
        TestJMeterUtils.createJmeterEnv();
    }

    @Before
    public void createConfig() {
        gui = new RemoteDriverConfigGui();
    }

    @Test
    public void shouldReturnStaticLabel() {
        assertThat(gui.getStaticLabel(), containsString("Remote Driver Config"));
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
    public void shouldReturnRemoteDriverConfig() {
        assertThat(gui.createTestElement(), is(instanceOf(RemoteDriverConfig.class)));
    }
    
    @Test
	public void shouldSetTheSeleniumNodeUrl() throws Exception {
		gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
		final RemoteDriverConfig testElement = (RemoteDriverConfig) gui.createTestElement();
        assertThat(testElement.getSeleniumGridUrl(), is("http://my.awesomegrid.com"));
	}

    @Test
    public void shouldResetValuesOnClearGui() {
        gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
        
        gui.clearGui();

        assertThat(gui.remoteSeleniumGridText.getText(), is(StringUtils.EMPTY));
    }

    @Test
    public void shouldSetRemoteDriverConfigOnConfigure() {
        RemoteDriverConfig config = new RemoteDriverConfig();
        config.setSeleniumGridUrl("my.awesome.grid.com");
        config.setSelectedBrowser(RemoteBrowser.FIREFOX);
        gui.configure(config);

        assertThat(gui.remoteSeleniumGridText.getText(), is(config.getSeleniumGridUrl()));
        assertThat((RemoteBrowser)gui.browserCapabilitiesComboBox.getSelectedItem(), is(config.getSelectedBrowser()));
        assertFalse(gui.headless.isSelected());
    }

    @Test
    public void shouldSetHeadlessEnabledOnConfigure() {
        RemoteDriverConfig config = new RemoteDriverConfig();
        config.setSeleniumGridUrl("my.awesome.grid.com");
        config.setSelectedBrowser(RemoteBrowser.CHROME);
        config.setHeadless(true);
        gui.configure(config);
        assertTrue(gui.headless.isSelected());
    }

    @Test
	public void shouldFireAMessageWindowWhenTheFocusIsLost() throws Exception {
    	gui.remoteSeleniumGridText.setText("badURL");
    	FocusEvent focusEvent = new FocusEvent(gui.remoteSeleniumGridText, 1);
    	gui.focusLost(focusEvent);
        assertEquals("The selenium grid URL is malformed", gui.RemoteErrorMsg.getText());
	}

    @Test
	public void shouldNotFireAMessageWindowWhenTheURLIsCorrect() throws Exception {
    	gui.remoteSeleniumGridText.setText("http://my.awesomegrid.com");
    	FocusEvent focusEvent = new FocusEvent(gui.remoteSeleniumGridText, 1);
    	gui.focusLost(focusEvent);
        assertEquals("", gui.RemoteErrorMsg.getText());
	}
    
    @Test
	public void shouldEnableProxyAndNotBrowser() throws Exception {
		assertThat(gui.isBrowser(), is(false));
		assertThat(gui.isProxyEnabled(), is(true));
	}
}
