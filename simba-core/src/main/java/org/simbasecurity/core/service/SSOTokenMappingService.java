package org.simbasecurity.core.service;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.SSOTokenMapping;

public interface SSOTokenMappingService {
    /**
     * Create a temporary token linked to an existing session SSO token so the filter
     * can use this temporary token to fetch the actual SSOToken for which to create a
     * HTTP Cookie.
     *
     * @param token the SSOToken for which to create a mapping
     * @return the unique short-lived token to use
     */
    SSOTokenMapping createMapping(SSOToken token);

    SSOToken getSSOToken(String ssoTokenKey);

    void destroyMapping(String ssoTokenKey);

    void purgeExpiredMappings();
}
