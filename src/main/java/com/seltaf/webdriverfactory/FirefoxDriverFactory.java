package com.seltaf.webdriverfactory;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;


import com.seltaf.driver.DriverConfig;


public class FirefoxDriverFactory extends AbstractWebDriverFactory implements IWebDriverFactory {

	private long timeout = 60;
	
	public FirefoxDriverFactory(DriverConfig cfg) {
		super(cfg);
	}
	
	@Override
    public WebDriver createWebDriver() {
        DriverConfig cfg = this.getWebDriverConfig();

        System.out.println("start create firefox");
        driver = createWebDriverWithTimeout();

        System.out.println("end create firefox");

        // Implicit Waits to handle dynamic element. The default value is 5
        // seconds.
        setImplicitWaitTimeout(cfg.getImplicitWaitTimeout());
        if (cfg.getPageLoadTimeout() >= 0) {
            setPageLoadTimeout(cfg.getPageLoadTimeout());
        }

        this.setWebDriver(driver);
        return driver;
    }
	protected void setPageLoadTimeout(final long timeout) {
        driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);
    }

	protected WebDriver createWebDriverWithTimeout() {
        long time = 0;
        while (time < getTimeout()) {
            try {
                driver = createNativeDriver();
                return driver;
            } catch (WebDriverException ex) {
                if (ex.getMessage().contains("SocketException")
                        || ex.getMessage().contains("Failed to connect to binary FirefoxBinary")
                        || ex.getMessage().contains("Unable to bind to locking port 7054 within 45000 ms")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }

                    time++;
                } else {
                    throw new RuntimeException(ex);
                }
            }
        }

        throw new RuntimeException("Got customexception when creating webDriver with socket timeout 1 minute");
    }
	
	 protected WebDriver createNativeDriver() {
	        return new FirefoxDriver(new FirefoxCapabilitiesFactory().createCapabilities(webDriverConfig));
	 }
	
	protected long getTimeout() {
        return timeout;
    }
}
