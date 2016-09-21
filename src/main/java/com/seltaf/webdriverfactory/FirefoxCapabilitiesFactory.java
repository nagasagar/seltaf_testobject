package com.seltaf.webdriverfactory;

import java.io.File;
import java.io.IOException;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.seltaf.core.SeltafTestLogger;
import com.seltaf.driver.DriverConfig;

public class FirefoxCapabilitiesFactory implements ICapabilitiesFactory {

	public DesiredCapabilities createCapabilities(DriverConfig cfg) {
		DesiredCapabilities capability;
        capability = new DesiredCapabilities();
        capability.setBrowserName(DesiredCapabilities.firefox().getBrowserName());

        FirefoxProfile profile = getFirefoxProfile(cfg);
        configProfile(profile, cfg);
        capability.setCapability(FirefoxDriver.PROFILE, profile);

        if (cfg.isEnableJavascript()) {
            capability.setJavascriptEnabled(true);
        } else {
            capability.setJavascriptEnabled(false);
        }

        capability.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        if (cfg.getBrowserVersion() != null) {
            capability.setVersion(cfg.getBrowserVersion());
        }

        if (cfg.getWebPlatform() != null) {
            capability.setPlatform(cfg.getWebPlatform());
        }

        if (cfg.getProxyHost() != null) {
            capability.setCapability(CapabilityType.PROXY, cfg.getProxy());
        }

        return capability;
	}

	protected synchronized FirefoxProfile getFirefoxProfile(final DriverConfig webDriverConfig) {
        String path = webDriverConfig.getFirefoxProfilePath();
        FirefoxProfile profile;
        String realPath;
        if (webDriverConfig.isUseFirefoxDefaultProfile()) {
            realPath = getFirefoxProfilePath(path);
        } else {
            realPath = null;
        }

        profile = createFirefoxProfile(realPath);
        return profile;
    }
	
	protected FirefoxProfile createFirefoxProfile(final String path) {
        if (path != null) {
            return new FirefoxProfile(new File(path));
        } else {
            return new FirefoxProfile();
        }
    }
	
	protected String getFirefoxProfilePath(String path) {
        String realPath = null;
        if (path != null && !new File(path).exists()) {
        	SeltafTestLogger.log("Firefox profile path:" + path + " not found, use default");
            path = null;
        }

        if (path != null) 
        	realPath = path;
        

        System.out.println("Firefox Profile: " + realPath);
        return realPath;
    }
	protected void configProfile(final FirefoxProfile profile, final DriverConfig webDriverConfig) {
        profile.setAcceptUntrustedCertificates(webDriverConfig.isSetAcceptUntrustedCertificates());
        profile.setAssumeUntrustedCertificateIssuer(webDriverConfig.isSetAssumeUntrustedCertificateIssuer());

        if (webDriverConfig.getFirefoxBinPath() != null) {
            System.setProperty("webdriver.firefox.bin", webDriverConfig.getFirefoxBinPath());
        }

        if (webDriverConfig.getUserAgentOverride() != null) {
            profile.setPreference("general.useragent.override", webDriverConfig.getUserAgentOverride());
        }

        if (webDriverConfig.getNtlmAuthTrustedUris() != null) {
            profile.setPreference("network.automatic-ntlm-auth.trusted-uris", webDriverConfig.getNtlmAuthTrustedUris());
        }

        if (webDriverConfig.getBrowserDownloadDir() != null) {
            profile.setPreference("browser.download.dir", webDriverConfig.getBrowserDownloadDir());
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/octet-stream,text/plain,application/pdf,application/zip,text/csv,text/html");
        }

        if (!webDriverConfig.isEnableJavascript()) {
            profile.setPreference("javascript.enabled", false);
        } else {

            // Add Firefox extension to collect JS Error
            if (webDriverConfig.isAddJSErrorCollectorExtension()) {
                try {
                    JavaScriptError.addExtension(profile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // fix permission denied issues
        profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess");
        profile.setPreference("capability.policy.default.Window.frameElement.get", "allAccess");
        profile.setPreference("capability.policy.default.HTMLDocument.compatMode.get", "allAccess");
        profile.setPreference("capability.policy.default.Document.compatMode.get", "allAccess");
        profile.setEnableNativeEvents(false);
        profile.setPreference("dom.max_chrome_script_run_time", 0);
        profile.setPreference("dom.max_script_run_time", 0);
    }
}
