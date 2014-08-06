package org.simbasecurity.dwclient.gateway.protocol;

import org.simbasecurity.dwclient.gateway.protocol.ESAPIDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

class TestObject {

	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String esapiDeserializerAnnotatedString;

	private String nonAnnotatedString;

	private TestObject() {
	}

	String getEsapiDeserializerAnnotatedString() {
		return esapiDeserializerAnnotatedString;
	}

	void setEsapiDeserializerAnnotatedString(String aString) {
		this.esapiDeserializerAnnotatedString = aString;
	}

	public String getNonAnnotatedString() {
		return nonAnnotatedString;
	}

	public void setNonAnnotatedString(String anotherString) {
		this.nonAnnotatedString = anotherString;
	}
}
