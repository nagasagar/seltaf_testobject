package com.seltaf.webdriverfactory;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.seltaf.driver.DriverConfig;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class ChromeCapabilitiesFactory implements ICapabilitiesFactory {

	public DesiredCapabilities createCapabilities(DriverConfig webDriverConfig) {
		DesiredCapabilities capability = null;
        capability = DesiredCapabilities.chrome();
        capability.setBrowserName(DesiredCapabilities.chrome().getBrowserName());

        ChromeOptions options = new ChromeOptions();
        if (webDriverConfig.getUserAgentOverride() != null) {
            options.addArguments("--user-agent=" + webDriverConfig.getUserAgentOverride());
        }

        capability.setCapability(ChromeOptions.CAPABILITY, options);

        if (webDriverConfig.isEnableJavascript()) {
            capability.setJavascriptEnabled(true);
        } else {
            capability.setJavascriptEnabled(false);
        }

        capability.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        if (webDriverConfig.getBrowserVersion() != null) {
            capability.setVersion(webDriverConfig.getBrowserVersion());
        }

        if (webDriverConfig.getWebPlatform() != null) {
            capability.setPlatform(webDriverConfig.getWebPlatform());
        }

        if (webDriverConfig.getProxyHost() != null) {
            Proxy proxy = webDriverConfig.getProxy();
            capability.setCapability(CapabilityType.PROXY, proxy);
        }

        if (System.getenv("webdriver.chrome.driver") == null) {
        	ChromeDriverManager.getInstance().setup();
        }
        return capability;
	}

}
