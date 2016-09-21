package com.seltaf.utils;

import io.appium.java_client.AppiumDriver;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.seltaf.core.ScreenShot;
import com.seltaf.core.SeltafContext;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.customexceptions.WebSessionEndedException;
import com.seltaf.driver.DriverManager;
import com.seltaf.enums.BrowserType;
import com.seltaf.enums.TestType;
import com.seltaf.helpers.HashCodeGenerator;
import com.seltaf.helpers.WaitHelper;



public class ScreenshotUtility {
	private static final Logger logger = Logger.getLogger(ScreenshotUtility.class);

    private String suiteName;
    private String outputDirectory;
    private WebDriver driver;
    private String filename;

    public ScreenshotUtility() {
        suiteName = getSuiteName();
        outputDirectory = getOutputDirectory();
        this.driver = DriverManager.getWebDriver();
    }

    public ScreenshotUtility(final WebDriver driver) {
        suiteName = getSuiteName();
        outputDirectory = getOutputDirectory();
        this.driver = driver;
    }
    
    private static String getSuiteName() {
        String suiteName = null;

        suiteName = SeltafContextManager.getGlobalContext().getTestNGContext().getSuite().getName();

        return suiteName;
    }

    private static String getOutputDirectory() {
        String outputDirectory = null;
        outputDirectory = SeltafContextManager.getGlobalContext().getTestNGContext().getOutputDirectory();

        return outputDirectory;
    }
    
   
    
    /**
     * Used by DriverExceptionListener, don't log the customexception but put it into context.
     */
    public void capturePageSnapshotOnException() {
        Boolean capture = SeltafContextManager.getThreadContext().getCaptureSnapshot();
        SeltafContextManager.getThreadContext().setAttribute(SeltafContext.CAPTURE_SNAPSHOT, "true");
        captureWebPageSnapshot();
        SeltafContextManager.getThreadContext().setAttribute(SeltafContext.CAPTURE_SNAPSHOT,
            Boolean.toString(capture));

        // SeleniumTestsContextManager.getThreadContext().setScreenshotName(filename);
        // SeleniumTestsContextManager.getThreadContext().setWebExceptionURL(location);
        // SeleniumTestsContextManager.getThreadContext().setWebExceptionMessage(title + " ("
        // + sbMessage.toString() + ")");
        // screenShot.setException(true);
         if (SeltafContextManager.getThreadContext().getScreenshots().size() > 0) {
        	SeltafContextManager.getThreadContext().getScreenshots().getLast().setException(true);
        }
    }
    
    public ScreenShot captureWebPageSnapshot() {

        ScreenShot screenShot = new ScreenShot();

        if (SeltafContextManager.getThreadContext() == null || outputDirectory == null) {
            return screenShot;
        }

        screenShot.setSuiteName(this.suiteName);

        try {
            String url = null;
            if (SeltafContextManager.getThreadContext().getTestType().equalsIgnoreCase(
                    TestType.WEB.toString()))
            {
            	 try {
                     url = driver.getCurrentUrl();
                 } catch (org.openqa.selenium.UnhandledAlertException ex) {

                     // ignore alert customexception
                     ex.printStackTrace();
                     url = driver.getCurrentUrl();
                 }
            	 String title = driver.getTitle();
                 String pageSource = driver.getPageSource();
                 handleTitle(title, screenShot);
                 handleSource(pageSource, screenShot);

            }
           

            
            String filename = HashCodeGenerator.getRandomHashCode("web");
            this.filename = filename;
            screenShot.setLocation(url);

            
            if (SeltafContextManager.getThreadContext().getCaptureSnapshot()) {
                handleImage(screenShot);
            }
        } catch (WebSessionEndedException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (SeltafContextManager.getThreadContext().getCaptureSnapshot()) {
        	SeltafContextManager.getThreadContext().addScreenShot(screenShot);
        }

        return screenShot;
    }
    
    private void handleSource(String htmlSource, final ScreenShot screenShot) {
        if (htmlSource == null) {

            // driver.switchTo().defaultContent();
            htmlSource = driver.getPageSource();
        }

        if (htmlSource != null) {
            try {
                FileUtility.writeToFile(outputDirectory + "/htmls/" + filename + ".html", htmlSource);
                screenShot.setHtmlSourcePath(suiteName + "/htmls/" + filename + ".html");
            } catch (IOException e) {
                logger.warn("Ex", e);
            }

        }
    }

    private void handleImage(final ScreenShot screenShot) {
    	
        try {
            String screenshotString = captureEntirePageScreenshotToString(DriverManager.getWebDriver(), "");

            if (screenshotString != null && !screenshotString.equalsIgnoreCase("")) {
            	
                byte[] byteArray = screenshotString.getBytes();
                FileUtility.writeImage(outputDirectory + "/screenshots/" + filename + ".png", byteArray);
                screenShot.setImagePath(suiteName + "/screenshots/" + filename + ".png");
                
            }
        } catch (Throwable e) {
            logger.warn("Ex", e);
            e.printStackTrace();
        }
       
    }

    private void handleTitle(String title, final ScreenShot screenShot) {
        if (title == null) {

            // driver.switchTo().defaultContent();
            title = driver.getTitle();
        }

        if (title == null) {
            title = "";
        }

        screenShot.setTitle(title);
    }
    
    public static String captureEntirePageScreenshotToString(final WebDriver driver, final String arg0) {
        if (driver == null) {
            return "";
        }

        try {

            // Don't capture snapshot for htmlunit
            if (DriverManager.getDriverManager().getBrowser().equalsIgnoreCase(BrowserType.HtmlUnit.getBrowserType())) {
                return null;
            }

            if (DriverManager.getDriverManager().getBrowser().equalsIgnoreCase(BrowserType.Android.getBrowserType()) && !SeltafContextManager.isWebTest()) {
            	String current_context = ((AppiumDriver) ((EventFiringWebDriver) driver).getWrappedDriver()).getContext();
            	//switchContext(driver,"NATIVE");
            	TakesScreenshot screenShot = (TakesScreenshot) driver;
            	String screenshotstring = screenShot.getScreenshotAs(OutputType.BASE64);
                //switchContext(driver,current_context);
                return screenshotstring;
            }

            TakesScreenshot screenShot = (TakesScreenshot) driver;
            return screenShot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception ex) {

            // Ignore all exceptions
            ex.printStackTrace();
        }

        return "";
    }
    
    public static void captureSnapshot(final String messagePrefix) {
        if (SeltafContextManager.getThreadContext() != null
                && SeltafContextManager.getThreadContext().getCaptureSnapshot()
                && getOutputDirectory() != null) {
            String filename = HashCodeGenerator.getRandomHashCode("HtmlElement");
            StringBuffer sbMessage = new StringBuffer();
            try {
                String img = ScreenshotUtility.captureEntirePageScreenshotToString(DriverManager.getWebDriver(), "");
                if (img == null) {
                    return;
                }

                byte[] byteArray = img.getBytes();
                if (byteArray != null && byteArray.length > 0) {
                    String imgFile = "/screenshots/" + filename + ".png";
                    FileUtility.writeImage(getOutputDirectory() + imgFile, byteArray);

                    ScreenShot screenShot = new ScreenShot();
                    String imagePath = getSuiteName() + imgFile;
                    screenShot.setImagePath(imagePath);
                    SeltafContextManager.getThreadContext().addScreenShot(screenShot);
                    sbMessage.append(messagePrefix + ": <a href='" + imagePath
                            + "' class='lightbox'>Application Snapshot</a>");
                    SeltafTestLogger.logWebOutput(null, sbMessage.toString(), false);
                    sbMessage = null;
                }
            } catch (WebSessionEndedException ex) {
                throw ex;
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

    public static void switchContext(final WebDriver driver,final String context) throws Exception {
			 Set<String> contextNames = ((AppiumDriver) ((EventFiringWebDriver) driver).getWrappedDriver()).getContextHandles();
		 int webviewindex=0;
		 for (String contextName : contextNames) {
		   //  System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
		     if(contextName.contains(context))
		     {
		    	 break;
		     }
		     webviewindex++;
		 }
		 System.out.println("Switching context to : "+(String) contextNames.toArray()[webviewindex]);
		 ((AppiumDriver) ((EventFiringWebDriver) driver).getWrappedDriver()).context((String) contextNames.toArray()[webviewindex]);
   }
}
