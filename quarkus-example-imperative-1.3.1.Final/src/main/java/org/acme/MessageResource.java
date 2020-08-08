package org.acme;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class MessageResource {

	private static final int SLEEP_TIME;
	private static int sleep_control = 0;
	
	static {
		String tmp = System.getenv("SLEEP_TIME");
		if (tmp == null) {
			System.err.println("You need to set SLEEP_TIME environment variable (time to sleep in milliseconds), eg");
			System.err.println("export SLEEP_TIME=2");
			System.exit(-1);
		}
		SLEEP_TIME = Integer.parseInt(tmp);
	}

    private MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @GET
    @Path("/hello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@NotBlank @PathParam("name") String name) {
        return messageService.sayHello(name);
    }

    @GET
    @Path("/sleep")
    @Produces(MediaType.TEXT_PLAIN)
    public String sleep() {
		try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
        return messageService.sayHello("I just wake up.");
    }

	/**
	 * It does a blocking operation (sleep) each 5 executions.
	 */
    @GET
    @Path("/split/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String split(@NotBlank @PathParam("name") String name) {
		if (sleep_control++ % 5 == 0)
			try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
        return messageService.sayHello(name);
    }
}
