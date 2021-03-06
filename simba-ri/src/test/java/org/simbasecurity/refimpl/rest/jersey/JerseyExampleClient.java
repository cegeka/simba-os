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

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

public class JerseyExampleClient {

    private static final Logger LOGGER = Logger.getLogger(JerseyExampleClient.class.getName());

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        client.register(HttpAuthenticationFeature.basic("admin", "Simba3D"));
        client.register(new LoggingFeature(LOGGER));
        WebTarget target = client.target("http://localhost:8080/simba/jersey/service/").path("hello");
        try {
            String result = target.request(MediaType.TEXT_PLAIN_TYPE)
                                  .get(String.class);
            System.out.println(result);
        } finally {
            client.close();
        }
    }
}
