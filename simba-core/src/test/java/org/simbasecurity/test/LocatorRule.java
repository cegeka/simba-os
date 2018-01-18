package org.simbasecurity.test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mockito;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.locator.Locator;
import org.simbasecurity.core.locator.TestLocator;
import org.simbasecurity.core.service.config.CoreConfigurationService;

public class LocatorRule extends TestWatcher {
    private Locator locator;
    private CoreConfigurationService coreConfigurationService;

    private LocatorRule() {
    }

    public static LocatorRule locator() {
        return new LocatorRule();
    }

    @Override
    protected void starting(Description description) {
        createLocator();
        implantMock(Audit.class);
        coreConfigurationService = implantMock(CoreConfigurationService.class);
        Mockito.when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
    }


    @Override
    protected void finished(Description description) {
        TestLocator.reset();
    }

    private void createLocator() {
        locator = TestLocator.createLocatorMock();
    }

    public <B> B implantMock(Class<B> type) {
        return TestLocator.implantMock(locator, type);
    }

    public <B> B implantMockLocatingByNameOnly(Class<B> type, String name) {
        return TestLocator.implantMockLocatingByNameOnly(locator, type, name);
    }

    public <B> B implant(Class<B> type, B instance) {
        return TestLocator.implant(locator, type, instance);
    }

    public CoreConfigurationService getCoreConfigurationService() {
        return coreConfigurationService;
    }
}
