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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.common.request.RequestUtil;

public class DeleteCookieAction extends AbstractAction {

	DeleteCookieAction(ActionDescriptor actionDescriptor) {
		super(actionDescriptor);
	}

	public void execute() throws ServletException, IOException {
		Cookie cookie = RequestUtil.getSSOCookieThatShouldBePresent(request);
		deleteCookie(cookie);
	}

	private void deleteCookie(Cookie ssoCookie) {
		ssoCookie.setMaxAge(0);
		ssoCookie.setValue("");
		ssoCookie.setPath("/");
		response.addCookie(ssoCookie);
	}

	@Override
	public String toString() {
		return "Deleting SSO cookie";
	}

}
