package com.googlecode.jmeter.plugins.webdriver.config;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
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

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*","javax.net.ssl.*"})
@PrepareForTest(FirefoxDriverConfig.class)

public class FirefoxDriverConfigTest {

    private FirefoxDriverConfig config;
    private JMeterVariables variables;

    @Before
    public void createConfig() {
        config = new FirefoxDriverConfig();
        variables = new JMeterVariables();
        JMeterContextService.getContext().setVariables(variables);
    }

    @After
    public void resetConfig() {
        config.clearThreadBrowsers();
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
        final FirefoxDriverConfig deserializedConfig = (FirefoxDriverConfig) input.readObject();

        assertThat(deserializedConfig, is(config));
    }

    @Test
    public void shouldCreateFirefoxAndStartService() throws Exception {
        config.enableUnitTests();
        FirefoxDriver mockFirefoxDriver = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(FirefoxDriverService.class, FirefoxOptions.class).withArguments(isA(FirefoxDriverService.class), isA(FirefoxOptions.class)).thenReturn(mockFirefoxDriver);
        @SuppressWarnings("rawtypes")
        FirefoxDriverService.Builder mockServiceBuilder = mock(FirefoxDriverService.Builder.class);
        whenNew(FirefoxDriverService.Builder.class).withNoArguments().thenReturn(mockServiceBuilder);
        when(mockServiceBuilder.usingDriverExecutable(isA(File.class))).thenReturn(mockServiceBuilder);
        FirefoxDriverService mockService = mock(GeckoDriverService.class);
        when(mockServiceBuilder.build()).thenReturn(mockService);

        final FirefoxDriver browser = config.createBrowser();

        assertThat(browser, is(mockFirefoxDriver));
        verifyNew(FirefoxDriver.class, times(1)).withArguments(isA(FirefoxDriverService.class), isA(FirefoxOptions.class));
        verify(mockServiceBuilder, times(0)).build();
        assertThat(config.getServices().size(), is(1));
    }


    @Test
    public void shouldHaveProxyInCapability() {
        final FirefoxOptions options = config.createFirefoxOptions();
        assertThat(options.getCapability(CapabilityType.PROXY), is(notNullValue()));
    }

    @Test
    public void shouldMergeCustomCapabilities() {
        config.setCustomCapabilities("{\"myCustomCapability\": \"myCustomValue\"}");
        final Capabilities capabilities = config.createFirefoxOptions();
        assertThat(capabilities.getCapability("myCustomCapability"), is("myCustomValue"));
    }
}
