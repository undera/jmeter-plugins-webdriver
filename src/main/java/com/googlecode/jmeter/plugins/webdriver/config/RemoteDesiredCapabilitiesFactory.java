package com.googlecode.jmeter.plugins.webdriver.config;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;


public class RemoteDesiredCapabilitiesFactory {
  public static DesiredCapabilities build(RemoteCapability capability){
	  DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
	  if(RemoteCapability.CHROME.equals(capability)){
		  desiredCapabilities.setBrowserName("chrome");
		  ChromeOptions options = new ChromeOptions();
	      desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		  return desiredCapabilities;
	  } else if (RemoteCapability.FIREFOX.equals(capability)){
		  desiredCapabilities.setBrowserName("firefox");
		  FirefoxProfile profile = new FirefoxProfile();
		  desiredCapabilities.setCapability(FirefoxDriver.Capability.PROFILE, profile);
		  return desiredCapabilities;
	  } else if (RemoteCapability.INTERNET_EXPLORER.equals(capability)){
		  desiredCapabilities.setBrowserName("internetExplorer");
		  InternetExplorerOptions options = new InternetExplorerOptions();
		  // Setting to launch Microsoft Edge in IE mode with the IEDriver
		  options.attachToEdgeChrome();
		  desiredCapabilities.merge(options);
		  return desiredCapabilities;
	  }
	  throw new IllegalArgumentException("No such capability");
  }
}
