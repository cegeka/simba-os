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

package org.simbasecurity.client.filter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyEnumeration;
import static java.util.Collections.enumeration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.simbasecurity.common.config.SystemConfiguration.EXCLUDED_URL;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL;

public class SimbaFilterTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SimbaFilter filter = new SimbaFilter();

    @Mock private FilterConfig filterConfig;
    @Mock private ServletContext context;
    @Mock private HttpServletRequest request;

    @Before
    public void setup() {
        when(request.getContextPath()).thenReturn("/ELoket");
    }

    @Test
    public void testWithRootPath() throws Exception {
        setupExcludedUrl("/eid");

        when(request.getRequestURI()).thenReturn("/ELoket/");

        assertThat(filter.isUrlExcluded(request)).isFalse();
    }

    @Test
    public void testWithNotMatchingUrl() throws Exception {
        setupExcludedUrl("/eid");

        when(request.getRequestURI()).thenReturn("/ELoket/test/eid");

        assertThat(filter.isUrlExcluded(request)).isFalse();
    }

    @Test
    public void testWithMatchingUrl() throws Exception {
        setupExcludedUrl("/eid");

        when(request.getRequestURI()).thenReturn("/ELoket/eid");

        assertThat(filter.isUrlExcluded(request)).isTrue();
    }

    @Test
    public void testWithoutExcludedUrl() throws Exception {
        setupExcludedUrl(null);

        when(request.getRequestURI()).thenReturn("/ELoket/eid");

        assertThat(filter.isUrlExcluded(request)).isFalse();
    }

    private void setupExcludedUrl(String exclusionContextPath) throws ServletException {
        when(filterConfig.getServletContext()).thenReturn(context);
        when(context.getInitParameterNames()).thenReturn(emptyEnumeration());
        when(filterConfig.getInitParameterNames()).thenAnswer(invocation -> enumeration(asList(EXCLUDED_URL, SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME)));
        when(filterConfig.getInitParameter(EXCLUDED_URL)).thenReturn(exclusionContextPath);
        when(filterConfig.getInitParameter(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL)).thenReturn("eenprachtigeurl");
        when(filterConfig.getInitParameter(SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME)).thenReturn("authenticationChain");
        filter.init(filterConfig);
    }
}