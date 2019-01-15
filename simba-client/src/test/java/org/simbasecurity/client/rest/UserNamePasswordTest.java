package org.simbasecurity.client.rest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserNamePasswordTest {

    @Test
    public void happyPath() {
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDp0ZXN0");

        assertThat(userNamePassword.getUserName()).isEqualTo("test");
        assertThat(userNamePassword.getPassword()).isEqualTo("test");
    }

    @Test
    public void aPasswordCanHaveAColon() {
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDp0ZXN0OnRlc3Q=");

        assertThat(userNamePassword.getUserName()).isEqualTo("test");
        assertThat(userNamePassword.getPassword()).isEqualTo("test:test");
    }

    @Test
    public void aPasswordCanStartAndEndWithASpace() {
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDogdGVzdCA=");

        assertThat(userNamePassword.getPassword()).isEqualTo(" test ");
    }

    @Test
    public void aUsernameCanStartAndEndWithASpace() {
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader("Basic IHRlc3QgOnRlc3Q=");

        assertThat(userNamePassword.getUserName()).isEqualTo(" test ");
    }

    @Test
    public void noAuthorizationHeader() {
        //noinspection ConstantConditions
        assertThatThrownBy(() -> UserNamePassword.fromBasicAuthenticationHeader(null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("An authorization header is required");
    }

    @Test
    public void incorrectAuthorizationHeader() {
        assertThatThrownBy(() -> UserNamePassword.fromBasicAuthenticationHeader("dGVzdDp0ZXN0Cg=="))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("'dGVzdDp0ZXN0Cg==' is not a correct basic authentication header");
    }

    @Test
    public void incorrectEncodedString() {
        assertThatThrownBy(() -> UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDp0ZXN0Cg="))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("'dGVzdDp0ZXN0Cg=' is not a valid base64 encoded string");
    }
}