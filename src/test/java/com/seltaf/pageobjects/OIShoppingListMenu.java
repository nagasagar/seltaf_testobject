package com.seltaf.pageobjects;

import org.openqa.selenium.By;

import com.seltaf.core.SeltafPageObject;
import com.seltaf.webelements.LabelElement;
import com.seltaf.webelements.LinkElement;

public class OIShoppingListMenu extends SeltafPageObject{
	private static LabelElement identifier_element = new LabelElement("About_Link",By.xpath("//android.widget.TextView[@text='About']"));
	
	private static LinkElement NewList_Link = new LinkElement("NewList_Link",By.xpath("//android.widget.TextView[@text='New list']"));
	private static LinkElement PickItems_Link = new LinkElement("PickItems_Link",By.xpath("//android.widget.TextView[@text='Pick items']"));
	private static LinkElement Theme_Link = new LinkElement("Theme_Link",By.xpath("//android.widget.TextView[@text='Theme']"));
	private static LinkElement Settings_Link = new LinkElement("Settings_Link",By.xpath("//android.widget.TextView[@text='Settings']"));
	private static LinkElement RenameList_Link = new LinkElement("RenameList_Link",By.xpath("//android.widget.TextView[@text='Rename list']"));
	private static LinkElement DeleteList_Link = new LinkElement("DeleteList_Link",By.xpath("//android.widget.TextView[@text='Delete list']"));
	private static LinkElement SendList_Link = new LinkElement("SendList_Link",By.xpath("//android.widget.TextView[@text='Send list']"));
	private static LinkElement MarkAll_Link = new LinkElement("MarkAll_Link",By.xpath("//android.widget.TextView[@text='Mark all items']"));
	
	public OIShoppingListMenu() throws Exception {
		super(identifier_element);
	}
	
	public AddNewListPopUp clickNewListLink() throws Exception {
		NewList_Link.click();
		return new AddNewListPopUp();
	}

}
