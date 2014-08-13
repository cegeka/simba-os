package org.simbasecurity.dwclient.gateway.protocol;

import static org.fest.assertions.api.Assertions.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ESAPIDeserializerTest {

	@Test
	public void becauseSimbaEncodesToHTMLUsingESAPI_JacksonShouldDecodeWhenDeserializing() throws Exception {
		String esapiEncodedString = "bruce&#x40;wayneindustries.com";
		TestObject testObject = new ObjectMapper().readValue("{"
				+ "\"esapiDeserializerAnnotatedString\":\"" + esapiEncodedString + "\""
				+ ","
				+ "\"nonAnnotatedString\":\"" + esapiEncodedString + "\""
				+ "}", TestObject.class);
		assertThat(testObject.getEsapiDeserializerAnnotatedString()).isEqualTo("bruce@wayneindustries.com");
		assertThat(testObject.getNonAnnotatedString()).isEqualTo("bruce&#x40;wayneindustries.com");
	}

}
