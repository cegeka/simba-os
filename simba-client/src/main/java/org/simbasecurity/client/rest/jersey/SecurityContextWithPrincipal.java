package org.simbasecurity.client.rest.jersey;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class SecurityContextWithPrincipal implements SecurityContext {
    private final SecurityContext wrapped;
    private final Principal principal;

    public SecurityContextWithPrincipal(SecurityContext wrapped, Principal principal) {
        this.wrapped = wrapped;
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String s) {
        return wrapped.isUserInRole(s);
    }

    @Override
    public boolean isSecure() {
        return wrapped.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return wrapped.getAuthenticationScheme();
    }
}
