/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.simbasecurity.common.config;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.System.getProperties;
import static java.lang.System.setProperty;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_WEB_URL;

public final class SystemConfigurationTest {

    private static final String SERVICE_URL_SYSPROP_VALUE = "service.url.sysprop.value";
    private static final String WEB_URL_SYSPROP_VALUE = "web.url.sysprop.value";

    private static final String SERVICE_URL_SERVLETCONTEXT_VALUE = "service.url.servletcontext.value";
    private static final String SERVICE_URL_FILTERCONFIG_VALUE = "service.url.filterconfig.value";
    private static final String WEB_URL_FILTERCONFIG_VALUE = "web.url.filterconfig.value";

    @Before
    public void setUp() {
        getProperties().remove(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL);
        getProperties().remove(SYS_PROP_SIMBA_WEB_URL);
    }

    @Test
    public void serviceURLNullIfNotSet() {
        assertNull(SystemConfiguration.getSimbaServiceURL());
    }

    @Test
    public void webURLFallsBackOnServiceURLIfNotSet() {
        setProperty(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SYSPROP_VALUE);

        assertEquals(SERVICE_URL_SYSPROP_VALUE, SystemConfiguration.getSimbaWebURL());
    }

    @Test
    public void noFallbackForWebURLIfSet() {
        setProperty(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SYSPROP_VALUE);
        setProperty(SYS_PROP_SIMBA_WEB_URL, WEB_URL_SYSPROP_VALUE);

        assertEquals(WEB_URL_SYSPROP_VALUE, SystemConfiguration.getSimbaWebURL());
    }

    @Test
    public void configurationThroughFilterConfig() {
        final ServletContext servletContextMock = setupServletContextMock(Collections.<String, String>emptyMap());
        final FilterConfig filterConfigMock = setupFilterConfigMock(servletContextMock, Collections.singletonMap(
                SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_FILTERCONFIG_VALUE));

        assertEquals(SERVICE_URL_FILTERCONFIG_VALUE, SystemConfiguration.getSimbaServiceURL(filterConfigMock));
    }

    @Test
    public void configurationThroughServletContext() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SERVLETCONTEXT_VALUE));

        assertEquals(SERVICE_URL_SERVLETCONTEXT_VALUE, SystemConfiguration.getSimbaServiceURL(servletContextMock));
    }

    @Test
    public void configurationThroughServletContextWithLocalhost() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, "{localhost}"));

        assertNotSame("{localhost}", SystemConfiguration.getSimbaServiceURL(servletContextMock));
    }

    @Test
    public void filterConfigPrecedesServletContextConfiguration() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SERVLETCONTEXT_VALUE));
        final FilterConfig filterConfigMock = setupFilterConfigMock(servletContextMock, Collections.singletonMap(
                SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_FILTERCONFIG_VALUE));

        assertEquals(SERVICE_URL_FILTERCONFIG_VALUE, SystemConfiguration.getSimbaServiceURL(filterConfigMock));
    }

    @Test
    public void sysPropPrecedesAllConfiguration() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SERVLETCONTEXT_VALUE));
        final FilterConfig filterConfigMock = setupFilterConfigMock(servletContextMock, Collections.singletonMap(
                SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_FILTERCONFIG_VALUE));
        setProperty(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SYSPROP_VALUE);

        assertEquals(SERVICE_URL_SYSPROP_VALUE, SystemConfiguration.getSimbaServiceURL(filterConfigMock));
    }

    @Test
    public void getSimbaWebURLWithServletContextReturnsSimbaWebUrl() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, WEB_URL_FILTERCONFIG_VALUE));

        final String actual = SystemConfiguration.getSimbaWebURL(servletContextMock);
        assertEquals(WEB_URL_FILTERCONFIG_VALUE, actual);
    }

    @Test
    public void getSimbaWebURLWithFilterConfigReturnsSimbaWebUrl() {
        final ServletContext servletContextMock = setupServletContextMock(
                Collections.singletonMap(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_SERVLETCONTEXT_VALUE));
        final FilterConfig filterConfigMock = setupFilterConfigMock(servletContextMock, Collections.singletonMap(
                SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, WEB_URL_FILTERCONFIG_VALUE));

        final String actual = SystemConfiguration.getSimbaWebURL(filterConfigMock);
        assertEquals(WEB_URL_FILTERCONFIG_VALUE, actual);
    }

    @Test
    public void getSimbaServiceURLReturnsNullWithNullFilterConfig() {
        final FilterConfig filterConfig = null;
        final String actual = SystemConfiguration.getSimbaServiceURL(filterConfig);
        assertNull(actual);
    }

    @Test
    public void getSimbaServiceURLReturnsNullWithNullServletContext() {
        final ServletContext servletContext = null;
        final String actual = SystemConfiguration.getSimbaServiceURL(servletContext);
        assertNull(actual);
    }


    private ServletContext setupServletContextMock(Map<String, String> mockValues) {
        final ServletContext servletContextMock = mock(ServletContext.class);

        when(servletContextMock.getInitParameterNames()).thenReturn(Collections.enumeration(mockValues.keySet()));
        for (final Entry<String, String> entry : mockValues.entrySet()) {
            when(servletContextMock.getInitParameter(entry.getKey())).thenReturn(entry.getValue());
        }
        return servletContextMock;
    }

    private FilterConfig setupFilterConfigMock(ServletContext servletContextMock, Map<String, String> mockValues) {
        final FilterConfig filterConfigMock = mock(FilterConfig.class);

        when(filterConfigMock.getServletContext()).thenReturn(servletContextMock);
        when(filterConfigMock.getInitParameterNames()).thenReturn(Collections.enumeration(mockValues.keySet()));
        for (final Entry<String, String> entry : mockValues.entrySet()) {
            when(filterConfigMock.getInitParameter(entry.getKey())).thenReturn(entry.getValue());
        }
        return filterConfigMock;
    }
}
