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

package com.seltaf.util.internal.entity;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="testentitiy")
public class TestEntity {
    public static final String TEST_METHOD = "TestEntity.TestMethod";
    public static final String TEST_CASE_ID = "TestEntity.TestCaseId";
    public static final String TEST_DP_TAGS = "TestEntity.TestTags";

    private String testCaseId = "";
    private String testMethod = "";
    private String testTags = "";

    @XmlElement(name="TestcaseID")
    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(final String testCaseId) {
        this.testCaseId = testCaseId;
    }

    @XmlElement(name="TestMethod")
    public String getTestMethod() {
        return testMethod;
    }
    @XmlElement(name="TestTags")
    public String getTestTags() {
		return testTags;
	}

	public void setTestTags(String testTags) {
		this.testTags = testTags;
	}

	public void setTestMethod(final String testMethod) {
        this.testMethod = testMethod;
    }

    public String toString() {
        return ("Test Attributes: [ TestCaseId: " + testCaseId) + " ]";
    }
    
    public Map<String,Object> toMap() {
    	Map<String, Object> testentitymap =  new HashMap<String,Object> ();
    	testentitymap.put(TEST_METHOD, this.getTestMethod());
    	testentitymap.put(TEST_CASE_ID, this.getTestCaseId());
    	testentitymap.put(TEST_DP_TAGS, this.getTestTags());
    	
        return testentitymap;
    }
}
