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

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.seltaf.core.SeltafTestLogger;
import com.seltaf.enums.BrowserType;
import com.seltaf.driver.DriverManager;

public class ButtonElement extends HtmlElement {

    public ButtonElement(final String label, final By by) {
        super(label, by);
    }

    @Override
    public void click() {
        SeltafTestLogger.logWebStep(null, "click on " + toHTML(), false);

        BrowserType browser = DriverManager.getDriverManager().getConfig().getBrowser();
        if (browser == BrowserType.InternetExplore) {
            super.sendKeys(Keys.ENTER);
        } else {
            super.click();
        }
    }

    public void submit() {
    	SeltafTestLogger.logWebStep(null, "Submit form by clicking on " + toHTML(), false);
        findElement();
        element.submit();
    }
}
