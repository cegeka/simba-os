package org.simbasecurity.webdriver.test;

import static org.simbasecurity.webdriver.framework.Browser.goToPage;

import org.simbasecurity.webdriver.framework.WebdriverTestCase;
import org.simbasecurity.webdriver.page.LoginPage;
import org.simbasecurity.webdriver.page.ZooMainPage;

public class LoginToTargetURLUsingLoginScreenWebdriverTest extends WebdriverTestCase {

	private static final String PWD = "Simba3D";
	private static final String ADMIN = "admin";

	public void testUsernameWrong() {
		goToPage(getSimbaZooURL());
		
		checkErrorWrongUserName();
		checkErrorWrongPassword();
		
		checkLoginSuccess();
	}

	private void checkErrorWrongUserName() {
		new LoginPage().typeUsername("blablabla").typePassword("password").clickSignIn().assertErrorOnLoginPage();
	}
	
	private void checkErrorWrongPassword() {
		new LoginPage().typeUsername(ADMIN).typePassword("password").clickSignIn().assertErrorOnLoginPage();
	}
	
	private void checkLoginSuccess() {
		new LoginPage().typeUsername(ADMIN).typePassword(PWD).clickSignIn();
		ZooMainPage zooPage = new ZooMainPage();
		zooPage.assertOnPage();
		zooPage.clickLogout();		
	}

}