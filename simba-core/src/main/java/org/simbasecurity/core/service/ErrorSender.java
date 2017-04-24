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

package org.simbasecurity.core.service;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorSender {

	public static int UNABLE_TO_CHANGE_PASSWORD_ERROR_CODE = 444;
	public static int UNABLE_TO_RESET_PASSWORD_ERROR_CODE = 445;
	public static int NO_USER_FOUND_ERROR_CODE = 455;

	public static void sendUnauthorizedError(HttpServletResponse response) {
		sendError(SC_FORBIDDEN, response, "Unauthorized");
	}

	public static void sendError(int errorCode, HttpServletResponse response, String message) {
		try {
			response.sendError(errorCode, message);
		} catch (IOException e) {
			throw new RuntimeException(e.getCause());
		}
	}

}
