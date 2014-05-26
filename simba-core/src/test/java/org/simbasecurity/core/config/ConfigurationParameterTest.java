package org.simbasecurity.core.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationParameterTest {

    @Test
    public void givenAFirstNameParameter_WhenConvertingToType_thenConvertToInteger() throws Exception {
        assertEquals(5, ConfigurationParameter.FIRSTNAME_MAX_LENGTH.convertToType("5"));
    }

    @Test
    public void givenAnEmtpyStringFirstNameParameter_WhenConvertingToType_ThenConvertDefaultValueToInteger() throws Exception {
        assertEquals(20, ConfigurationParameter.FIRSTNAME_MAX_LENGTH.convertToType(""));
    }
}
