package com.seltaf.driver;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import com.seltaf.enums.BrowserType;
import com.seltaf.enums.DriverMode;
import com.seltaf.webdriverfactory.AndroidDriverFactory;
import com.seltaf.webdriverfactory.ChromeDriverFactory;
import com.seltaf.webdriverfactory.FirefoxDriverFactory;
import com.seltaf.webdriverfactory.IEDriverFactory;
import com.seltaf.webdriverfactory.IWebDriverFactory;
import com.seltaf.webdriverfactory.RemoteDriverFactory;
import com.seltaf.driver.CustomEventFiringWebDriver;
import com.seltaf.core.SeltafContext;
import com.seltaf.core.SeltafContextManager;
import com.seltaf.customlisteners.DriverExceptionListener;
import com.seltaf.driver.DriverConfig;



/**
 * Hello world!
 *
 */
public class DriverManager 
{
	private static ThreadLocal<WebDriver> driverSession = new ThreadLocal<WebDriver>();
    private static ThreadLocal<DriverManager> driverManagerSession = new ThreadLocal<DriverManager>();
    private String node;
    private DriverConfig config = new DriverConfig();
    private WebDriver driver;
    private IWebDriverFactory webDriverBuilder;
	
    public static WebDriver getWebDriver() {
        return getWebDriver(false);
    }
	
    public static WebDriver getWebDriver(final Boolean isCreate) {
        if (driverSession.get() == null && isCreate) {
            try {
                getDriverManager().createWebDriver();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return driverSession.get();
    }
	
    public static DriverManager getDriverManager() {
        if (driverManagerSession.get() == null) {
        	driverManagerSession.set(new DriverManager());
        }
        return driverManagerSession.get();
    }
    
    public static void setWebDriver(final WebDriver driver) {
        if (driver == null) {
            driverSession.remove();
        } else {
            if (getDriverManager() == null) {
                new DriverManager();
            }

            driverSession.set(driver);
        }
    }
    
    public WebDriver createRemoteWebDriver(final String browser, final String mode) throws Exception {
      
        config.setBrowser(BrowserType.getBrowserType(browser));
        config.setMode(DriverMode.valueOf(mode));

        if (config.getMode() == DriverMode.ExistingGrid) {
            webDriverBuilder = new RemoteDriverFactory(this.config);
        } else {
            if (config.getBrowser() == BrowserType.FireFox) {
                webDriverBuilder = new FirefoxDriverFactory(this.config);
            } else if (config.getBrowser() == BrowserType.InternetExplore) {
                webDriverBuilder = new IEDriverFactory(this.config);
            } else if (config.getBrowser() == BrowserType.Chrome) {
                webDriverBuilder = new ChromeDriverFactory(this.config);
            } /*else if (config.getBrowser() == BrowserType.HtmlUnit) {
                webDriverBuilder = new HtmlUnitDriverFactory(this.config);
            } else if (config.getBrowser() == BrowserType.Safari) {
                webDriverBuilder = new SafariDriverFactory(this.config);
            } else if (config.getBrowser() == BrowserType.SauceLabs) {
                webDriverBuilder = new SauceLabsDriverFactory(this.config);
            } */else if (config.getBrowser() == BrowserType.Android) {
                webDriverBuilder = new AndroidDriverFactory(this.config);
            } else if (config.getBrowser() == BrowserType.IPhone) {

                // webDriverBuilder = new IPhoneDriverFactory(this.config);
                webDriverBuilder = (IWebDriverFactory) Class.forName(
                                                                "com.seleniumtests.browserfactory.IPhoneDriverFactory")
                                                            .getConstructor(DriverConfig.class).newInstance(
                                                                this.config);
            } else if (config.getBrowser() == BrowserType.IPad) {

                // webDriverBuilder = new IPadDriverFactory(this.config);
                webDriverBuilder = (IWebDriverFactory) Class.forName(
                                                                "com.seleniumtests.browserfactory.IPadDriverFactory")
                                                            .getConstructor(DriverConfig.class).newInstance(
                                                                this.config);
            } else {
                throw new RuntimeException("Unsupported browser" + browser);
            }
        }

        synchronized (this.getClass()) {
            driver = webDriverBuilder.createWebDriver();
        }

        driver = handleListeners(driver);

        return driver;
    }
    
    public DriverManager() {
        init();
        driverManagerSession.set(this);
    }
    
    public DriverManager(final String browser, final String mode) {
        init();
        this.setBrowser(browser);
        this.setMode(mode);
        driverManagerSession.set(this);
    }
    
    public WebDriver createWebDriver() throws Exception {
        System.out.println(Thread.currentThread() + ":" + new Date() + ":::Start creating web driver instance: "
                + this.getBrowser());
        driver = createRemoteWebDriver(config.getBrowser().getBrowserType(), config.getMode().name());

        System.out.println(Thread.currentThread() + ":" + new Date() + ":::Finish creating web driver instance: "
                + this.getBrowser());

        driverSession.set(driver);
        return driver;
    }
    
    protected WebDriver handleListeners(WebDriver driver) {
        ArrayList<WebDriverEventListener> listeners = config.getWebDriverListeners();
        if (listeners != null && listeners.size() > 0) {
            for (int i = 0; i < config.getWebDriverListeners().size(); i++) {
                driver = new CustomEventFiringWebDriver(driver).register(listeners.get(i));
            }
        }

        return driver;
    }
    
    private void init() {
    if (SeltafContextManager.getThreadContext() == null) {
            return;
        }

        String browser = SeltafContextManager.getThreadContext().getWebRunBrowser();
        config.setBrowser(BrowserType.getBrowserType(browser));

        String mode = SeltafContextManager.getThreadContext().getWebRunMode();
        config.setMode(DriverMode.valueOf(mode));

        String hubUrl = SeltafContextManager.getThreadContext().getWebDriverGrid();
        config.setHubUrl(hubUrl);

        String ffProfilePath = SeltafContextManager.getThreadContext().getFirefoxUserProfilePath();
        config.setFfProfilePath(ffProfilePath);

        String operaProfilePath = SeltafContextManager.getThreadContext().getOperaUserProfilePath();
        config.setOperaProfilePath(operaProfilePath);

        String ffBinPath = SeltafContextManager.getThreadContext().getFirefoxBinPath();
        config.setFfBinPath(ffBinPath);

        String chromeBinPath = SeltafContextManager.getThreadContext().getChromeBinPath();
        config.setChromeBinPath(chromeBinPath);

        String chromeDriverPath = SeltafContextManager.getThreadContext().getChromeDriverPath();
        config.setChromeDriverPath(chromeDriverPath);

        String ieDriverPath = SeltafContextManager.getThreadContext().getIEDriverPath();
        config.setIeDriverPath(ieDriverPath);

        int webSessionTimeout = SeltafContextManager.getThreadContext().getWebSessionTimeout();
        config.setWebSessionTimeout(webSessionTimeout);

        double implicitWaitTimeout = SeltafContextManager.getThreadContext().getImplicitWaitTimeout();
        config.setImplicitWaitTimeout(implicitWaitTimeout);

        int explicitWaitTimeout = SeltafContextManager.getThreadContext().getExplicitWaitTimeout();
        config.setExplicitWaitTimeout(explicitWaitTimeout);
        config.setPageLoadTimeout(SeltafContextManager.getThreadContext().getPageLoadTimeout());

        String outputDirectory = SeltafContextManager.getGlobalContext().getOutputDirectory();
        config.setOutputDirectory(outputDirectory);

        if (SeltafContextManager.getThreadContext().isWebProxyEnabled()) {
            String proxyHost = SeltafContextManager.getThreadContext().getWebProxyAddress();
            config.setProxyHost(proxyHost);
        }

        String browserVersion = SeltafContextManager.getThreadContext().getWebBrowserVersion();
        config.setBrowserVersion(browserVersion);

        String webPlatform = SeltafContextManager.getThreadContext().getWebPlatform();
        if (webPlatform != null) {
            config.setWebPlatform(Platform.valueOf(webPlatform));
        }

        if ("false".equalsIgnoreCase(
                    (String) SeltafContextManager.getThreadContext().getAttribute(
                        SeltafContext.Set_Assume_Untrusted_Certificate_Issuer))) {
            config.setSetAssumeUntrustedCertificateIssuer(false);
        }

        if ("false".equalsIgnoreCase(
                    (String) SeltafContextManager.getThreadContext().getAttribute(
                        SeltafContext.Set_Accept_Untrusted_Certificates))) {
            config.setSetAcceptUntrustedCertificates(false);
        }

        if ("false".equalsIgnoreCase(
                    (String) SeltafContextManager.getThreadContext().getAttribute(
                        SeltafContext.ENABLE_JAVASCRIPT))) {
            config.setEnableJavascript(false);
        }

        if (SeltafContextManager.getThreadContext().getNtlmAuthTrustedUris() != null) {
            config.setNtlmAuthTrustedUris(SeltafContextManager.getThreadContext().getNtlmAuthTrustedUris());
        }

        if (SeltafContextManager.getThreadContext().getBrowserDownloadDir() != null) {
            config.setBrowserDownloadDir(SeltafContextManager.getThreadContext().getBrowserDownloadDir());
        }

        if (SeltafContextManager.getThreadContext().getAddJSErrorCollectorExtension() != null) {
            config.setAddJSErrorCollectorExtension(Boolean.parseBoolean(
                    SeltafContextManager.getThreadContext().getAddJSErrorCollectorExtension()));
        }

        String ua = null;
        if (SeltafContextManager.getThreadContext().getUserAgent() != null) {
            ua = SeltafContextManager.getThreadContext().getUserAgent();
        } else {
            ua = null;
        }

        config.setUserAgentOverride(ua);

        String listeners = SeltafContextManager.getThreadContext().getWebDriverListener();
        if (SeltafContextManager.getThreadContext().getEnableExceptionListener()) {
            if (listeners != null) {
                listeners = listeners + ",";
            } else {
                listeners = "";
            }

            listeners = listeners + DriverExceptionListener.class.getName();
        }

        if (listeners != null && !listeners.equals("")) {
            config.setWebDriverListeners(listeners);
        } else {
            config.setWebDriverListeners("");
        }

        config.setUseFirefoxDefaultProfile(SeltafContextManager.getThreadContext().isUseFirefoxDefaultProfile());

        String size = SeltafContextManager.getThreadContext().getBrowserWindowSize();
        if (size != null) {
            int width = -1;
            int height = -1;
            try {
                width = Integer.parseInt(size.split(",")[0].trim());
                height = Integer.parseInt(size.split(",")[1].trim());
            } catch (Exception ex) { }

            config.setBrowserWindowWidth(width);
            config.setBrowserWindowHeight(height);
        }

        String appiumServerURL = SeltafContextManager.getThreadContext().getAppiumServerURL();
        config.setAppiumServerURL(appiumServerURL);

        String automationName = SeltafContextManager.getThreadContext().getAutomationName();
        config.setAutomationName(automationName);

        String mobilePlatformName = SeltafContextManager.getThreadContext().getMobilePlatformName();
        config.setMobilePlatformName(mobilePlatformName);

        String mobilePlatformVersion = SeltafContextManager.getThreadContext().getMobilePlatformVersion();
        config.setMobilePlatformVersion(mobilePlatformVersion);

        String deviceName = SeltafContextManager.getThreadContext().getDeviceName();
        config.setDeviceName(deviceName);

        String app = SeltafContextManager.getThreadContext().getApp();
        config.setApp(app);

        String browserName = SeltafContextManager.getThreadContext().getBrowserName();
        config.setBrowserName(browserName);

        String appPackage = SeltafContextManager.getThreadContext().getAppPackage();
        config.setAppPackage(appPackage);

        String appActivity = SeltafContextManager.getThreadContext().getAppActivity();
        config.setAppActivity(appActivity);

        String newCommandTimeOut = SeltafContextManager.getThreadContext().getNewCommandTimeout();
        config.setNewCommandTimeout(newCommandTimeOut);

        config.setVersion(SeltafContextManager.getThreadContext().getVersion());
        config.setPlatform(SeltafContextManager.getThreadContext().getPlatform());
        config.setSauceLabsURL(SeltafContextManager.getThreadContext().getSaucelabsURL());
        config.setTestType(SeltafContextManager.getThreadContext().getTestType());
        
        config.setTestobject_api_key(SeltafContextManager.getThreadContext().getTestObjectApiKey());
        config.setTestobject_app_id(SeltafContextManager.getThreadContext().getTestObjectAppId());
        config.setTestobject_device(SeltafContextManager.getThreadContext().getTestObjectDevice());
    }

    public String getBrowser() {
        return config.getBrowser().getBrowserType();
    }

    public String getPlatform() {
        return config.getWebPlatform().name();
    }

    public String getBrowserVersion() {
        return config.getBrowserVersion();
    }

    public String getChromeBinPath() {
        return config.getChromeBinPath();
    }

    public String getChromeDriverPath() {
        return config.getChromeDriverPath();
    }

    public DriverConfig getConfig() {
        return config;
    }

    public int getExplicitWait() {
        return config.getExplicitWaitTimeout();
    }

    public String getFfBinPath() {
        return config.getFirefoxBinPath();
    }

    public String getFfProfilePath() throws URISyntaxException {
        return config.getFirefoxProfilePath();
    }

    public String getOperaProfilePath() throws URISyntaxException {
        return config.getOperaProfilePath();
    }

    public void setOperaProfilePath(final String operaProfilePath) {
        config.setOperaProfilePath(operaProfilePath);
    }

    public String getHubUrl() {
        return config.getHubUrl();
    }

    public String getIEDriverPath() {
        return config.getIeDriverPath();
    }

    public double getImplicitWait() {
        return config.getImplicitWaitTimeout();
    }

    public String getMode() {
        return config.getMode().name();
    }

    public String getOutputDirectory() {
        return config.getOutputDirectory();
    }

    public String getNtlmAuthTrustedUris() {
        return config.getNtlmAuthTrustedUris();
    }

    public void setNtlmAuthTrustedUris(final String url) {
        config.setNtlmAuthTrustedUris(url);
    }

    public int getPageLoadTimeout() {
        return config.getPageLoadTimeout();
    }

    public String getProxyHost() {
        return config.getProxyHost();
    }

    public void setUserAgentOverride(final String userAgentOverride) {
        config.setUserAgentOverride(userAgentOverride);
    }

    public String getUserAgentOverride() {
        return config.getUserAgentOverride();
    }

    public IWebDriverFactory getWebDriverBuilder() {
        return webDriverBuilder;
    }

    public int getWebSessionTimeout() {
        return config.getWebSessionTimeout();
    }

    
    public boolean isSetAcceptUntrustedCertificates() {
        return config.isSetAcceptUntrustedCertificates();
    }

    public boolean isAddJSErrorCollectorExtension() {
        return config.isAddJSErrorCollectorExtension();
    }

    public void setAddJSErrorCollectorExtension(final Boolean addJSErrorCollectorExtension) {
        config.setAddJSErrorCollectorExtension(addJSErrorCollectorExtension);
    }

    public boolean isSetAssumeUntrustedCertificateIssuer() {
        return config.isSetAssumeUntrustedCertificateIssuer();
    }

    public boolean isEnableJavascript() {
        return config.isEnableJavascript();
    }

    public void setEnableJavascript(final Boolean enableJavascript) {
        config.setEnableJavascript(enableJavascript);
    }

    public void setBrowser(final String browser) {
        config.setBrowser(BrowserType.getBrowserType(browser));

    }

    public void setBrowserVersion(final String browserVersion) {
        config.setBrowserVersion(browserVersion);
    }

    public void setPlatform(final String platform) {
        config.setWebPlatform(Platform.valueOf(platform));
    }

    public void setChromeBinPath(final String chromeBinPath) {
        config.setChromeBinPath(chromeBinPath);
    }

    public void setBrowserDownloadDir(final String browserDownloadDir) {
        config.setBrowserDownloadDir(browserDownloadDir);
    }

    public String getBrowserDownloadDir() {
        return config.getBrowserDownloadDir();
    }

    public void setChromeDriverPath(final String chromeDriverPath) {
        config.setChromeDriverPath(chromeDriverPath);
    }

    public void setConfig(final DriverConfig config) {
        this.config = config;
    }

    public void setExplicitTimeout(final int explicitWaitTimeout) {
        config.setExplicitWaitTimeout(explicitWaitTimeout);
    }

    public void setFfBinPath(final String ffBinPath) {
        config.setFfBinPath(ffBinPath);
    }

    public void setFfProfilePath(final String ffProfilePath) {
        config.setFfProfilePath(ffProfilePath);
    }

    public void setHubUrl(final String hubUrl) {
        config.setHubUrl(hubUrl);
    }

    public void setIEDriverPath(final String ieDriverPath) {
        config.setIeDriverPath(ieDriverPath);
    }

    public void setImplicitlyWaitTimeout(final double implicitTimeout) {
        config.setImplicitWaitTimeout(implicitTimeout);
    }

    public void setMode(final String mode) {
        config.setMode(DriverMode.valueOf(mode));
    }

    public void setOutputDirectory(final String outputDirectory) {
        config.setOutputDirectory(outputDirectory);
    }

    public void setPageLoadTimeout(final int pageLoadTimeout) {
        config.setPageLoadTimeout(pageLoadTimeout);
    }

    public void setProxyHost(final String proxyHost) {
        config.setProxyHost(proxyHost);
    }

    public void setSetAcceptUntrustedCertificates(final boolean setAcceptUntrustedCertificates) {
        config.setSetAcceptUntrustedCertificates(setAcceptUntrustedCertificates);
    }

    public void setSetAssumeUntrustedCertificateIssuer(final boolean setAssumeUntrustedCertificateIssuer) {
        config.setSetAssumeUntrustedCertificateIssuer(setAssumeUntrustedCertificateIssuer);
    }

    public void setWebDriverBuilder(final IWebDriverFactory builder) {
        this.webDriverBuilder = builder;
    }

    public void setWebSessionTimeout(final int webSessionTimeout) {
        config.setWebSessionTimeout(webSessionTimeout);
    }
    
    public static void cleanUp() {
        IWebDriverFactory iWebDriverFactory = getDriverManager().webDriverBuilder;
        if (iWebDriverFactory != null) {
            iWebDriverFactory.cleanUp();
        } else {
            WebDriver driver = driverSession.get();
            if (driver != null) {
                try {
                    driver.quit();
                } catch (WebDriverException ex) {
                    ex.printStackTrace();
                }

                driver = null;
            }
        }

        driverSession.remove();
        driverManagerSession.remove();
    }

}
