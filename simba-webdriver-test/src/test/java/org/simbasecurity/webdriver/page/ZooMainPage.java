package org.simbasecurity.webdriver.page;


import static org.junit.Assert.assertTrue;
import static org.simbasecurity.webdriver.framework.Browser.javaScriptClick;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.simbasecurity.webdriver.framework.Browser;

public class ZooMainPage {
    
	@FindBy(id = "content")
    protected WebElement content;
	
	@FindBy (id="logoutLink")
	protected WebElement logoutLink;
	
	@FindBy (id="changePwdLink")
	protected WebElement changePasswordLink;
 
	public ZooMainPage(){
    	PageFactory.initElements(Browser.getDriverInstance(), this);
    }

	public ChangePasswordPage clickOnChangePasswordLink() {
		javaScriptClick(changePasswordLink);
		return new ChangePasswordPage();
	}

	public void clickLogout() {
		javaScriptClick(logoutLink);		
	}
	
	public ZooMainPage assertOnPage() {
		assertTrue(content.getText().contains("SIMBA Zoo"));
		return this;
	}
	
}
