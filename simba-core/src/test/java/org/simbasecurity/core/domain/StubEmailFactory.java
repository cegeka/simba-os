package org.simbasecurity.core.domain;

import org.mockito.Mockito;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StubEmailFactory extends EmailFactory {

    private CoreConfigurationService coreConfigurationService;

    public static StubEmailFactory emailNotRequired() {
        return instance(false);
    }

    public static StubEmailFactory emailRequired() {
        return instance(true);
    }

    private static StubEmailFactory instance(boolean emailRequired) {
        CoreConfigurationService coreConfigurationService = mock(CoreConfigurationService.class);
        when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(emailRequired);
        return new StubEmailFactory(coreConfigurationService);
    }

    private StubEmailFactory(CoreConfigurationService configurationService) {
        super(configurationService);

        this.coreConfigurationService = configurationService;
    }

    public void setEmailRequired(boolean emailRequired) {
        Mockito.reset(coreConfigurationService);
        when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(emailRequired);
    }

    public CoreConfigurationService configurationService() {
        return coreConfigurationService;
    }
}
