package org.simbasecurity.core.saml;

public interface SAMLResponseHandler {
    // isValid() function should be called to make basic security checks to responses.
    boolean isValid(String... requestId);

    String getInResponseTo();

    String getAttribute(String name);
}
