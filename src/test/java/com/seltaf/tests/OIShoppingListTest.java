/*
 * Copyright 2015 www.seleniumtests.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seltaf.tests;

import static org.hamcrest.CoreMatchers.is;
import static com.seltaf.core.CustomAssertion.assertThat;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testobject.appium.testng.AppiumDriverProvider;

import com.seltaf.core.CustomAssertion;
import com.seltaf.core.Filter;
import com.seltaf.core.ScreenShot;
import com.seltaf.core.SeltafTestPlan;
import com.seltaf.dataobjects.Item;
import com.seltaf.dataobjects.OIList;
import com.seltaf.driver.DriverManager;
import com.seltaf.helpers.XmlObjectDataHelper;
import com.seltaf.pageobjects.OIShoppingListHomeScreen;
import com.seltaf.util.internal.entity.TestEntity;

import io.appium.java_client.AppiumDriver;



/**
 * Android app test suite.
 */
public class OIShoppingListTest extends SeltafTestPlan implements AppiumDriverProvider  {
	
	@DataProvider(
	        name = "ListData",
	        parallel = false
	    )
	    public static Iterator<Object[]> getData(final Method m)
	        throws Exception {
	        Filter filter = Filter.equalsIgnoreCase(TestEntity.TEST_METHOD,
	                m.getName());

	        LinkedHashMap<String, Class<?>> classMap = new LinkedHashMap<String, Class<?>>();
			 classMap.put("TestEntity", TestEntity.class);
			 classMap.put("OIList", OIList.class);
			 classMap.put("Item", Item.class);
			 Iterator<Object[]> entities = XmlObjectDataHelper.getEntitiesFromxml(classMap,m.getName(), "./src/test/resources/TestData/OIlistdata.xml",filter);
	        
	        return entities;
	    }

    @Test(groups = {"OI","TestObject"},dataProvider = "ListData", description = "Adds New List and add new items to it")
    public void CreateNewListDatadrivenTest(TestEntity testentity,OIList list) throws Exception {

    	OIShoppingListHomeScreen HomeScreen = new OIShoppingListHomeScreen(true);
        HomeScreen.CreateNewList(list);
        CustomAssertion.assertTrue(HomeScreen.getCurrentListName().equalsIgnoreCase(list.getName()), "Verify New List Created and made CurrentList");
        System.out.println(HomeScreen.getCurrentListName());
        for(Item item : list.getItems())
		{
        	HomeScreen.addItem(item);
        	CustomAssertion.assertTrue(HomeScreen.isItemPresent(item), "Verify New item("+item.getName() +") added to CurrentList");

		}
        
    }

	public AppiumDriver getAppiumDriver() {
		
		return (AppiumDriver) ((EventFiringWebDriver) DriverManager.getWebDriver(true)).getWrappedDriver();
	}
}
