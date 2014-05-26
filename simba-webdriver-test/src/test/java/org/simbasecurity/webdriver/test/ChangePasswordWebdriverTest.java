package org.simbasecurity.webdriver.test;

import static org.simbasecurity.webdriver.framework.Browser.goToPage;

import org.simbasecurity.webdriver.framework.WebdriverTestCase;
import org.simbasecurity.webdriver.page.ChangePasswordPage;
import org.simbasecurity.webdriver.page.LoginPage;
import org.simbasecurity.webdriver.page.ZooMainPage;

/**
 * Important! For now this test is not restartable. In other words, it tests if a password can be changed succesfully, and after it has done so, you cannot restart the test
 * and log in with the old password. This will be fixed by release 2.1.
 *  
 * @author philipn
 */
public class ChangePasswordWebdriverTest extends WebdriverTestCase {

	private static final String CHANGE_PWD_USER = "changePwdUser1"; //user that is marked in test data to change pwd on next login
	private static final String OLD_PWD = "Simba3D";
	private static final String NEW_PWD = "Simba2D";

	//see SIMBA_PARAMETER table (PASSWORD_COMPLEXITY_RULE, PASSWORD_MIN_LENGTH, ...)
	
	private static final String NEW_PWD_TOO_SHORT = "abcde"; //min length 6
	private static final String NEW_PWD_TOO_LONG = "ffffffffffffffff"; //max length 15
	private static final String NEW_PWD_TOO_WEAK = "newWeakPwd"; //matches min and max length, but is not complex enough

	public void testChangePassword(){
		goToPage(getSimbaZooURL());
		new LoginPage().typeUsername(CHANGE_PWD_USER).typePassword(OLD_PWD).clickSignIn(); //if test fails here see the class level doc
		
		ChangePasswordPage changePasswordPage = new ChangePasswordPage();
		checkErrorIfNewPasswordAndConfirmationDontMatch(changePasswordPage);
		checkErrorIfNewPasswordNotAccordingToPasswordPolicy(changePasswordPage);
		
		checkChangePasswordSucceeds(changePasswordPage);
		checkChangePasswordSucceedsViaScreenLink();
		
	}

	private void checkErrorIfNewPasswordAndConfirmationDontMatch(ChangePasswordPage changePasswordPage) {
		changePasswordPage.typeOldPassword(OLD_PWD).typeNewPassword(NEW_PWD).typeConfirmPassword("aaa").clickChange();
		changePasswordPage.assertPasswordsDoNotMatchErrorOnPage();
	}
	
	private void checkErrorIfNewPasswordNotAccordingToPasswordPolicy(ChangePasswordPage changePasswordPage) {
		checkPasswordTooShort(changePasswordPage);
		checkPasswordTooLong(changePasswordPage);
		checkPasswordTooWeak(changePasswordPage);
	}

	private void checkPasswordTooShort(ChangePasswordPage changePasswordPage) {
		changePasswordPage.typeOldPassword(OLD_PWD).typeNewPassword(NEW_PWD_TOO_SHORT).typeConfirmPassword(NEW_PWD_TOO_SHORT).clickChange();
		changePasswordPage.assertInvalidPasswordLength();
	}
	
	private void checkPasswordTooLong(ChangePasswordPage changePasswordPage) {
		changePasswordPage.typeOldPassword(OLD_PWD).typeNewPassword(NEW_PWD_TOO_LONG).typeConfirmPassword(NEW_PWD_TOO_LONG).clickChange();
		changePasswordPage.assertInvalidPasswordLength();
	}
	
	private void checkPasswordTooWeak(ChangePasswordPage changePasswordPage) {
		changePasswordPage.typeOldPassword(OLD_PWD).typeNewPassword(NEW_PWD_TOO_WEAK).typeConfirmPassword(NEW_PWD_TOO_WEAK).clickChange();
		changePasswordPage.assertPasswordNotStrongEnough();
	}

	private void checkChangePasswordSucceeds(ChangePasswordPage changePasswordPage) {
		changePasswordPage.typeOldPassword(OLD_PWD).typeNewPassword(NEW_PWD).typeConfirmPassword(NEW_PWD).clickChange();
		new ZooMainPage().assertOnPage();
	}
	
	private void checkChangePasswordSucceedsViaScreenLink() {
		ZooMainPage zooMainPage = new ZooMainPage();
		ChangePasswordPage changePasswordPage = zooMainPage.clickOnChangePasswordLink();
		changePasswordPage.typeOldPassword(NEW_PWD).typeNewPassword(OLD_PWD).typeConfirmPassword(OLD_PWD).clickChange();
		changePasswordPage.assertPasswordChanged();
	}

}