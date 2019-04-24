package com.googlecode.jmeter.plugins.webdriver.config;

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UselessFileDetector;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoteDriverConfig.class)
public class RemoteDriverConfigTest {

    private RemoteDriverConfig config;
    private JMeterVariables variables;

    @Before
    public void createConfig() {
        config = new RemoteDriverConfig();
        variables = new JMeterVariables();
        config.setCapability(RemoteCapability.CHROME);
        JMeterContextService.getContext().setVariables(variables);
    }

    @After
    public void resetConfig() {
        config.clearThreadBrowsers();
        JMeterContextService.getContext().setVariables(null);
    }
    
    @Test
	public void shouldSetTheCapability() throws Exception {
		assertThat(config.getCapability(), is(RemoteCapability.CHROME));
		config.setCapability(RemoteCapability.FIREFOX);
		assertThat(config.getCapability(), is(RemoteCapability.FIREFOX));
		config.setCapability(RemoteCapability.INTERNET_EXPLORER);
		assertThat(config.getCapability(), is(RemoteCapability.INTERNET_EXPLORER));
		config.setCapability(RemoteCapability.PHANTOMJS);
		assertThat(config.getCapability(), is(RemoteCapability.PHANTOMJS));
	}

    @Test
    public void shouldBeAbleToSerialiseAndDeserialise() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);

        output.writeObject(config);
        output.flush();
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        final RemoteDriverConfig deserializedConfig = (RemoteDriverConfig) input.readObject();

        assertThat(deserializedConfig, is(config));
    }

    @Test
    public void shouldCreateRemoteDriver() throws Exception {
    	config.setSeleniumGridUrl("http://my.awesomegrid.com");
        RemoteWebDriver mockRemoteWebDriver = Mockito.mock(RemoteWebDriver.class);
        whenNew(RemoteWebDriver.class)
            .withParameterTypes(URL.class, Capabilities.class)
            .withArguments(isA(URL.class), isA(Capabilities.class))
            .thenReturn(mockRemoteWebDriver);

        final RemoteWebDriver browser = config.createBrowser();

        assertThat(browser, is(mockRemoteWebDriver));
        verifyNew(RemoteWebDriver.class, times(1)).withArguments(isA(URL.class), isA(Capabilities.class));
    }

    @Test
    public void shouldHaveProxyInCapability() {
        final Capabilities capabilities = config.createCapabilities();
        assertThat(capabilities.getCapability(CapabilityType.PROXY), is(notNullValue()));
        assertThat(capabilities.getCapability(ChromeOptions.CAPABILITY), is(notNullValue()));
        assertThat(capabilities.isJavascriptEnabled(), is(true));
    }

    @Test
    public void shouldHaveHeadlessInChromeOptionsWhenEnabled() {
        config.setHeadlessEnabled(true);
        final Capabilities capabilities = config.createCapabilities();
        TreeMap capability = (TreeMap) capabilities.getCapability(ChromeOptions.CAPABILITY);
        assertThat(capability, is(notNullValue()));
        List<String> args = (List<String>) capability.get("args");
        assertThat(args, is(notNullValue()));
        assertEquals(1, args.size());
        assertEquals("--headless", args.get(0));
    }

    @Test
    public void shouldNotHaveHeadlessInChromeOptionsWhenDisabled() {
        config.setHeadlessEnabled(false);
        final Capabilities capabilities = config.createCapabilities();
        TreeMap capability = (TreeMap) capabilities.getCapability(ChromeOptions.CAPABILITY);
        assertThat(capability, is(notNullValue()));
        List<String> args = (List<String>) capability.get("args");
        assertThat(args, is(notNullValue()));
        assertEquals(0, args.size());
    }

    @Test
    public void should() throws Exception {

    }

    @Test
    public void shouldRevertToDefaultFileLocator() {
        assertEquals(FileDetectorOption.USELESS, config.getFileDetectorOption());
    }

    @Test
    public void shouldProduceLocalFileLocator() {
        config.setFileDetectorOption(FileDetectorOption.LOCAL);
        assertTrue(config.createFileDetector() instanceof LocalFileDetector);
    }

    @Test
    public void shouldProduceUselessFileLocator() {
        config.setFileDetectorOption(FileDetectorOption.USELESS);
        assertTrue(config.createFileDetector() instanceof UselessFileDetector);
    }
    
    @Test
	public void shouldThrowAnExceptionWhenTheURLIsMalformed() throws Exception {
    	try{
    		config.setSeleniumGridUrl("BadURL");
    		config.createBrowser();
    		fail();
    	} catch (Exception unit){
    		assertThat(unit, instanceOf(RuntimeException.class));
    		assertThat(unit.getMessage(), is("java.net.MalformedURLException: no protocol: BadURL"));
    	}
	}
}
