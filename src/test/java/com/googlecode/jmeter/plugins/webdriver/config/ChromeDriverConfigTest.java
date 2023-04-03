package com.googlecode.jmeter.plugins.webdriver.config;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(ChromeDriverConfig.class)

public class ChromeDriverConfigTest {

    private ChromeDriverConfig config;
    private JMeterVariables variables;

    @Before
    public void createConfig() {
        config = new ChromeDriverConfig();
        variables = new JMeterVariables();
        JMeterContextService.getContext().setVariables(variables);
    }

    @After
    public void resetConfig() {
        config.clearThreadBrowsers();
        config.getServices().clear();
        JMeterContextService.getContext().setVariables(null);
    }

    @Test
    public void shouldBeAbleToSerialiseAndDeserialise() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);

        output.writeObject(config);
        output.flush();
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        final ChromeDriverConfig deserializedConfig = (ChromeDriverConfig) input.readObject();

        assertThat(deserializedConfig, is(config));
    }

    @Test
    public void shouldCreateChromeAndStartService() throws Exception {
        ChromeDriver mockChromeDriver = mock(ChromeDriver.class);
        whenNew(ChromeDriver.class).withParameterTypes(ChromeDriverService.class, ChromeOptions.class).withArguments(isA(ChromeDriverService.class), isA(ChromeOptions.class)).thenReturn(mockChromeDriver);
        ChromeDriverService.Builder mockServiceBuilder = mock(ChromeDriverService.Builder.class);
        whenNew(ChromeDriverService.Builder.class).withNoArguments().thenReturn(mockServiceBuilder);
        when(mockServiceBuilder.usingDriverExecutable(isA(File.class))).thenReturn(mockServiceBuilder);
        ChromeDriverService mockService = mock(ChromeDriverService.class);
        when(mockServiceBuilder.build()).thenReturn(mockService);

        final ChromeDriver browser = config.createBrowser();

        assertThat(browser, is(mockChromeDriver));
        verifyNew(ChromeDriver.class, times(1)).withArguments(isA(ChromeDriverService.class), isA(ChromeOptions.class));
        verify(mockServiceBuilder, times(1)).build();
        assertThat(config.getServices().size(), is(1));
        assertThat(config.getServices().values(), hasItem(mockService));
    }

    @Test
    public void shouldNotCreateChromeWhenStartingServiceThrowsAnException() throws Exception {
        ChromeDriverService.Builder mockServiceBuilder = mock(ChromeDriverService.Builder.class);
        whenNew(ChromeDriverService.Builder.class).withNoArguments().thenReturn(mockServiceBuilder);
        when(mockServiceBuilder.usingDriverExecutable(isA(File.class))).thenReturn(mockServiceBuilder);
        ChromeDriverService mockService = mock(ChromeDriverService.class);
        when(mockServiceBuilder.build()).thenReturn(mockService);
        doThrow(new IOException("Stubbed exception")).when(mockService).start();

        final ChromeDriver browser = config.createBrowser();

        assertThat(browser, is(nullValue()));
        assertThat(config.getServices(), is(Collections.<String, ChromeDriverService>emptyMap()));
        verify(mockServiceBuilder, times(1)).build();
    }

    @Test
    public void shouldQuitWebDriverAndStopServiceWhenQuitBrowserIsInvoked() throws Exception {
        ChromeDriver mockChromeDriver = mock(ChromeDriver.class);
        ChromeDriverService mockService = mock(ChromeDriverService.class);
        when(mockService.isRunning()).thenReturn(true);
        config.getServices().put(config.currentThreadName(), mockService);

        config.quitBrowser(mockChromeDriver);

        verify(mockChromeDriver).quit();
        assertThat(config.getServices(), is(Collections.<String, ChromeDriverService>emptyMap()));
        verify(mockService, times(1)).stop();
    }

    @Test
    public void shouldNotStopServiceIfNotRunningWhenQuitBrowserIsInvoked() throws Exception {
        ChromeDriver mockChromeDriver = mock(ChromeDriver.class);
        ChromeDriverService mockService = mock(ChromeDriverService.class);
        when(mockService.isRunning()).thenReturn(false);
        config.getServices().put(config.currentThreadName(), mockService);

        config.quitBrowser(mockChromeDriver);

        verify(mockChromeDriver).quit();
        assertThat(config.getServices(), is(Collections.<String, ChromeDriverService>emptyMap()));
        verify(mockService, times(0)).stop();
    }

    @Test
    public void shouldBeAbleToCallQuitBrowserMultipleTimes() throws Exception {
        ChromeDriver mockChromeDriver = mock(ChromeDriver.class);
        ChromeDriverService mockService = mock(ChromeDriverService.class);
        when(mockService.isRunning()).thenReturn(true);
        config.getServices().put(config.currentThreadName(), mockService);

        config.quitBrowser(mockChromeDriver);
        config.quitBrowser(mockChromeDriver);

        assertThat(config.getServices(), is(Collections.<String, ChromeDriverService>emptyMap()));
        verify(mockService, times(1)).stop();
    }

    @Test
    public void shouldHaveProxyInCapability() {
        final ChromeOptions options = config.createChromeOptions();
        assertThat(options.getCapability(CapabilityType.PROXY), is(notNullValue()));
    }

    @Test
    public void shouldMergeCustomCapabilities() {
        config.setCustomCapabilities("{\"myCustomCapability\": \"myCustomValue\"}");
        final Capabilities capabilities = config.createChromeOptions();
        assertThat(capabilities.getCapability("myCustomCapability"), is("myCustomValue"));
    }

    @Test
    public void shouldHaveChromeOptionsWhenRemoteIsEnabled() {
        config.setHeadless(true);
        final ChromeOptions options = config.createChromeOptions();
        @SuppressWarnings("unchecked")
		Map<String,Object> capability = (Map<String,Object>) options.getCapability(ChromeOptions.CAPABILITY);
        assertThat(capability, is(notNullValue()));
		@SuppressWarnings("unchecked")
		List<String> args = (List<String>) capability.get("args");
        assertThat(args, is(notNullValue()));
        assertEquals(2, args.size());
        assertEquals("--remote-allow-origins=*", args.get(0));
        assertEquals("--headless=new", args.get(1));
    }

    @Test
    public void shouldNotHaveChromeOptionsWhenRemoteIsNotEnabled() {
        final ChromeOptions options = config.createChromeOptions();
        org.hamcrest.MatcherAssert.assertThat(options.getCapability(ChromeOptions.CAPABILITY), Matchers.hasToString("{args=[--remote-allow-origins=*], extensions=[]}"));
    }

    @Test
    public void shouldHaveInsecureCertsWhenInsecureCertsIsEnabled() {
        config.setAcceptInsecureCerts(true);
        final ChromeOptions options = config.createChromeOptions();
        assertThat((Boolean) options.getCapability("acceptInsecureCerts"), is(true));
    }

    @Test
    public void shouldNotHaveInsecureCertsWhenInsecureCertsIsNotEnabled() {
        config.setAcceptInsecureCerts(false);
        final ChromeOptions options = config.createChromeOptions();
        assertThat(options.getCapability("acceptInsecureCerts"), is(false));
    }

    @Test
    public void getSetChromeDriverPath() {
        config.setDriverPath("some path");
        assertThat(config.getDriverPath(), is("some path"));
    }

    @Test
    public void getSetBinaryPath() {
        config.setChromeBinaryPath("some/path");
        assertThat(config.getChromeBinaryPath(), is("some/path"));
    }

    @Test
    public void getSetHeadlessEnabled() {
        assertThat(config.isHeadless(), is(false));
        config.setHeadless(true);
        assertThat(config.isHeadless(), is(true));
    }

    @Test
    public void getSetInsecureCertsEnabled() {
        assertThat(config.isAcceptInsecureCerts(), is(false));
        config.setAcceptInsecureCerts(true);
        assertThat(config.isAcceptInsecureCerts(), is(true));
    }

    @Test
    public void getSetAdditionalArgs() {
        config.setChromeAdditionalArgs("additional args");
        assertThat(config.getChromeAdditionalArgs(), is("additional args"));
    }
}
