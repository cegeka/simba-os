package org.simbasecurity.webdriver.test;

import static org.simbasecurity.webdriver.framework.Browser.goToPage;

import org.simbasecurity.webdriver.framework.WebdriverTestCase;
import org.simbasecurity.webdriver.page.LoginPage;

public class LoginFailsWebdriverTest extends WebdriverTestCase {

	public void testInvalidCredentials() {

		goToPage(getSimbaZooURL());
		
		checkUsernameTooShort();
		checkUsernameEmpty();
		checkPasswordEmpty();
	}

	private void checkUsernameTooShort() {
		new LoginPage().typeUsername("aa").typePassword("p").clickSignIn().assertErrorOnLoginPage();
	}
	
	private void checkUsernameEmpty() {
		new LoginPage().typeUsername("").typePassword("p").clickSignIn().assertErrorUsernameEmpty();
	}
	
	private void checkPasswordEmpty() {
		new LoginPage().typeUsername("someuser").typePassword("").clickSignIn().assertErrorPasswordEmpty();
	}
	
}
