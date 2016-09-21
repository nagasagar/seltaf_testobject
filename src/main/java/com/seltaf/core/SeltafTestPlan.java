package com.seltaf.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.xml.XmlTest;
import com.seltaf.driver.DriverManager;


public class SeltafTestPlan {
	
	 private static final Logger logger = SeltafTestLogger.getLogger(SeltafTestPlan.class);
	  private Date start;
	
	@BeforeSuite(alwaysRun = true)
    public void beforeTestSuite(final ITestContext testContext) throws IOException {
        System.out.println("####################################################");
        System.out.println("Starting Test Execution");
        System.out.println("####################################################");
        start = new Date();
        SeltafContextManager.initGlobalContext(testContext);
        SeltafContextManager.initThreadContext(testContext, null);        
    }
	
	 @BeforeTest(alwaysRun = true)
	 public void beforeTest(final ITestContext testContext, final XmlTest xmlTest) {
		 SeltafContextManager.initTestLevelContext(testContext, xmlTest);
	 }
	 
	 @BeforeMethod(alwaysRun = true)
	 public void beforeTestMethod(final Object[] parameters, final Method method, final ITestContext testContex,
	            final XmlTest xmlTest) {
	        logger.info(Thread.currentThread() + " Start method " + method.getName());
	        SeltafContextManager.initThreadContext(testContex, xmlTest);
	        if (method != null) {
	        	SeltafContextManager.getThreadContext().setAttribute(SeltafContext.TEST_METHOD_SIGNATURE,
	                buildMethodSignature(method, parameters));
	        }
	  }

	 @AfterMethod(alwaysRun = true)
	 public void afterTestMethod(final Object[] parameters, final Method method, final ITestContext testContex,
	            final XmlTest xmlTest) {
	        List<TearDownService> serviceList = SeltafContextManager.getThreadContext().getTearDownServices();
	        if (serviceList != null && !serviceList.isEmpty()) {
	            for (TearDownService service : serviceList) {
	                service.tearDown();
	            }
	        }

	        DriverManager.cleanUp();
	        logger.info(Thread.currentThread() + " Finish method " + method.getName());
	}
	 
	@AfterSuite(alwaysRun = true)
    public void afterTestSuite() throws IOException {
		logger.info("Test Suite Execution Time: " + (new Date().getTime() - start.getTime()) / 1000 / 60 + " minutes.");
    }

	
	private String buildMethodSignature(final Method method, final Object[] parameters) {
        return method.getDeclaringClass().getCanonicalName() + "." + method.getName() + "("
                + buildParameterString(parameters) + ")";
    }
	
	 private String buildParameterString(final Object[] parameters) {
	        StringBuffer parameter = new StringBuffer();

	        if (parameters != null) {
	            for (int i = 0; i < parameters.length; i++) {
	                if (parameters[i] == null) {
	                    parameter.append("null, ");
	                } else if (parameters[i] instanceof java.lang.String) {
	                    parameter.append("\"").append(parameters[i]).append("\", ");
	                } else {
	                    parameter.append(parameters[i]).append(", ");
	                }
	            }
	        }

	        if (parameter.length() > 0) {
	            parameter.delete(parameter.length() - 2, parameter.length() - 1);
	        }

	        return parameter.toString();
	    }


}
