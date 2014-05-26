package org.simbasecurity.core.service.cache;

import org.springframework.transaction.annotation.Transactional;

public interface CacheService {
    void refreshCacheIfEnabled();

    void refreshCacheIfEnabled(String userName);

    boolean isCacheEnabled();

    @Transactional
    void setCacheEnabled(boolean isEnabled);
}
