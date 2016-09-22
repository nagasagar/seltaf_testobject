package com.seltaf.stepdefinations;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.asserts.Assertion;

import com.seltaf.core.CustomAssertion;
import com.seltaf.core.Filter;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.customexceptions.NoMatchingTestData;
import com.seltaf.helpers.SpreadSheetHelper;
import com.seltaf.pageobjects.AddNewListPopUp;
import com.seltaf.pageobjects.OIShoppingListHomeScreen;
import com.seltaf.util.internal.entity.TestEntity;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class OIShoppingList_Steps {
	
	Map<String,String> TestData = new HashMap<String,String>();
	OIShoppingListHomeScreen HomeScreen;
	AddNewListPopUp popup;
	
	@Given("^OIShoppingList TestData is available for <testcaseid>$")
	public void testdata_is_available_for_testcaseid(DataTable data) throws Throwable {
		String testcaseid = data.asList(String.class).get(0);
		System.out.println(testcaseid);
		Filter filter = Filter.equalsIgnoreCase(TestEntity.TEST_CASE_ID,
				testcaseid);
		 Iterator<Object[]> rdata = SpreadSheetHelper.getDataFromSpreadsheet(
				 null, "./src/test/resources/TestData/OIShoppingListInput.csv", filter,true);
		 if(!rdata.hasNext())
		 {
			 throw new NoMatchingTestData("NO Matching Testdata available - examine your filters");
		 }
		 //read headers and create a map
		 Object[] headers = rdata.next();
		 Object[] values = rdata.next();
		 for(int i=0;i<headers.length;i++){
			 TestData.put(headers[i].toString(), values[i].toString());
		 }
		
	   
	}
	
	@Given("^I am on the HomeScreen$")
	public void i_am_on_the_HomeScreen() throws Throwable {
		HomeScreen = new OIShoppingListHomeScreen(true);
	}

	@When("^I click on Add New List link$")
	public void i_click_on_Add_New_List_link() throws Throwable {
		popup = HomeScreen.clickOnMenuButton().clickNewListLink();
	}

	@When("^I enter Listname and click on OK$")
	public void i_enter_Listname_and_click_OK() throws Throwable {
		popup.addNconfirm(TestData.get("OIList.name"));
	}

	@Then("^New List is created and displayed$")
	public void new_List_is_created_and_displayed() throws Throwable {
		 CustomAssertion.assertTrue(HomeScreen.getCurrentListName().equalsIgnoreCase(TestData.get("OIList.name")), "Verify New List Created and made CurrentList");
	}
	
	@Then("^(.*) is created and is displayed$")
	public void specified_List_is_created_and_displayed(String arg1) throws Throwable {
		 CustomAssertion.assertTrue(HomeScreen.getCurrentListName().equalsIgnoreCase(arg1), "Verify New List Created and made CurrentList");
	}

	@Given("^I have the Newly created List open$")
	public void i_have_the_Newly_created_List_open() throws Throwable {
		HomeScreen = new OIShoppingListHomeScreen(false);
		 CustomAssertion.assertTrue(HomeScreen.getCurrentListName().equalsIgnoreCase(TestData.get("OIList.name")), "Verify New List Created and made CurrentList");
	}

	
	@When("^I add new items$")
	public void i_add_new_items() throws Throwable {
		String[] items = TestData.get("OIList.items").split("#");
		for(String item : items)
		{
        	HomeScreen.addItem(item);
        	//CustomAssertion.assertTrue(HomeScreen.isItemPresent(item), "Verify New item("+item.getName() +") added to CurrentList");

		}
	}

	@Then("^Items are added to the list$")
	public void items_are_added_to_the_list() throws Throwable {
		String[] items = TestData.get("OIList.items").split("#");
		for(String item : items)
		{
        	//HomeScreen.addItem(item);
        	CustomAssertion.assertTrue(HomeScreen.isItemPresent(item), "Verify New item("+item+") added to CurrentList");
        	HomeScreen.captureAPPSnapshot();

		}
	}
	
	
	 @When ("^I enter (.*) and click OK$")
	   public void enter_specified_listname(String listname) throws Exception {
		 popup.addNconfirm(listname);
	 } 
	 
	 @When("^I add few (.*)$")
	 public void i_add_new_items_specified(String arg1) throws Throwable {
		 String[] items = arg1.split(":");
			for(String item : items)
			{
	        	HomeScreen.addItem(item);
	        	//CustomAssertion.assertTrue(HomeScreen.isItemPresent(item), "Verify New item("+item.getName() +") added to CurrentList");

			}
	 }
	 
	 @Then("^(.*) are added to the list.$")
		public void specifieditems_are_added_to_the_list(String arg1) throws Throwable {
			String[] items = arg1.split(":");
			for(String item : items)
			{
	        	//HomeScreen.addItem(item);
	        	CustomAssertion.assertTrue(HomeScreen.isItemPresent(item), "Verify New item("+item+") added to CurrentList");
	        	HomeScreen.captureAPPSnapshot();

			}
		}
	 
	@Before
    public void beforeScenario(Scenario scenario){
 		SeltafContextManager.getThreadContext().clearcucumberVerificationFailures();
 		SeltafTestLogger.log("Started Scenario"+"###"+scenario.getName());
    }
	@After
    public void afterScenario(Scenario scenario){
 		SeltafContextManager.getThreadContext().clearcucumberVerificationFailures();
 		SeltafTestLogger.log("Ended Scenario");
    }
}