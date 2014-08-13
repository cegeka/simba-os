package org.simbasecurity.dwclient.gateway.protocol;

import java.io.IOException;

import org.owasp.esapi.ESAPI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ESAPIDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return ESAPI.encoder().decodeForHTML(jp.getText());
	}

}
