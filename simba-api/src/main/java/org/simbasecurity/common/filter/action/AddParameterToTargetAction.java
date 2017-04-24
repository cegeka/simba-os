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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;

import org.simbasecurity.api.service.thrift.ActionDescriptor;

public final class AddParameterToTargetAction extends AbstractAction {

    AddParameterToTargetAction(final ActionDescriptor actionDescriptor) {
        super(actionDescriptor);
    }

    @Override
    public void execute() throws ServletException, IOException {
        final String targetURL = getActionDescriptor().getRedirectURL();
        if (targetURL == null) {
            throw new IllegalStateException("TargetURL must be set!");
        }
        final Map<String, String> map = getActionDescriptor().getParameterMap();

        final StringBuilder newTargetURL = new StringBuilder(targetURL);

        for (final Entry<String, String> entry : map.entrySet()) {
            newTargetURL.append(newTargetURL.indexOf("?") >= 0 ? '&' : '?');
            newTargetURL.append(entry.getKey());
            newTargetURL.append('=');
            newTargetURL.append(entry.getValue());
        }

        getActionDescriptor().setRedirectURL(newTargetURL.toString());
    }

    @Override
    public String toString() {
        return "Adding parameters " + getActionDescriptor().getParameterMap() + " to URL";
    }

}
