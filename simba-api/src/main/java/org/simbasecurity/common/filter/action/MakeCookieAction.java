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

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.common.request.RequestConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

public final class MakeCookieAction extends AbstractAction {

    private static boolean ENABLE_SECURE_COOKIES = false;

    MakeCookieAction(final ActionDescriptor actionDescriptor) {
        super(actionDescriptor);
    }

    @Override
    public void execute() throws ServletException, IOException {
        final SSOToken token = getActionDescriptor().getSsoToken();
        assertNotNull(token, "SSOToken should be present");

        Cookie cookie = new Cookie(RequestConstants.SIMBA_SSO_TOKEN, token.getToken());
        cookie.setHttpOnly(true);
        if(ENABLE_SECURE_COOKIES) {
            cookie.setSecure(true);
        }
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    @Override
    public String toString() {
        return "Making SSO cookie with value: " + getActionDescriptor().getSsoToken();
    }

    public static void setSecureCookiesEnabled(boolean value) {
        ENABLE_SECURE_COOKIES = value;
    }

}
