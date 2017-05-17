package org.simbasecurity.core.service.manager;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.service.cache.CacheService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CacheManagerServiceTest {

    private CacheManagerService cacheManagerService;
    private CacheService cacheServiceMock;

    @Before
    public void setUp() throws Exception {
        cacheServiceMock = mock(CacheService.class);
        cacheManagerService = new CacheManagerService(cacheServiceMock);
    }

    @Test
    public void refreshCache() throws Exception {
        cacheManagerService.refreshCache();

        verify(cacheServiceMock).refreshCacheIfEnabled();
    }

    @Test
    public void enableCache() throws Exception {
        cacheManagerService.enableCache();

        verify(cacheServiceMock).setCacheEnabled(true);
    }

    @Test
    public void disableCache() throws Exception {
        cacheManagerService.disableCache();

        verify(cacheServiceMock).setCacheEnabled(false);
    }

    @Test
    public void isEnabled() throws Exception {
        when(cacheServiceMock.isCacheEnabled()).thenReturn(true);

        boolean result = cacheManagerService.isCacheEnabled();

        assertThat(result).isTrue();
    }
}