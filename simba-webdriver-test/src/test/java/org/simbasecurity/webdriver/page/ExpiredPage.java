package org.simbasecurity.webdriver.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.simbasecurity.webdriver.framework.Browser;

import static org.junit.Assert.assertTrue;


public class ExpiredPage {
    
    @FindBy(id = "errorMessage")
    private WebElement errorMessage;
     
    public ExpiredPage(){
    	PageFactory.initElements(Browser.getDriverInstance(), this);
    }
    
	public void assertErrorLoginTokeExpired() {
		String bodyText = Browser.getDriverInstance().findElement(By.tagName("body")).getText();
        assertTrue("Text not found!", bodyText.contains("You waited too long to log in. Please go to the application again."));
	}
    
}