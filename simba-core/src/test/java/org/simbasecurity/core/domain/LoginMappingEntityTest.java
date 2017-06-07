package org.simbasecurity.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.simbasecurity.core.domain.LoginMappingEntity.MAX_URL_LENGTH;


public class LoginMappingEntityTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    private TestLogger logger = TestLoggerFactory.getTestLogger(LoginMappingEntity.class);

    @Test
    public void create() throws Exception {
        String ridiculouslyLargeUrl = IntStream.range(0,3000).mapToObj(i -> "0").collect(joining());

        LoginMappingEntity entity = LoginMappingEntity.create(ridiculouslyLargeUrl);

        assertThat(logger.getLoggingEvents()).containsOnly(LoggingEvent.error("SIMBA-12899: Target URL is too large for LoginMappingEntity: {}", entity));
        assertThat(entity.getTargetURL()).hasSize(MAX_URL_LENGTH);
        assertThat(entity.getToken()).isNotNull();
        assertThat(entity.getCreationTime()).isNotZero();
    }

}