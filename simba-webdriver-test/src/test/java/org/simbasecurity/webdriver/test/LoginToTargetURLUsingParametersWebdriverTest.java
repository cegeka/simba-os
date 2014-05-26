package org.simbasecurity.webdriver.test;

import static org.simbasecurity.webdriver.framework.Browser.goToPage;

import org.simbasecurity.webdriver.framework.WebdriverTestCase;
import org.simbasecurity.webdriver.page.LoginPage;
import org.simbasecurity.webdriver.page.ZooMainPage;

public class LoginToTargetURLUsingParametersWebdriverTest extends WebdriverTestCase {

	private static final String PWD = "Simba3D";
	private static final String ADMIN = "admin";

	public void testLoginViaRequestParams() {
		
		checkLoginFailed_wrongUser();
		checkLoginFailed_wrongPassword();
		
		checkLoginSucceeds();
	}

	private void checkLoginSucceeds() {
		goToPage(getTargetURL(ADMIN,PWD));
		new ZooMainPage()
			.assertOnPage()
			.clickLogout();
	}

	private void checkLoginFailed_wrongUser() {
		goToPage(getTargetURL("blablabla","password"));
		new LoginPage().assertErrorOnLoginPage();
	}
	
	private void checkLoginFailed_wrongPassword() {
		goToPage(getTargetURL(ADMIN,"password"));
		new LoginPage().assertErrorOnLoginPage();
	}
	
	private String getTargetURL(String user, String pwd) {
		return getSimbaZooURL() + "?SimbaAction=SimbaLoginAction&username=" + user + "&password="+ pwd;
	}
	
}
