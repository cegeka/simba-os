package org.simbasecurity.refimpl.rest.jersey;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/service")
public class JerseyService {

    @GET
    @Path("/hello")
    public Response hello(@Context SecurityContext securityContext) {

        String result = "Hello Jersey!!!";

        return Response.status(200).entity(result).build();
    }
}
