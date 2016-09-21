package com.seltaf.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.seltaf.core.CustomAssertion;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.core.SeltafPageObject;
import com.seltaf.dataobjects.Item;
import com.seltaf.dataobjects.OIList;
import com.seltaf.helpers.WaitHelper;
import com.seltaf.webelements.ButtonElement;
import com.seltaf.webelements.LabelElement;
import com.seltaf.webelements.MultipleElementsList;
import com.seltaf.webelements.TextFieldElement;

public class OIShoppingListHomeScreen extends SeltafPageObject {
	private static LabelElement identifier_element = new LabelElement("APP Header",By.xpath("//android.widget.TextView[@text='OI Shopping List']"));
	
	private static ButtonElement CleanupButton = new ButtonElement("CleanUp Button", By.xpath("//android.widget.TextView[@content-desc='Clean up list']"));
	
	private static ButtonElement MenuButton = new ButtonElement("Menu Button", By.xpath("//android.widget.ImageButton[@content-desc='More options']"));
	
	private static TextFieldElement CurrentList = new TextFieldElement("Current List", By.xpath("//android.widget.Spinner[@resource-id='org.openintents.shopping:id/spinner_listfilter']/android.widget.TextView[@resource-id='android:id/text1']"));
	
	private static ButtonElement SwitchListSpinner = new ButtonElement("Switch List Spinner", By.xpath("//android.widget.Spinner[@resource-id='org.openintents.shopping:id/spinner_listfilter']"));
	
	private static TextFieldElement NewItem_TextField = new TextFieldElement("NewItem TextField", By.xpath("//android.widget.EditText[@resource-id='org.openintents.shopping:id/autocomplete_add_item']"));
	
	private static ButtonElement ADDButton = new ButtonElement("ADD Button", By.xpath("//android.widget.Button[@resource-id='org.openintents.shopping:id/button_add_item']"));
	
	private static TextFieldElement NewList_TextField = new TextFieldElement("NewItem TextField", By.xpath("//android.widget.EditText[@text='e.g. wishlist, family list']"));
	
	private static ButtonElement cancelNewListButton = new ButtonElement("cancelNewListButton", By.xpath("//android.widget.Button[@text='Cancel']"));
	
	private static ButtonElement confirmNewListButton = new ButtonElement("confirmNewListButton", By.xpath("//android.widget.Button[@text='OK']"));
	
	private static MultipleElementsList ItemsList = new MultipleElementsList("Items List", By.xpath("//android.widget.ListView[@resource-id='org.openintents.shopping:id/list_items']//android.widget.LinearLayout//android.widget.TextView"));
	
	
	public OIShoppingListHomeScreen(final boolean openAPP) throws Exception {
	        super(identifier_element, openAPP ? SeltafContextManager.getThreadContext().getApp() : null);
	        
	    }	 
	 
	 public OIShoppingListHomeScreen() throws Exception {
		super(identifier_element);
	}

	public OIShoppingListMenu clickOnMenuButton() throws Exception
	 {
		 MenuButton.click();
		 return new OIShoppingListMenu();		 
	 }
	
	public OIShoppingListHomeScreen CreateNewList(OIList list) throws Exception {
		OIShoppingListMenu menu = clickOnMenuButton();	
		AddNewListPopUp popup = menu.clickNewListLink();
		return popup.addNconfirm(list);
	}
	
	public String getCurrentListName(){
		return CurrentList.getText();
	}

	public OIShoppingListHomeScreen addItem(Item item) {
		NewItem_TextField.sendKeys(item.getName());
		ADDButton.click();
		return this;
	}
	
	public boolean isItemPresent(Item item)
	{
		boolean flag = false ;
		for (WebElement ele : ItemsList.getElements())
		{
			if(ele.getText().equalsIgnoreCase(item.getName()))
			{
				flag = true;
				break;
			}
		}
		return flag;
	}
	

}
 	