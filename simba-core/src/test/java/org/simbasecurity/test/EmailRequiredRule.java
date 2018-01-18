package org.simbasecurity.test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mockito;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.locator.Locator;
import org.simbasecurity.core.locator.TestLocator;
import org.simbasecurity.core.service.config.CoreConfigurationService;

public class EmailRequiredRule extends TestWatcher {

    private boolean emailIsRequired;
    private CoreConfigurationService coreConfigurationService;

    private EmailRequiredRule(boolean emailIsRequired) {
        this.emailIsRequired = emailIsRequired;
    }

    public static EmailRequiredRule emailRequired() {
        return new EmailRequiredRule(true);
    }

    public static EmailRequiredRule emailNotRequired() {
        return new EmailRequiredRule(true);
    }

    public void emailShouldBeRequired() {
        Mockito.when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
    }

    public void emailShouldNotBeRequired() {
        Mockito.when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
    }

    @Override
    protected void starting(Description description) {
        Locator locatorMock = TestLocator.createLocatorMock();
        coreConfigurationService = TestLocator.implantMock(locatorMock, CoreConfigurationService.class);
        Mockito.when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(emailIsRequired);
    }

    @Override
    protected void finished(Description description) {
        TestLocator.reset();
    }
}
