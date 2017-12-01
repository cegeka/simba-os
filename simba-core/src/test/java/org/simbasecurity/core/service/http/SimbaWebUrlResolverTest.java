package org.simbasecurity.core.service.http;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.simbasecurity.core.service.http.HttpServletRequestTestBuilder.httpServletRequest;

public class SimbaWebUrlResolverTest {

    private SimbaWebUrlResolver simbaWebUrlResolver = new SimbaWebUrlResolver();

    @Test
    public void will() throws Exception {
        HttpServletRequest httpServletRequest = httpServletRequest()
                .scheme("http")
                .serverName("localhost")
                .serverPort(8080)
                .contextPath("somePath")
                .build();

        String simbaWebURL = simbaWebUrlResolver.resolveSimbaWebURL(httpServletRequest);

        assertThat(simbaWebURL).isEqualTo("http://localhost:8080somePath");
    }
}