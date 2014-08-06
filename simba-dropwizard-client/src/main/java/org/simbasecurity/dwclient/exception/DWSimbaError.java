package org.simbasecurity.dwclient.exception;

public enum DWSimbaError {
	GENERIC_ERROR("0", "One or more errors were found."),
	ILLEGAL_ARGUMENT("1", "An illegal argument was given.");

	private String code;
	private String desc;

	private DWSimbaError(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return desc;
	}

	@Override
	public String toString() {
		return String.format("DWS[%s]-[\"%s\"]", getCode(), getDescription());
	}

	public static String applicationErrorHeader() {
		return "DWSimbaError";
	}
}
