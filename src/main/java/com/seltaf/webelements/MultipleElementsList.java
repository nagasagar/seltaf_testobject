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

package com.seltaf.webelements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import com.seltaf.core.SeltafTestLogger;
import com.seltaf.driver.DriverManager;

/**
 * Support both standard select tag and fake select consists of tag ul and li.
 */
public class MultipleElementsList extends HtmlElement {

     protected List<WebElement> elements = null;

    public MultipleElementsList(final String text, final By by) {
        super(text, by);
    }

    protected void findElement() {
        driver = DriverManager.getWebDriver();
        try {
        	elements = driver.findElements(this.getBy());
        } catch (UnexpectedTagNameException e) {
            if (element.getTagName().equalsIgnoreCase("ul")) {
            	elements = element.findElements(By.tagName("li"));
            }
        }
    }

    public List<WebElement> getElements() {
        findElement();
        return elements;
    }



    public WebElement getElementByIndex(int i) {
        findElement();
        return elements.get(i);
    }

    public List<String> getallTexts() {
        findElement();

        List<String> valueList = new ArrayList<String>();
        for (WebElement ele : elements) {
            valueList.add(ele.getText());
        }
        return valueList;
    }


    public WebElement getElementByText(final String text) {
        SeltafTestLogger.logWebStep(null, "make selection using text\"" + text + "\" on " + toHTML(), false);
        findElement();
        if (elements == null) {
        	return driver.findElement(By.xpath("//li[text()='" + text + "']"));
            
        }
        for (WebElement ele : elements) {
           String  selectedText = ele.getText();
           if (selectedText.equals(text)) {
                return ele;
            }
        }
        return null;
    }

}
