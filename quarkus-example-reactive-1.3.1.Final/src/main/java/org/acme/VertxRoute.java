package org.acme;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Set;


@Singleton
public class VertxRoute {

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

	@Inject
	Validator validator;

	final MessageService messageService;

	public VertxRoute(MessageService messageService) {
		this.messageService = messageService;
	}

	@Route(path = "/hello/:name", methods = HttpMethod.GET)
	void greetings(RoutingExchange ex) {
		Set<ConstraintViolation<RequestWrapper>> violations = validator.validate(new RequestWrapper(ex.getParam("name").get()));
		if( violations.size() == 0) {
			ex.ok(messageService.sayHello(ex.getParam("name").orElse("world")));
		} else {
			StringBuilder vaidationError = new StringBuilder();
			violations.stream().forEach(violation -> vaidationError.append(violation.getMessage()));
			ex.response().setStatusCode(400).end(vaidationError.toString());
		}
	}

	@Route(path = "/sleep", methods = HttpMethod.GET)
	void sleep(RoutingExchange ex) {
		try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
		ex.ok(messageService.sayHello("I just wake up."));
	}

	@Route(path = "/split/:name", methods = HttpMethod.GET)
	void split(RoutingExchange ex) {
		if (sleep_control++ % 5 == 0)
			try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
		greetings(ex);
	}

	private class RequestWrapper {
		@NotBlank
		public String name;

		public RequestWrapper(String name) {
			this.name = name;
		}
	}

	@Route(path = "/environment", methods = HttpMethod.GET, produces = "text/plain")
	void env(RoutingExchange ex){
		StringBuilder stringBuilder = new StringBuilder();

		ProcessHandle processHandle = ProcessHandle.current();

		stringBuilder.append("pid: ").append(processHandle.pid()).append("\n");
		stringBuilder.append("\n");

		stringBuilder.append("commandLine: ").append(processHandle.info().commandLine().get()).append("\n");
		stringBuilder.append("\n");

		stringBuilder.append("thread-name: ").append(Thread.currentThread().getName()).append("\n");
		stringBuilder.append("\n\n");
		stringBuilder.append("stack trace: ").append("\n\n");

		Arrays.stream(Thread.currentThread().getStackTrace()).forEach(ste -> stringBuilder.append(ste.toString()).append("\n"));

		ex.ok(stringBuilder.toString());

	}

}

