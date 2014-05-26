package org.simbasecurity.core.domain.repository;

import org.simbasecurity.core.domain.SSOTokenMapping;

public interface SSOTokenMappingRepository extends AbstractRepository<SSOTokenMapping> {

    SSOTokenMapping findByToken(String tokenKey);

    void remove(String token);

}
