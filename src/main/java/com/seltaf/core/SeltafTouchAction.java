package com.seltaf.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.seltaf.driver.DriverManager;
import com.seltaf.webelements.HtmlElement;

public class SeltafTouchAction {
	
	protected WebDriver driver = DriverManager.getWebDriver();
	AppiumDriver appiumdriver = (AppiumDriver) ((EventFiringWebDriver) driver).getWrappedDriver();
	protected final DriverManager driverManager = DriverManager.getDriverManager();
	
	public void scroll2Element(HtmlElement e){
		Dimension  size = driver.manage().window().getSize();
		while (!e.isDisplayed()) {
			TouchAction action = new TouchAction(appiumdriver);
			int x1 = (int) (size.width * 0.50);
			int y1 = (int) (size.height * 0.50);
			action.press(x1, y1).moveTo(x1, y1+150).release().perform();
		}
		//	((AppiumDriver) ((EventFiringWebDriver) driver).getWrappedDriver()).swipe(arg0, arg1, arg2, arg3, arg4);
	}

}
