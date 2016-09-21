package com.seltaf.webdriverfactory;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;






import com.seltaf.core.SeltafTestLogger;
import com.seltaf.driver.DriverConfig;
import com.seltaf.driver.DriverManager;

public abstract class AbstractWebDriverFactory {
	
	protected DriverConfig webDriverConfig;

    protected WebDriver driver;

    public AbstractWebDriverFactory(final DriverConfig cfg) {
        this.webDriverConfig = cfg;
    }

    public void cleanUp() {
        try {
            if (driver != null) {
                try {
                    SeltafTestLogger.log("quiting webdriver" + Thread.currentThread().getId());
                    driver.quit();
                } catch (WebDriverException ex) {
                	SeltafTestLogger.log("Exception encountered when quiting driver: "
                            + DriverManager.getDriverManager().getConfig().getBrowser().name() + ":" + ex.getMessage());
                }

                driver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public WebDriver createWebDriver() throws Exception {
        return null;
    }

    /**
     * Accessed by sub classes so that they don't have be declared abstract class.
     *
     * @return  driver instance
     */
    public WebDriver getWebDriver() {
        return driver;
    }
    
    public DriverConfig getWebDriverConfig() {
        return webDriverConfig;
    }
    
    public void setWebDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public void setWebDriverConfig(final DriverConfig cfg) {
        this.webDriverConfig = cfg;
    }
    
    public void setImplicitWaitTimeout(final double timeout) {
        if (timeout < 1) {
            driver.manage().timeouts().implicitlyWait((long) (timeout * 1000), TimeUnit.MILLISECONDS);
        } else {
            try {
                driver.manage().timeouts().implicitlyWait(new Double(timeout).intValue(), TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
