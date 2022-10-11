package com.googlecode.jmeter.plugins.webdriver.config;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*","javax.net.ssl.*"})
@PrepareForTest(FirefoxDriverConfig.class)

public class FirefoxDriverConfigTest {

    private FirefoxDriverConfig config;
    private JMeterVariables variables;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }
    
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
    public void shouldCreateFirefox() throws Exception {
        // Set the path to GeckoDriver executable on your machine
        config.setFirefoxDriverPath("Path_of_GeckoDriver_Driver");
        FirefoxDriver mockFirefoxDriver = Mockito.mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class)
            .withParameterTypes(FirefoxDriverService.class, FirefoxOptions.class)
            .withArguments(isA(GeckoDriverService.class), isA(FirefoxOptions.class))

            .thenReturn(mockFirefoxDriver);

        final FirefoxDriver browser = config.createBrowser();

        assertThat(browser, is(mockFirefoxDriver));
        verifyNew(FirefoxDriver.class, times(1)).withArguments(isA(GeckoDriverService.class), isA(FirefoxOptions.class));
    }

    @Test
    public void shouldHaveProxyInCapability() {
        final FirefoxOptions options = config.createOptions();
        assertThat(options.getCapability(CapabilityType.PROXY), is(notNullValue()));
    }

}
