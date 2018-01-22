package org.simbasecurity.core.service.thrift;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.LocatorRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED;

@RunWith(MockitoJUnitRunner.class)
public class ThriftAssemblerTest {

    @Rule
    public LocatorRule locatorRule = LocatorRule.locator();

    @Mock private CoreConfigurationService config;

    private ThriftAssembler assembler;

    @Before
    public void setUp() throws Exception {
        locatorRule.implantMock(UserValidator.class);
        config = locatorRule.getCoreConfigurationService();
        assembler = new ThriftAssembler(locatorRule.getCoreConfigurationService());
    }

    @Test
    public void assemble_User_emailAddressIsRequired_emailIsEmpty_throwsException() throws Exception {
        when(config.getValue(EMAIL_ADDRESS_REQUIRED)).thenReturn(true);

        assertThatExceptionOfType(SimbaException.class)
                .isThrownBy(() ->
                        assembler.assemble(new TUser().setEmail(null))
                );
    }

    @Test
    public void assemble_User_emailAddressIsNotRequired_emailIsEmpty_userEmailIsNull() throws Exception {
        when(config.getValue(EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        User user = assembler.assemble(new TUser().setEmail(null));

        assertThat(user.getEmail().isEmpty()).isTrue();
    }

    @Test
    public void assemble_User_emailAddressIsNotRequired_emailIsNotEmpty_emailIsNotValid_throwsException() throws Exception {
        when(config.getValue(EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        assertThatExceptionOfType(SimbaException.class)
                .isThrownBy(() ->
                        assembler.assemble(new TUser().setEmail("test"))
                );
    }

    @Test
    public void assemble_User_emailAddressIsNotRequired_emailIsNotEmpty_userEmailIsSet() throws Exception {
        when(config.getValue(EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        User user = assembler.assemble(new TUser().setEmail("test@cegeka.com"));

        assertThat(user.getEmail().toString()).isEqualTo("test@cegeka.com");
    }
}