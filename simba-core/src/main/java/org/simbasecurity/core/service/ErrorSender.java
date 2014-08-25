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
