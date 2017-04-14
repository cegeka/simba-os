package org.simbasecurity.refimpl.rest.jersey;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.simbasecurity.client.rest.jersey.JerseyBasicAuthenticationFilter;
import org.springframework.web.filter.RequestContextFilter;

import java.util.logging.Logger;

public class JerseyApplication extends ResourceConfig {

    private static final Logger logger = Logger.getLogger(JerseyApplication.class.getName());

    public JerseyApplication() {
        register(RequestContextFilter.class);
        register(JerseyBasicAuthenticationFilter.class);
        register(new LoggingFeature(logger));
        register(JerseyService.class);
    }
}
