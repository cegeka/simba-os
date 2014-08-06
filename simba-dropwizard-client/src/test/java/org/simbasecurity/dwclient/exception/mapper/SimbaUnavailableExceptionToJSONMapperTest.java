package org.simbasecurity.dwclient.exception.mapper;

import static org.fest.assertions.api.Assertions.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;
import org.simbasecurity.dwclient.dropwizard.representation.DWSimbaErrorR;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.exception.mapper.SimbaUnavailableExceptionToJSONMapper;

public class SimbaUnavailableExceptionToJSONMapperTest {

	@Test
	public void toResponse_ReturnsJSONValidationError() throws Exception {
		SimbaUnavailableExceptionToJSONMapper mapperUnderTest = new SimbaUnavailableExceptionToJSONMapper();

		SimbaUnavailableException simbaUnavailableException = new SimbaUnavailableException();

		DWSimbaErrorR expectedEntity = new DWSimbaErrorR(HttpStatus.INTERNAL_SERVER_ERROR.getDescription(),
				SimbaUnavailableExceptionToJSONMapper.SIMBA_ERROR_MESSAGE, null);
		MediaType expectedMediaType = MediaType.APPLICATION_JSON_TYPE;
		int expectedStatus = 500;

		Response actual = mapperUnderTest.toResponse(simbaUnavailableException);

		assertThat(actual.getStatus()).isEqualTo(expectedStatus);
		assertThat((MediaType) actual.getMetadata().get("Content-Type").get(0)).isEqualTo(expectedMediaType);
		assertThat(actual.getEntity()).isEqualsToByComparingFields(expectedEntity);
	}
}
