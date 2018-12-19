package org.simbasecurity.client.rest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserNamePasswordTest {

    @Test
    public void happyPath() {
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDp0ZXN0Cg==");

        assertThat(userNamePassword.getUserName()).isEqualTo("test");
        assertThat(userNamePassword.getPassword()).isEqualTo("test");
    }

    @Test
    public void noAuthorizationHeader() {
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

    @Test
    public void incorrectFormat() {
        assertThatThrownBy(() -> UserNamePassword.fromBasicAuthenticationHeader("Basic dGVzdDp0ZXN0OnRlc3QK"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("The provided authorization needs to be in the form 'username:password'");
    }
}