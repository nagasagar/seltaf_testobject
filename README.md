# seltaf_testobject
Sample automation tests for IOshopping list for TestObject

This project is a sample project which automates 'OI Shopping List'

tests are present in the folder /src/test/java

com.seltaf.dataobjects - contains pojo objects to hold test data
com.seltaf.pageobjects - contains page objects (page object framework is used)
com.seltaf.tests - contains sample data driven test which verifies the functionality of creating a new list and adds items to the list



framework components are present in /src/main/java (will be distributed via public maven repository soon)

seltaf is a test automation framework which i have devoloped and currenty enhancing   is opensource and is available on github  (https://github.com/nagasagar/seltaf)


#### Testobject specific changes incorporated

Test implements 'AppiumDriverprovider' interface and implements getAppiumDriver()

all the desired capabilities required for a test to run on testobject cloud are passed via testng.xml file

	<test name="OIShoppingListTest">
	    <parameter name="testobject_api_key" value="24918D348DF845808356FC21018CF57A"/>
        <parameter name="testobject_app_id" value="1"/>
        <parameter name="testobject_device" value="Samsung_Google_Nexus_10_P8110_real"/>
	    <parameter name="browser" value="android" />
	    <parameter name="testType" value="app" />
        <parameter name="appiumServerURL" value="http://appium.testobject.com/wd/hub"/>        
        <parameter name="automationName" value="Appium"/>
        <parameter name="platform" value="Android"/>
        <parameter name="mobilePlatformVersion" value="5.1"/>
        <parameter name="deviceName" value="BX903HYQWF"/>
        <parameter name="appPackage" value="org.openintents.shopping"/>
        <parameter name="appActivity" value="org.openintents.shopping.ShoppingActivity"/>
        <parameter name="browserName" value=""/>
        <parameter name="newCommandTimeout" value="120"/>
        <parameter name="parallel" value="false"/>
		<classes>
			<class name="com.seltaf.tests.OIShoppingListTest" />
		</classes>
	</test>
	
and watcher is also configured in testng.xml


	<listeners>
         <listener class-name="org.testobject.appium.testng.TestObjectTestNGTestResultWatcher" />
         <!-- <listener class-name="com.seltaf.customlisteners.ExtentReporterNG" /> -->
    	</listeners>
    
  look at testng.xml for more details.
  
  # Test Results
  
  Results of the test executed on testobject cloud are available here -> https://app.testobject.com/#/share/46c09d06-6b50-4041-b311-31fb7417c6a0/suite-report
  
  
	
