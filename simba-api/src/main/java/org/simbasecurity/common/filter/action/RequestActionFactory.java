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
package org.simbasecurity.common.filter.action;

import static org.simbasecurity.api.service.thrift.ActionType.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;

public class RequestActionFactory extends ActionFactory {

    private Map<ActionType, Class<? extends AbstractAction>> actionMap;

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public RequestActionFactory(final HttpServletRequest request, final HttpServletResponse response) {
        this.request = request;
        this.response = response;

        this.actionMap = new EnumMap<ActionType, Class<? extends AbstractAction>>(ActionType.class);

        fillActionMap(this.actionMap);
    }

    protected void fillActionMap(final Map<ActionType, Class<? extends AbstractAction>> map) {
        map.put(MAKE_COOKIE, MakeCookieAction.class);
        map.put(DELETE_COOKIE, DeleteCookieAction.class);
        map.put(REDIRECT, RedirectAction.class);
        map.put(ADD_PARAMETER_TO_TARGET, AddParameterToTargetAction.class);
    }

    @Override
    public List<Action> create(final ActionDescriptor actionDescriptor) {
        EnumSet<ActionType> orderedActionTypes = EnumSet.noneOf(ActionType.class);
        orderedActionTypes.addAll(actionDescriptor.getActionTypes());

        final List<Action> actions = new ArrayList<Action>(4);
        for (final ActionType actionType : orderedActionTypes) {
            actions.add(createInstance(this.actionMap.get(actionType), actionDescriptor));
        }
        return actions;
    }

    protected AbstractAction createInstance(final Class<? extends AbstractAction> actionClazz, final ActionDescriptor actionDescriptor) {
        try {
            final AbstractAction action = actionClazz.getDeclaredConstructor(ActionDescriptor.class).newInstance(actionDescriptor);
            action.setRequest(this.request);
            action.setResponse(this.response);
            return action;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
