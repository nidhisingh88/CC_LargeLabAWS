package main.java.nl.tud.cc.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class TestResource {
	
	/**
	 * The 'hello, world' method.
	 * 
	 * @return A simple response containing 'hello, world' in the body.
	 */
	@GET
	@Path("/world")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() {
		return simpleResponse("hello, world");
	}
	
	protected Response simpleResponse(String msg) {
		return Response.ok(msg, MediaType.TEXT_PLAIN).build();
	}

}
