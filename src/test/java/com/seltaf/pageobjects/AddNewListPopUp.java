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

public class AddNewListPopUp extends SeltafPageObject {
	private static LabelElement identifier_element = new LabelElement("AddNewlist Popup Header",By.xpath("//android.widget.TextView[@text='Enter name of new shopping list']"));
	
	private static TextFieldElement NewList_TextField = new TextFieldElement("NewItem TextField", By.xpath("//android.widget.EditText[@text='e.g. wishlist, family list']"));
	
	private static ButtonElement cancelNewListButton = new ButtonElement("cancelNewListButton", By.xpath("//android.widget.Button[@text='Cancel']"));
	
	private static ButtonElement confirmNewListButton = new ButtonElement("confirmNewListButton", By.xpath("//android.widget.Button[@text='OK']"));
	
	public AddNewListPopUp() throws Exception {
		super(identifier_element);
	}

	public OIShoppingListHomeScreen addNconfirm(OIList list) throws Exception {
		NewList_TextField.sendKeys(list.getName());
		confirmNewListButton.click();
		return new OIShoppingListHomeScreen();
	}
	
	public OIShoppingListHomeScreen addNcancel(OIList list) throws Exception {
		NewList_TextField.sendKeys(list.getName());
		cancelNewListButton.click();
		return new OIShoppingListHomeScreen();
	}


}
 	