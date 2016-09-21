package com.cucumber.listener;

import com.relevantcodes.extentreports.*;
import com.seltaf.core.CustomAssertion;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.customexceptions.NoMatchingTestData;

import cucumber.runtime.CucumberException;
import cucumber.runtime.io.URLOutputStream;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

import org.testng.SkipException;
import org.testng.internal.TestResult;
import org.testng.internal.Utils;

/**
 * Cucumber custom format listener which generates ExtentsReport html file
 */
public class ExtentCucumberFormatter implements Reporter, Formatter {

    private static ExtentReports extent;
    private ExtentTest featureTest;
    private ExtentTest scenarioTest;
    private LinkedList<Step> testSteps = new LinkedList<Step>();
    private static File htmlReportDir;
    private static Map systemInfo;
    private boolean scenarioOutlineTest;

    private static final Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap() {
        {
            this.put("image/bmp", "bmp");
            this.put("image/gif", "gif");
            this.put("image/jpeg", "jpg");
            this.put("image/png", "png");
            this.put("image/svg+xml", "svg");
            this.put("video/ogg", "ogg");
        }
    };

    public ExtentCucumberFormatter(File filePath) {
        if (!filePath.getPath().equals("")) {
            String reportPath = filePath.getPath();
            this.htmlReportDir = new File(reportPath);
            this.extent = new ExtentReports(reportPath);
        } else {
            String reportDir = "output/Run_" + System.currentTimeMillis();
            this.htmlReportDir = new File(reportDir);
            this.extent = new ExtentReports(reportDir + "/report.html");
        }
    }

    public ExtentCucumberFormatter() {
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting,
                                                       DisplayOrder displayOrder, NetworkMode networkMode,
                                                       Locale locale) {
        htmlReportDir = filePath;
        extent = new ExtentReports(filePath.getAbsolutePath(), replaceExisting, displayOrder, networkMode, locale);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting,
                                                       DisplayOrder displayOrder, NetworkMode networkMode) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, displayOrder, networkMode, null);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting,
                                                       DisplayOrder displayOrder, Locale locale) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, displayOrder, null, locale);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting,
                                                       DisplayOrder displayOrder) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, displayOrder, null, null);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting, NetworkMode networkMode,
                                                       Locale locale) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, null, networkMode, locale);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting,
                                                       NetworkMode networkMode) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, null, networkMode, null);
    }

    public static void initiateExtentCucumberFormatter(File filePath, NetworkMode networkMode) {
        initiateExtentCucumberFormatter(filePath, null, null, networkMode, null);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting, Locale locale) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, null, null, locale);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Boolean replaceExisting) {
        initiateExtentCucumberFormatter(filePath, replaceExisting, null, null, null);
    }

    public static void initiateExtentCucumberFormatter(File filePath, Locale locale) {
        initiateExtentCucumberFormatter(filePath, null, null, null, locale);
    }

    public static void initiateExtentCucumberFormatter(File filePath) {
        initiateExtentCucumberFormatter(filePath, null, null, null, null);
    }

    public static void initiateExtentCucumberFormatter() {
        String reportFilePath = "test-output" + File.separator + "CucumberFeaturesExecutionReport.html";
        initiateExtentCucumberFormatter(new File(reportFilePath));
    }

    public static void loadConfig(File configFile) {
        extent.loadConfig(configFile);
    }

    public static void addSystemInfo(String param, String value) {
        if (systemInfo == null) {
            systemInfo = new HashMap();
        }
        systemInfo.put(param, value);
    }

    public static void addSystemInfo(Map<String, String> info) {
        if (systemInfo == null) {
            systemInfo = new HashMap();
        }
        systemInfo.putAll(info);
    }

    public void before(Match match, Result result) {
    	
    }

    public void result(Result result) {
        if (!scenarioOutlineTest) {
            if ("passed".equals(result.getStatus())) {
            	   List<Throwable> verificationFailures = CustomAssertion.getcucumberVerificationFailures();

    	            int size = verificationFailures.size();
    	            if (size > 0) {
    	            	 if (size == 1) {
    	            		 scenarioTest.log(LogStatus.FAIL, testSteps.poll().getName(), verificationFailures.get(0));
    	    	            }
    	            	 else{
    	            		 StringBuilder failureMessage = new StringBuilder("!!! Many Test Failures (").append(size).append(
    	    	                        ")\n");
    	    	                for (int i = 0; i < size - 1; i++) {
    	    	                    failureMessage.append("Failure ").append(i + 1).append(" of ").append(size).append("\n");

    	    	                    Throwable t = verificationFailures.get(i);
    	    	                    String fullStackTrace = Utils.stackTrace(t, false)[1];
    	    	                    failureMessage.append(fullStackTrace).append("\n");
    	    	                }

    	    	                // final failure
    	    	                Throwable last = verificationFailures.get(size - 1);
    	    	                failureMessage.append("Failure ").append(size).append(" of ").append(size).append("\n");
    	    	                failureMessage.append(last.toString());

    	    	                // set merged throwable
    	    	                Throwable merged = new Throwable(failureMessage.toString());
    	    	                merged.setStackTrace(last.getStackTrace());
    	    	                scenarioTest.log(LogStatus.FAIL, testSteps.poll().getName(), merged);
    	            	 }
    	            } else  {
    	            	scenarioTest.log(LogStatus.PASS, testSteps.poll().getName(), "PASSED");
    	            }
    	          SeltafContextManager.getThreadContext().clearcucumberVerificationFailures();
            } else if ("failed".equals(result.getStatus())) {
            	if(result.getError().getClass().equals(NoMatchingTestData.class))
            	{
            		scenarioTest.log(LogStatus.SKIP, testSteps.poll().getName(), "SKIPPED - Test data Not available");
            	}
            	else if(result.getError().getClass().equals(NoMatchingTestData.class))
            	{
            		scenarioTest.log(LogStatus.SKIP, testSteps.poll().getName(), "SKIPPED");
            	}
            	else{
            		scenarioTest.log(LogStatus.FAIL, testSteps.poll().getName(), result.getError());
            	}
                
            } else if ("skipped".equals(result.getStatus())) {
                scenarioTest.log(LogStatus.SKIP, testSteps.poll().getName(), "SKIPPED");
            } else if ("undefined".equals(result.getStatus())) {
                scenarioTest.log(LogStatus.UNKNOWN, testSteps.poll().getName(), "UNDEFINED");
            }
        }
    }

    public void after(Match match, Result result) {

    }

    public void match(Match match) {

    }

    public void embedding(String s, byte[] bytes) {
        if (!scenarioOutlineTest) {
            String extension = (String)MIME_TYPES_EXTENSIONS.get(s);
            String fileName = "screenshot-" + System.currentTimeMillis() + "." + extension;
            this.writeBytesAndClose(bytes, this.reportFileOutputStream(fileName));
            scenarioTest.log(LogStatus.INFO, scenarioTest.addScreenCapture(fileName));
        }
    }

    public void write(String s) {
        if (!scenarioOutlineTest)
            scenarioTest.log(LogStatus.INFO, s);
    }

    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
    }

    public void uri(String s) {
    }

    public void feature(Feature feature) {
        featureTest = extent.startTest("Feature: " + feature.getName());
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        scenarioOutlineTest = true;
    }

    public void examples(Examples examples) {
    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
        scenarioTest = extent.startTest("Scenario: " + scenario.getName());

        for (Tag tag : scenario.getTags()) {
            scenarioTest.assignCategory(tag.getName());
        }
        scenarioOutlineTest = false;
    }

    public void background(Background background) {
    }

    public void scenario(Scenario scenario) {
    }

    public void step(Step step) {
        if (!scenarioOutlineTest)
            testSteps.add(step);
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {
        if (!scenarioOutlineTest) {
            extent.endTest(scenarioTest);
            featureTest.appendChild(scenarioTest);
        }
    }

    public void done() {
    }

    public void close() {
        extent.addSystemInfo(systemInfo);
        extent.close();
    }

    public void eof() {
        extent.endTest(featureTest);
        extent.flush();
    }

    private OutputStream reportFileOutputStream(String fileName) {
        try {
            return new URLOutputStream(new URL(this.htmlReportDir.toURI().toURL(), fileName));
        } catch (IOException var3) {
            throw new CucumberException(var3);
        }
    }

    private void writeBytesAndClose(byte[] buf, OutputStream out) {
        try {
            out.write(buf);
        } catch (IOException var4) {
            throw new CucumberException("Unable to write to report file item: ", var4);
        }
    }

}
