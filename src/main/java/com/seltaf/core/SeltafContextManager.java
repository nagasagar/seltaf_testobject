package com.seltaf.core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.ITestContext;
import org.testng.xml.XmlTest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.seltaf.core.SeltafContext;
import com.seltaf.enums.TestType;
import com.seltaf.utils.XMLUtility;





public class SeltafContextManager {
	  // global level context
    private static SeltafContext globalContext;

    // test level context
    private static Map<String, SeltafContext> testLevelContext = Collections.synchronizedMap(
            new HashMap<String, SeltafContext>());

    // thread level SeleniumTestsContext
    private static ThreadLocal<SeltafContext> threadLocalContext = new ThreadLocal<SeltafContext>();
    
    public static SeltafContext getGlobalContext() {
        if (globalContext == null) {
            System.out.println("Initialize default GlobalContext");
            initGlobalContext(new DefaultTestNGContext());
        }

        return globalContext;
    }

    public static SeltafContext getTestLevelContext(final ITestContext testContext) {
        if (testContext != null && testContext.getCurrentXmlTest() != null) {
            if (testLevelContext.get(testContext.getCurrentXmlTest().getName()) == null) {
                initTestLevelContext(testContext, testContext.getCurrentXmlTest());
            }

            return testLevelContext.get(testContext.getCurrentXmlTest().getName());
        } else {
            return null;
        }
    }

    public static SeltafContext getTestLevelContext(final String testName) {
        return testLevelContext.get(testName);
    }

    public static SeltafContext getThreadContext() {
        if (threadLocalContext.get() == null) {
            System.out.println("Initialize default ThreadContext");
            initThreadContext(null, null);
        }

        return threadLocalContext.get();
    }
    
    public static void initGlobalContext(ITestContext testNGCtx) {
        testNGCtx = getContextFromConfigFile(testNGCtx);
        globalContext = new SeltafContext(testNGCtx);
    }
    
    private static ITestContext getContextFromConfigFile(final ITestContext iTestContext) {
        if (iTestContext != null) {

            // "testConfig" parameter can be define in testng.xml file
            // This parameter points to a config xml file which defines test configuration parameters
            // Hence testng.xml file can focus on test
            if (iTestContext.getSuite().getParameter(SeltafContext.TEST_CONFIGURATION) != null) {
                File suiteFile = new File(iTestContext.getSuite().getXmlSuite().getFileName());
                String configFile = suiteFile.getPath().replace(suiteFile.getName(), "")
                        + iTestContext.getSuite().getParameter("testConfig");
                NodeList nList = XMLUtility.getXMLNodes(configFile, "parameter");
                Map<String, String> parameters = iTestContext.getSuite().getXmlSuite().getParameters();
                for (int i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    parameters.put(nNode.getAttributes().getNamedItem("name").getNodeValue(),
                        nNode.getAttributes().getNamedItem("value").getNodeValue());
                }

                iTestContext.getSuite().getXmlSuite().setParameters(parameters);
            }
        }

        return iTestContext;
    }
    
    public static void initTestLevelContext(final ITestContext testNGCtx, final XmlTest xmlTest) {
    	SeltafContext seleniumTestsCtx = new SeltafContext(testNGCtx);
        if (xmlTest != null) {
            Map<String, String> testParameters = xmlTest.getTestParameters();

            // parse the test level parameters
            for (Entry<String, String> entry : testParameters.entrySet()) {
                seleniumTestsCtx.setAttribute(entry.getKey(), entry.getValue());
            }

        }

        testLevelContext.put(xmlTest.getName(), seleniumTestsCtx);
    }

    public static void initTestLevelContext(final XmlTest xmlTest) {
        initTestLevelContext(globalContext.getTestNGContext(), xmlTest);
    }
    
    public static void initThreadContext() {
        initThreadContext(globalContext.getTestNGContext(), null);
    }

    public static void initThreadContext(final ITestContext testNGCtx) {
        initThreadContext(testNGCtx, null);
    }

    public static void initThreadContext(final ITestContext testNGCtx, final XmlTest xmlTest) {
    	SeltafContext SeltafCtx = new SeltafContext(testNGCtx);

        if (xmlTest != null) {
            Map<String, String> testParameters = xmlTest.getTestParameters();

            // parse the test level parameters
            for (Entry<String, String> entry : testParameters.entrySet()) {

                if (System.getProperty(entry.getKey()) == null) {
                	SeltafCtx.setAttribute(entry.getKey(), entry.getValue());
                }

            }

        }

        threadLocalContext.set(SeltafCtx);
    }

    public static void initThreadContext(final XmlTest xmlTest) {
        initThreadContext(globalContext.getTestNGContext(), xmlTest);
    }

	public static boolean isWebTest() {
        return (getThreadContext().getTestType().equalsIgnoreCase(TestType.WEB.toString()));
    }

}
