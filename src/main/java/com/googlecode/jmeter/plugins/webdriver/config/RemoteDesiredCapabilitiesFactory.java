package com.googlecode.jmeter.plugins.webdriver.config;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;


public class RemoteDesiredCapabilitiesFactory {
  public static DesiredCapabilities build(RemoteCapability capability){
	  DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
	  if(RemoteCapability.CHROME.equals(capability)){
		  ChromeOptions options = new ChromeOptions();
		  desiredCapabilities.setBrowserName("chrome");
	      desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		  return desiredCapabilities;
	  } else if (RemoteCapability.FIREFOX.equals(capability)){
		  FirefoxProfile profile = new FirefoxProfile();
		  desiredCapabilities.setBrowserName("firefox");
		  desiredCapabilities.setCapability(FirefoxDriver.Capability.PROFILE, profile);
		  return desiredCapabilities;
	  } else if (RemoteCapability.INTERNET_EXPLORER.equals(capability)){
		  desiredCapabilities.setBrowserName("internetExplorer");
		  return desiredCapabilities;
	  }
	  throw new IllegalArgumentException("No such capability");
  }
}
