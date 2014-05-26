/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.common.filter.action;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.ActionDescriptor;

public abstract class AbstractAction implements Action {
    private ActionDescriptor actionDescriptor;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected FilterChain filterChain;

    protected AbstractAction(final ActionDescriptor actionDescriptor) {
        this.actionDescriptor = actionDescriptor;
    }

    protected final ActionDescriptor getActionDescriptor() {
        return this.actionDescriptor;
    }

    public final void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    public final void setResponse(final HttpServletResponse response) {
        this.response = response;
    }

    public final void setFilterChain(final FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    static void assertNotNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
    }

    final HttpServletRequest getHttpServletRequest() {
        return this.request;
    }

    final HttpServletResponse getHttpServletResponse() {
        return this.response;
    }
}
