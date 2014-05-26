package org.simbasecurity.webdriver.page;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.simbasecurity.webdriver.framework.Browser;

import static org.junit.Assert.assertEquals;

public class LoginPage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "signIn")
    private WebElement signInButton;
    
    @FindBy(id = "errorMessage")
    private WebElement errorMessage;
    
    
    public LoginPage(){
    	PageFactory.initElements(Browser.getDriverInstance(), this);
    }
    
    public LoginPage typeUsername(String username) {
        this.usernameField.clear();
    	this.usernameField.sendKeys(username);
        return this;
    }
    
    public LoginPage typePassword(String password) {
        this.passwordField.clear();
    	this.passwordField.sendKeys(password);
        return this;
    }
    
    public LoginPage clickSignIn() {
        signInButton.submit();
        return this;
    }
    
	public void assertErrorOnLoginPage() {
		assertEquals("Invalid username and/or password.", errorMessage.getText());
	}

	public void assertErrorDirectLogin() {		
		assertEquals("Direct login not allowed for user.",errorMessage.getText());
		
	}
	
	public void assertErrorAccountBlocked(){
		assertEquals("User account is blocked. Please contact your administrator.",errorMessage.getText());
	}

	public void assertErrorUsernameEmpty() {
		assertEquals("Please specify a valid username.", errorMessage.getText());
	}

	public void assertErrorPasswordEmpty() {
		assertEquals("Please specify a valid password.", errorMessage.getText());
	}   
}