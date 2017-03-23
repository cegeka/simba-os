package org.simbasecurity.core.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimbaConfigurationParameterTest {

    @Test
    public void givenAFirstNameParameter_WhenConvertingToType_thenConvertToInteger() throws Exception {
        int result = SimbaConfigurationParameter.FIRSTNAME_MAX_LENGTH.convertToType("5");
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void givenAnEmtpyStringFirstNameParameter_WhenConvertingToType_ThenConvertDefaultValueToInteger() throws Exception {
        int result = SimbaConfigurationParameter.FIRSTNAME_MAX_LENGTH.convertToType("");
        assertThat(result).isEqualTo(20);
    }
}
