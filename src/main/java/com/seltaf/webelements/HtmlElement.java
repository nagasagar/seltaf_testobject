package com.seltaf.webelements;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.driver.DriverManager;
import com.seltaf.helpers.WaitHelper;
import com.seltaf.utils.ScreenshotUtility;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

public class HtmlElement {
	
	 protected WebDriver driver = DriverManager.getWebDriver(true);
	 protected WebElement element = null;
	 private String label = null;
	 private String locator = null;
	 private By by = null;
	
	private int explictWaitTimeout = DriverManager.getDriverManager().getExplicitWait();
	protected static final Logger logger = SeltafTestLogger.getLogger(HtmlElement.class);
	
	private static enum LocatorType {
        ID, NAME, CLASS_NAME, LINK_TEXT, PARTIAL_LINK_TEXT, CSS_SELECTOR,
        TAG_NAME, XPATH,
    }
	
	public HtmlElement(final String label, final By by) {
        this.label = label;
        this.by = by;
    }

    public HtmlElement(final String label, final String locator,
        final LocatorType locatorType) {
        this.label = label;
        this.locator = locator;
        this.by = getLocatorBy(locator, locatorType);
    }
    
    public By getBy() {
        return by;
    }

    public WebElement getElement() {
        element = driver.findElement(by);

        return element;
    }
    
    public String getLabel() {
        return label;
    }

    public String getLocator() {
        return locator;
    }
    
    /**
     * Finds the element using By type. Implicit Waits is built in createWebDriver() in WebUIDriver to handle dynamic
     * element problem. This method is invoked before all the basic operations like click, sendKeys, getText, etc. Use
     * waitForPresent to use Explicit Waits to deal with special element which needs long time to present.
     */
    protected void findElement() {
        driver = DriverManager.getWebDriver();
        element = driver.findElement(by);
    }
    
    public void click() {
        findElement();
        element.click();
    }

    /**
     * Click element in native way by Actions.
     */
    public void clickAt() {
        clickAt("1,1");

    }

    
    public void clickAt(final String value) {
    	SeltafTestLogger.logWebStep(null, "click on " + toHTML(), false);
        findElement();

        String[] parts = value.split(",");
        int xOffset = Integer.parseInt(parts[0]);
        int yOffset = Integer.parseInt(parts[1]);

        try {
            new Actions(driver).moveToElement(element, xOffset, yOffset).click()
                .perform();
        } catch (InvalidElementStateException e) {
            e.printStackTrace();
            element.click();
        }
    }
    
    public void mouseDown() {
    	SeltafTestLogger.log("MouseDown " + this.toString());
        findElement();

        Mouse mouse = ((HasInputDevices) driver).getMouse();
        mouse.mouseDown(null);
    }

    public void mouseOver() {
    	SeltafTestLogger.log("MouseOver " + this.toString());
        findElement();

        // build and perform the mouseOver with Advanced User Interactions API
        // Actions builder = new Actions(driver);
        // builder.moveToElement(element).build().perform();
        Locatable hoverItem = (Locatable) element;
        Mouse mouse = ((HasInputDevices) driver).getMouse();
        mouse.mouseMove(hoverItem.getCoordinates());
    }
    
    public void mouseUp() {
    	SeltafTestLogger.log("MouseUp " + this.toString());
        findElement();

        Mouse mouse = ((HasInputDevices) driver).getMouse();
        mouse.mouseUp(null);
    }

    public void sendKeys(final CharSequence arg0) {
        findElement();
        element.sendKeys(arg0);
    }
    
    public List<WebElement> getAllElements() {
        findElement();

        return driver.findElements(by);
    }
    
    public String getAttribute(final String name) {
        findElement();

        return element.getAttribute(name);
    }
    
    public String getCssValue(final String propertyName) {
        findElement();

        return element.getCssValue(propertyName);
    }
    
    public int getHeight() {
        findElement();

        return element.getSize().getHeight();
    }

    public Point getLocation() {
        findElement();

        return element.getLocation();
    }
    
    public Dimension getSize() {
        findElement();

        return element.getSize();
    }

    public String getTagName() {
        findElement();

        return element.getTagName();
    }

    public String getText() {
        findElement();

        return element.getText();
    }

    public String getValue() {
        findElement();

        return element.getAttribute("value");
    }

    public int getWidth() {
        findElement();

        return element.getSize().getWidth();
    }
    
    public boolean isDisplayed() {

        try {
            findElement();

            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isElementPresent() {

        if (DriverManager.getWebDriver() == null) {
        	SeltafTestLogger.log(
                "Web Driver is terminated! Exception might caught in last action.");
            throw new RuntimeException(
                "Web Driver is terminated! Exception might caught in last action.");
        }

        int count = 0;

        try {

            count = DriverManager.getWebDriver().findElements(by).size();
        } catch (RuntimeException e) {

            if (e instanceof InvalidSelectorException) {
            	SeltafTestLogger.log("Got InvalidSelectorException, retry");
                WaitHelper.waitForSeconds(2);
                count = DriverManager.getWebDriver().findElements(by).size();
            } else if ((e.getMessage() != null) &&
                    e.getMessage().contains(
                        "TransformedEntriesMap cannot be cast to java.util.List")) {
            	SeltafTestLogger.log("Got CastException, retry");
                WaitHelper.waitForSeconds(2);
                count = DriverManager.getWebDriver().findElements(by).size();
            } else {
                throw e;
            }
        }

        if (count == 0) {
            return false;
        }

        return true;
    }
    
    public boolean isEnabled() {
        findElement();

        return element.isEnabled();
    }

    public boolean isSelected() {
        findElement();

        return element.isSelected();
    }

    public boolean isTextPresent(final String text) {
        findElement();

        return element.getText().contains(text);
    }

    /**
     * Refreshes the WebUIDriver before locating the element, to ensure we have the current version (useful for when the
     * state of an element has changed via an AJAX or non-page-turn action).
     */
    public void init() {
        driver = DriverManager.getWebDriver();
        element = driver.findElement(by);
    }
    
    private By getLocatorBy(final String locator,
            final LocatorType locatorType) {

            switch (locatorType) {

            case ID:
                return By.id(locator);

            case NAME:
                return By.name(locator);

            case CLASS_NAME:
                return By.className(locator);

            case LINK_TEXT:
                return By.linkText(locator);

            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(locator);

            case CSS_SELECTOR:
                return By.cssSelector(locator);

            case TAG_NAME:
                return By.tagName(locator);

            default:
                return By.xpath(locator);
            }
        }
    
    public String toHTML() {
    	/*
    	 return getClass().getSimpleName().toLowerCase() +
            " <a style=\"font-style:normal;color:#8C8984;text-decoration:none;\" href=# \">" +
            getLabel() + ",: " + getBy().toString() + "</a>";
    	 */
        return " <a style=\"font-style:normal;color:#8C8984;text-decoration:none;\" href=# \">" +
            getLabel() + ",: " + getBy().toString() + "</a>";
    }
    
    public String toString() {
        return getClass().getSimpleName().toLowerCase() + " " + getLabel() +
            ", by={" + getBy().toString() + "}";
    }
    
    // Wait for maethods
    
    public void waitForPresent() {
        waitForPresent(explictWaitTimeout);
    }

    public void waitForPresent(final int timeout) {
    	SeltafTestLogger.logWebStep(null,
            "wait for " + this.toString() + " to present.", false);

        Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
        wait.until(new ExpectedCondition<WebElement>() {
                public WebElement apply(final WebDriver driver) {
                    return driver.findElement(by);
                }
            });
    }
    public void simulateClick() {
        findElement();
        SeltafTestLogger.logWebStep(null, "Simulation click on " + toHTML(), false);
        String mouseOverScript =
            "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(mouseOverScript, element);
        WaitHelper.waitForSeconds(2);

        String clickScript =
            "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('click', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onclick');}";
        js.executeScript(clickScript, element);
        WaitHelper.waitForSeconds(2);
    }
    
    public void simulateMoveToElement(final int x, final int y) {
        findElement();
        ((JavascriptExecutor) driver).executeScript(
            "function simulate(f,c,d,e){var b,a=null;for(b in eventMatchers)if(eventMatchers[b].test(c)){a=b;break}if(!a)return!1;document.createEvent?(b=document.createEvent(a),a==\"HTMLEvents\"?b.initEvent(c,!0,!0):b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null),f.dispatchEvent(b)):(a=document.createEventObject(),a.detail=0,a.screenX=d,a.screenY=e,a.clientX=d,a.clientY=e,a.ctrlKey=!1,a.altKey=!1,a.shiftKey=!1,a.metaKey=!1,a.button=1,f.fireEvent(\"on\"+c,a));return!0} var eventMatchers={HTMLEvents:/^(?:load|unload|abort|errorLogger|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/}; " +
            "simulate(arguments[0],\"mousemove\",arguments[1],arguments[2]);",
            element, x, y);

    }
    
    public void fireEvent(final String eventName) {
        findElement();

        try {
            JavascriptLibrary jsLib = new JavascriptLibrary();
            jsLib.callEmbeddedSelenium(driver, "doFireEvent", element,
                eventName);
        } catch (Exception ex) {
            // Handle OperaDriver
        }
    }
    
    public String getEval(final String script) {
        findElement();

        String name = (String) ((JavascriptExecutor) driver).executeScript(
                script, element);

        return name;
    }
    
    /**
     * Captures snapshot of the current browser window.
     */
    public void captureSnapshot() {
        captureSnapshot(com.seltaf.helpers.ContextHelper.getCallerMethod() + " on ");
    }

    /**
     * Captures snapshot of the current browser window, and prefix the file name with the assigned string.
     *
     * @param  messagePrefix
     */
    protected void captureSnapshot(final String messagePrefix) {
        ScreenshotUtility.captureSnapshot(messagePrefix);
    }

}
