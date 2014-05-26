package org.simbasecurity.webdriver.framework;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.concurrent.TimeUnit;


public class Browser {

    private static final int TIME_OUT_IN_SECONDS = 10;
    private static FirefoxDriver browser;
    
        
    public static void open() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.no_proxies_on", "localhost");
        profile.setPreference("intl.accept_languages", "en");
        browser = new FirefoxDriver(profile);
        browser.manage().timeouts().implicitlyWait(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS);
    }
    
    public static FirefoxDriver getDriverInstance() {
        return browser;
    }
    
    public static String getTitle() {
        return browser.getTitle();
    }
      
      
    public static void acceptConfirmation() {
        browser.switchTo().alert().accept();
    }
    
    public static void goToPage(String url) {
        try {
            browser.get(url);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to navigate to url " + url, e);
        }
    }
    
    public static Object executeScript(final String script, final Object... args) {
        return browser.executeScript(script, args);
    }
    
    public static void quit() {
        if (browser != null) {
            browser.quit();
            browser = null;
        }
    }

    /**
     * Should be used for menu items
     */
    public static void javaScriptClick(WebElement webElement) {
        Browser.executeScript("return document.getElementById('"
                + webElement.getAttribute("id")
                + "').click()");
    }
}