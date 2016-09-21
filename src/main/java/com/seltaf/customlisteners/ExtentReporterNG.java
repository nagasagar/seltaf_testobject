package com.seltaf.customlisteners;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.internal.ResultMap;
import org.testng.internal.TestResult;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.core.TestRetryAnalyzer;
import com.seltaf.core.ScreenShot;
import com.seltaf.util.internal.entity.TestEntity;
import com.seltaf.utils.ScreenshotUtility;
import com.seltaf.driver.DriverManager;
import com.seltaf.enums.TestType;
import com.seltaf.core.CustomAssertion;
import com.seltaf.customexceptions.NoMatchingTestData;

import cucumber.api.testng.CucumberFeatureWrapper;
 
public class ExtentReporterNG implements IReporter, IInvokedMethodListener, ITestListener{
    private ExtentReports extent;
    private Map<String, Boolean> isRetryHandleNeeded = new HashMap<String, Boolean>();
    private Map<String, IResultMap> failedTests = new HashMap<String, IResultMap>();
    private Map<String, IResultMap> skippedTests = new HashMap<String, IResultMap>();
 
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        extent = new ExtentReports(outputDirectory + File.separator + "ExtentReportsTestNG.html", true);
 
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();
 
            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();
 
                buildTestNodes(context.getPassedTests(), LogStatus.PASS);
                buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
                buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
            }
        }
        
        Map<String, String> sysInfo = new HashMap<String, String>();
        sysInfo.put("Selenium Version", "2.53.1");
        sysInfo.put("Appium version", "2.10");
        sysInfo.put("TestNG version", "6.8");

        extent.addSystemInfo(sysInfo);
        for (String s : Reporter.getOutput()) { 
            extent.setTestRunnerOutput(s); 
        }
 
        extent.flush();
        extent.close();
    }
 
    private void buildTestNodes(IResultMap tests, LogStatus status) {
        ExtentTest test;
 
        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
            	String TestcaseId = "";
            	Object[] testparameters = result.getParameters();
    			for(Object testparameter: testparameters)
    			{
    				if(testparameter.getClass().getSimpleName().equalsIgnoreCase("TestEntity"))
    				{
    					TestEntity x = (TestEntity) testparameter;
    					TestcaseId = x.getTestCaseId();
    				}
    			}
    			String methodname = result.getMethod().getMethodName();
    			if(methodname.equals("feature"))
    			{
    				methodname=((CucumberFeatureWrapper)testparameters[0]).toString();
    			}
    			if(TestcaseId.equals(""))
    				test = extent.startTest(methodname);
    			else
    				test = extent.startTest(methodname+"  ("+TestcaseId+")");
                test.getTest().setStartedTime(getTime(result.getStartMillis()));
                test.getTest().setEndedTime(getTime(result.getEndMillis()));
                StringBuffer description = new StringBuffer("");
               if(result.getParameters().length>0){
            	   description.append("<ul>");
            	   description.append("<li>"+result.getMethod().getDescription()+"</li>");
            	   description.append("Parameters:");
            	   description.append("\n");
                     		for(Object parameters : result.getParameters())
                     		{
                     			description.append("<li>"+parameters+"</li>");
                     			//description.append("\n");
                     		}
                   description.append("</ul>");
                }
                else
                {
                	description.append(result.getMethod().getDescription());
                }
                
                test.setDescription(description.toString());
               
                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);
  
                if(result.getAttribute("browser")!= null)
                	test.assignCategory(result.getAttribute("browser").toString());
                
                ExtentTest scenario=null;
                for(String message : Reporter.getOutput(result)) {
                	
                	if(message.equals("Started Scenario"))
                	{
                		 scenario = extent.startTest("Scenario");
                	}
                	
                	if(scenario==null){
                		if(message.contains("!!!FAILURE ALERT!!!"))
                    		test.log(LogStatus.FAIL, message);
                		else if(message.endsWith("- Passed."))
                			test.log(LogStatus.PASS, message);
                    	else
                    		test.log(LogStatus.INFO, message);
                      }
                	else
                	{
                		if(message.contains("!!!FAILURE ALERT!!!"))
                			scenario.log(LogStatus.FAIL, message);
                		else if(message.endsWith("- Passed."))
                			test.log(LogStatus.PASS, message);
                    	else
                    		scenario.log(LogStatus.INFO, message);
                	}
                	if(message.equals("Ended Scenario") && scenario!=null)
                	{
                		test.appendChild(scenario);
                		 scenario =null;
                	}
                }
                	
                                
                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable());
                }
                else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                }
 
                extent.endTest(test);
            }
        }
    }
 
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();        
    }

	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		if (method.isTestMethod()) {
			String TestcaseId = "";
			Object[] parameters = testResult.getParameters();
			for(Object parameter: parameters)
			{
				if(parameter.getClass().getSimpleName().equalsIgnoreCase("TestEntity"))
				{
					TestEntity x = (TestEntity) parameter;
					TestcaseId = x.getTestCaseId();
				}
			}
		}
		
	}

	public void afterInvocation(IInvokedMethod method, ITestResult result) {
		 Reporter.setCurrentTestResult(result);

	        // Handle Soft CustomAssertion
	        if (method.isTestMethod()) {
	        	if(result.getStatus() == TestResult.FAILURE && result.getThrowable().getCause()!=null && result.getThrowable().getCause().getClass().equals(NoMatchingTestData.class)){
	        		result.setStatus(TestResult.SKIP);
	        	}
	        	if(result.getStatus() == TestResult.FAILURE && result.getThrowable().getCause()!=null && result.getThrowable().getCause().getClass().equals(SkipException.class)){
	        		result.setStatus(TestResult.SKIP);
	        	}
	            List<Throwable> verificationFailures = CustomAssertion.getVerificationFailures();

	            int size = verificationFailures.size();
	            if (size == 0) {
	                return;
	            } else if (result.getStatus() == TestResult.FAILURE) {
	                return;
	            }

	            result.setStatus(TestResult.FAILURE);

	            if (size == 1) {
	                result.setThrowable(verificationFailures.get(0));
	            } else {

	                // create failure message with all failures and stack traces barring last failure)
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

	                result.setThrowable(merged);
	            }
	        }
		
	}

	public void onTestStart(ITestResult result) {
		String Testtype = SeltafContextManager.getThreadContext().getTestType();
		if (!Testtype.equalsIgnoreCase(TestType.UNIT.toString()) && !Testtype.equalsIgnoreCase(TestType.API.toString()))
			result.setAttribute("browser", DriverManager.getDriverManager().getBrowser().replace("*", ""));
		
	}

	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailure(ITestResult result) {
		 if (result.getMethod().getRetryAnalyzer() != null) {
	            TestRetryAnalyzer testRetryAnalyzer = (TestRetryAnalyzer) result.getMethod().getRetryAnalyzer();

	            if (testRetryAnalyzer.getCount() <= testRetryAnalyzer.getMaxCount()) {
	            	result.setStatus(ITestResult.SKIP);
	                Reporter.setCurrentTestResult(null);
	            } else {
	                IResultMap rMap = failedTests.get(result.getTestContext().getName());
	                rMap.addResult(result, result.getMethod());
	                failedTests.put(result.getTestContext().getName(), rMap);
	            }

	            System.out.println(result.getMethod() + " Failed in " + testRetryAnalyzer.getCount() + " times");
	            isRetryHandleNeeded.put(result.getTestContext().getName(), true);
	        }

	        // capture snap shot only for the failed web tests
	        if (DriverManager.getWebDriver() != null) {
	            ScreenShot screenShot = new ScreenshotUtility().captureWebPageSnapshot();
	            SeltafTestLogger.logWebOutput(screenShot.getTitle(), SeltafTestLogger.buildScreenshotLog(screenShot), true);
	        }
		
	}

	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onStart(ITestContext context) {
		 isRetryHandleNeeded.put(context.getName(), false);
	        failedTests.put(context.getName(), new ResultMap());
	        skippedTests.put(context.getName(), new ResultMap());
		
	}

	public void onFinish(ITestContext context) {
		 if (isRetryHandleNeeded.get(context.getName())) {
	            removeIncorrectlySkippedTests(context, failedTests.get(context.getName()));
	            removeFailedTestsInTestNG(context);
	        } else {
	            failedTests.put(context.getName(), context.getFailedTests());
	            skippedTests.put(context.getName(), context.getSkippedTests());
	        }
		
	}
	
	/**
     * Remove retrying failed test cases from skipped test cases.
     *
     * @param   tc
     * @param   map
     *
     * @return
     */
    private void removeIncorrectlySkippedTests(final ITestContext tc, final IResultMap map) {
        List<ITestNGMethod> failsToRemove = new ArrayList<ITestNGMethod>();
        IResultMap returnValue = tc.getSkippedTests();

        for (ITestResult result : returnValue.getAllResults()) {
            for (ITestResult resultToCheck : map.getAllResults()) {
                if (resultToCheck.getMethod().equals(result.getMethod())) {
                    failsToRemove.add(resultToCheck.getMethod());
                    break;
                }
            }

            for (ITestResult resultToCheck : tc.getPassedTests().getAllResults()) {
                if (resultToCheck.getMethod().equals(result.getMethod())) {
                    failsToRemove.add(resultToCheck.getMethod());
                    break;
                }
            }
        }

        for (ITestNGMethod method : failsToRemove) {
            returnValue.removeResult(method);
        }

        skippedTests.put(tc.getName(), tc.getSkippedTests());

    }
    
    /**
     * Remote failed test cases in TestNG.
     *
     * @param   tc
     *
     * @return
     */
    private void removeFailedTestsInTestNG(final ITestContext tc) {
        IResultMap returnValue = tc.getFailedTests();

        ResultMap removeMap = new ResultMap();
        for (ITestResult result : returnValue.getAllResults()) {
            boolean isFailed = false;
            for (ITestResult resultToCheck : failedTests.get(tc.getName()).getAllResults()) {
                if (result.getMethod().equals(resultToCheck.getMethod())
                        && result.getEndMillis() == resultToCheck.getEndMillis()) {
                    isFailed = true;
                    break;
                }
            }

            if (!isFailed) {
                System.out.println("Removed failed cases:" + result.getMethod().getMethodName());
                removeMap.addResult(result, result.getMethod());
            }
        }

        for (ITestResult result : removeMap.getAllResults()) {
            ITestResult removeResult = null;
            for (ITestResult resultToCheck : returnValue.getAllResults()) {
                if (result.getMethod().equals(resultToCheck.getMethod())
                        && result.getEndMillis() == resultToCheck.getEndMillis()) {
                    removeResult = resultToCheck;
                    break;
                }
            }

            if (removeResult != null) {
                returnValue.getAllResults().remove(removeResult);
            }
        }
    }

}