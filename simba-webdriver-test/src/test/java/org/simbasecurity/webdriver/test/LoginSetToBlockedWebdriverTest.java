package org.simbasecurity.webdriver.test;

import static org.simbasecurity.webdriver.framework.Browser.goToPage;

import org.simbasecurity.webdriver.framework.WebdriverTestCase;
import org.simbasecurity.webdriver.page.LoginPage;

public class LoginSetToBlockedWebdriverTest extends WebdriverTestCase {

	private static final String PWD = "Simba3D";
	private static final String JOHN = "johns";

	public void testUserIsBlockedAfterSixAttempts() {
		goToPage(getSimbaZooURL());
		
		//first attempt
		LoginPage loginPage = new LoginPage().typeUsername(JOHN).typePassword("password").clickSignIn();
		loginPage.assertErrorOnLoginPage();
		
		//second attempt
		loginPage = loginPage.typeUsername(JOHN).typePassword("df").clickSignIn();
		loginPage.assertErrorOnLoginPage();
		
		//third attempt
		loginPage = loginPage.typeUsername(JOHN).typePassword("df").clickSignIn();
		loginPage.assertErrorOnLoginPage();
		
		//fourth attempt
		loginPage = loginPage.typeUsername(JOHN).typePassword("password").clickSignIn();
		loginPage.assertErrorOnLoginPage();
		
		//fifth attempt
		loginPage = loginPage.typeUsername(JOHN).typePassword("password1").clickSignIn();
		loginPage.assertErrorOnLoginPage();
		
		//sixth attempt = BLOCKED
		loginPage = loginPage.typeUsername(JOHN).typePassword(PWD).clickSignIn();
		loginPage.assertErrorAccountBlocked();
	}
	
}