package org.simbasecurity.webdriver.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.simbasecurity.webdriver.framework.Browser;

public class ChangePasswordPage {
    
    @FindBy(id = "password")
    private WebElement oldPasswordField;
    
    @FindBy(id = "newpassword")
    private WebElement newPasswordField;
    
    @FindBy(id = "newpasswordconfirmation")
    private WebElement newPasswordFieldConfirmation;

    @FindBy(id = "submit")
    private WebElement changePasswordButton;
    
    @FindBy(id = "errorMessage")
    private WebElement errorMessage;
    
    @FindBy(id = "content")
    private WebElement content;
    
    public ChangePasswordPage(){
    	PageFactory.initElements(Browser.getDriverInstance(), this);
    }
    
    public ChangePasswordPage typeOldPassword(String oldPwd) {
        this.oldPasswordField.clear();
    	this.oldPasswordField.sendKeys(oldPwd);
        return this;
    }
    
    public ChangePasswordPage typeNewPassword(String newPwd) {
    	this.newPasswordField.clear();
    	this.newPasswordField.sendKeys(newPwd);
        return this;
    }
    
    public ChangePasswordPage typeConfirmPassword(String newPwdConfirm) {
    	this.newPasswordFieldConfirmation.clear();
    	this.newPasswordFieldConfirmation.sendKeys(newPwdConfirm);
        return this;
    }
    
    public ChangePasswordPage clickChange() {
    	changePasswordButton.submit();
        return this;
    }
    
	public ChangePasswordPage assertPasswordChanged() {
		assertTrue(content.getText().contains("password has successfully been changed"));
		return this;
	}
	
	public void assertPasswordsDoNotMatchErrorOnPage() {
		assertEquals("New password and new password confirmation must match.", errorMessage.getText());		
	}
	
	public void assertInvalidPasswordLength() {
		assertEquals("Invalid password length.", errorMessage.getText());		
	}
	
	public void assertPasswordNotStrongEnough() {
		assertEquals("Invalid password. Password contains invalid characters or is not strong enough.", errorMessage.getText());		
	}
	
}