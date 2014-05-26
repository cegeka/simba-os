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
package org.simbasecurity.client.filter.action;

import static org.simbasecurity.api.service.thrift.ActionType.*;

import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.common.filter.action.AbstractAction;
import org.simbasecurity.common.filter.action.RequestActionFactory;

public class FilterActionFactory extends RequestActionFactory {

    private final FilterChain filterChain;

    public FilterActionFactory(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        super(request, response);
        this.filterChain = filterChain;
    }


    @Override
    protected void fillActionMap(Map<ActionType, Class<? extends AbstractAction>> map) {
        super.fillActionMap(map);
        map.put(DO_FILTER_AND_SET_PRINCIPAL, DoFilterAndSetPrincipalAction.class);
    }


    @Override
    protected AbstractAction createInstance(Class<? extends AbstractAction> actionClazz, ActionDescriptor actionDescriptor) {
        AbstractAction action = super.createInstance(actionClazz, actionDescriptor);
        action.setFilterChain(filterChain);
        return action;
    }
}
