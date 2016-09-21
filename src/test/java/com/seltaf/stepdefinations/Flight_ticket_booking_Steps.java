package com.seltaf.stepdefinations;

import static com.seltaf.core.CustomAssertion.assertEquals;
import static com.seltaf.core.CustomAssertion.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.asserts.Assertion;

import com.seltaf.core.Filter;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.customexceptions.NoMatchingTestData;
import com.seltaf.helpers.SpreadSheetHelper;

import com.seltaf.util.internal.entity.TestEntity;
import com.seltaf.webelements.ImageElement;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Flight_ticket_booking_Steps {
	/*FlightSearchinput searchinput;
	Passenger passenger;
	CreditCard card;
	HomePage homepage;
	MercureToursLoginPage loginpage;
	FlightFinderPage flightfinderpage;
	FlightResultsPage flightresultspage;
	FlightBookingPage bookingpage;
	BookingConfirmationPage confirmationpage;
	
	@Given("^TestData is available for <testcaseid>$")
	public void testdata_is_available_for_testcaseid(DataTable data) throws Throwable {
		String testcaseid = data.asList(String.class).get(0);
		System.out.println(testcaseid);
		Filter filter = Filter.equalsIgnoreCase(TestEntity.TEST_CASE_ID,
				testcaseid);
        LinkedHashMap<String, Class<?>> classMap =
            new LinkedHashMap<String, Class<?>>();
        classMap.put("TestEntity", TestEntity.class);
        classMap.put("FlightSearchinput", FlightSearchinput.class);
        classMap.put("Passenger", Passenger.class);
        classMap.put("CreditCard", CreditCard.class);
		 Iterator<Object[]> rdata = SpreadSheetHelper.getEntitiesFromSpreadsheet(
	        		MercuryToursFlightBookingTest.class, classMap, "flightsearchinput.csv", filter);
		 if(!rdata.hasNext())
		 {
			 throw new NoMatchingTestData("NO Matching Testdata available - examine your filters");
		 }
		 while (rdata.hasNext())
		 {
			 Object[] elements = rdata.next();
	            
				for	(Object element:elements)
				{
					if(element.getClass().equals(FlightSearchinput.class))
					{
						searchinput = (FlightSearchinput) element;
	            	}
					if(element.getClass().equals(Passenger.class))
					{
						passenger = (Passenger) element ;
					}
					if(element.getClass().equals(CreditCard.class))
					{
						card = (CreditCard) element ;
					}
				}
	            	
	         
		 }
	   
	}
	@Given("^I am on the HomePage$")
	public void i_am_on_the_HomePage() throws Throwable {
		homepage = new HomePage(true);
	}

	@When("^I click on SIGN-ON link$")
	public void i_click_on_SIGN_ON_link() throws Throwable {
		loginpage = homepage.navigateToLoginPage();
	}

	@When("^enter login credentials and sign in$")
	public void enter_login_credentials_and_sign_in() throws Throwable {
		flightfinderpage =loginpage.login2Website();
	}

	@Then("^Flight finder page is displayed$")
	public void flight_finder_page_is_displayed() throws Throwable {
		ImageElement identifier_element = new ImageElement("Flight Finder Image",By.cssSelector("img[src='/images/masts/mast_flightfinder.gif']"));
		flightfinderpage.assertElementPresent(identifier_element);
	}

	@Given("^I am on the Flight finder page$")
	public void i_am_on_the_Flight_finder_page() throws Throwable {
		ImageElement identifier_element1 = new ImageElement("Flight Finder Image",By.cssSelector("img[src='/images/masts/mast_flightfinder.gif']"));
		flightfinderpage = new FlightFinderPage();
		flightfinderpage.assertElementPresent(identifier_element1);
		
	}

	@When("^I enter search parameters$")
	public void i_enter_search_parameters() throws Throwable {
		flightresultspage= flightfinderpage.Searchflight(searchinput);
	}

	@When("^click on search$")
	public void click_on_search() throws Throwable {
	    
	}

	@Then("^flight search results should be displayed$")
	public void flight_search_results_should_be_displayed() throws Throwable {
		ImageElement identifier_element2 = new ImageElement("Sign - on Image",By.cssSelector("img[src='/images/masts/mast_selectflight.gif']"));
		
		flightresultspage.assertElementPresent(identifier_element2);
	}

	@Given("^I am on flight search results page$")
	public void i_am_on_flight_search_results_page() throws Throwable {
		ImageElement identifier_element3 = new ImageElement("Sign - on Image",By.cssSelector("img[src='/images/masts/mast_selectflight.gif']"));
		flightresultspage = new FlightResultsPage();
		flightresultspage.assertElementPresent(identifier_element3);
	}

	@Given("^I Select the cheapest flights$")
	public void i_Select_the_cheapest_flights() throws Throwable {
		flightresultspage.select_cheap_outgoing_flight();
		flightresultspage.select_cheap_return_flight();
	}

	@Given("^I click on contniue$")
	public void i_click_on_contniue() throws Throwable {
		bookingpage = flightresultspage.continue_with_selected_flight_options();
	}

	@Then("^Book a flight page should be displayed$")
	public void book_a_flight_page_should_be_displayed() throws Throwable {
		ImageElement identifier_element4 = new ImageElement("Book a Flight Image",By.cssSelector("img[src='/images/masts/mast_book.gif']"));
		bookingpage.assertElementPresent(identifier_element4);
	}

	@Given("^I am on Book a flight page$")
	public void i_am_on_Book_a_flight_page() throws Throwable {
		ImageElement identifier_element5 = new ImageElement("Book a Flight Image",By.cssSelector("img[src='/images/masts/mast_book.gif']"));
		bookingpage = new FlightBookingPage();
		bookingpage.assertElementPresent(identifier_element5);
	}

	@Given("^I enter Passenger and payment information$")
	public void i_enter_Passenger_and_payment_information() throws Throwable {
		if(passenger.getFirstName().equalsIgnoreCase("nagasagar")||passenger.getFirstName().equalsIgnoreCase("web"))
			assertEquals(true, false, passenger.getFirstName()+" is a realuser");
	    bookingpage.enter_passenger_information(passenger);
	    bookingpage.enter_creditcard_information(card);
	    bookingpage.enter_billingaddress_information(null);
	    bookingpage.enter_deliveryaddress_information(null);
	}

	@Given("^I click on Purchase Tickets button$")
	public void i_click_on_Purchase_Tickets_button() throws Throwable {
		confirmationpage = bookingpage.proceed_2_book();
	}

	@Then("^Flight confirmation page should be displayed with booking confirmation number$")
	public void flight_confirmation_page_should_be_displayed_with_booking_confirmation_number() throws Throwable {
		String conf_num = confirmationpage.getConfirmationNumber();
        assertThat("Confirmation number is missing on Booking Confirmation Page!!!",	conf_num.length()>1, is(true));
	}
	
	@Before
    public void beforeScenario(Scenario scenario){
 		SeltafContextManager.getThreadContext().clearcucumberVerificationFailures();
 		SeltafTestLogger.log("Started Scenario");
    }
	@After
    public void afterScenario(Scenario scenario){
 		SeltafContextManager.getThreadContext().clearcucumberVerificationFailures();
 		SeltafTestLogger.log("Ended Scenario");
    }*/
}