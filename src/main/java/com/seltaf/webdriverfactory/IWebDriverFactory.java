package com.seltaf.webdriverfactory;

import org.openqa.selenium.WebDriver;

import com.seltaf.driver.DriverConfig;

public interface IWebDriverFactory {

    void cleanUp();

    WebDriver createWebDriver() throws Exception;

    WebDriver getWebDriver();

    DriverConfig getWebDriverConfig();
}
