package com.seltaf.tests;

import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import org.testng.internal.Utils;

import com.cucumber.listener.ExtentCucumberFormatter;
import com.relevantcodes.extentreports.LogStatus;
import com.seltaf.core.CustomAssertion;
import com.seltaf.core.SeltafTestPlan;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.testng.TestNGCucumberRunner;
import gherkin.formatter.model.Result;
import cucumber.api.testng.CucumberFeatureWrapper;

@CucumberOptions(
		features = "classpath:features",
				glue={"com.seltaf.stepdefinations"},
		plugin = {"html:target/cucumber-html-report","com.cucumber.listener.ExtentCucumberFormatter"},
		
		tags = {}
		)
public class RunCukesTest extends SeltafTestPlan{
		
	 private TestNGCucumberRunner testNGCucumberRunner;

	    @BeforeClass(alwaysRun = true)
	    public void setUpClass() throws Exception {
	        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
	        // Initiates the extent report and generates the output in the output/Run_<unique timestamp>/report.html file by default.
			 ExtentCucumberFormatter.initiateExtentCucumberFormatter();
	    }

	    @Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
	    public void feature(CucumberFeatureWrapper cucumberFeature) {
	        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
	    }

	    @DataProvider
	    public Object[][] features() {
	    	Object[][] features = testNGCucumberRunner.provideFeatures();
	        return features;
	    }

	    @AfterClass(alwaysRun = true)
	    public void tearDownClass() throws Exception {
	        testNGCucumberRunner.finish();
	    }
	   
}