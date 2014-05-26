package org.simbasecurity.core.domain;

import org.simbasecurity.api.service.thrift.SSOToken;

public interface SSOTokenMapping extends Identifiable {
    /**
     * @return the generated token.
     */
    String getToken();

    /**
     * @return the mapped session token
     */
    SSOToken getSSOToken();

    /**
     * @return the creation time stamp for the mapping
     */
    long getCreationTime();

    /**
     * @return <tt>true</tt> if the mapping is expired; <tt>false</tt> otherwise
     */
    boolean isExpired();

}
