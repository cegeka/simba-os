package org.simbasecurity.refimpl.rest.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import javax.ws.rs.core.MediaType;

public class JerseyExampleClient {

    public static void main(String[] args) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        try {
            WebResource resource = client.resource("http://localhost:8080/simba/jersey/service/").path("hello");

            client.addFilter(new HTTPBasicAuthFilter("admin", "Simba3D"));

            String result = resource.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
            System.out.println(result);
        } finally {
            client.destroy();
        }
    }
}
