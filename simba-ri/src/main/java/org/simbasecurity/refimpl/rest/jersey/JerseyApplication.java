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
