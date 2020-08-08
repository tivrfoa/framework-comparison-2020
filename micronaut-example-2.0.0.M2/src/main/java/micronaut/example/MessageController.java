package micronaut.example;


import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import javax.validation.constraints.NotBlank;

@Controller("/")
public class MessageController {
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

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Get(value = "/hello/{name}", produces = MediaType.TEXT_PLAIN)
    String hello(@NotBlank String name) {
        return messageService.sayHello(name);
    }

    @Get(value = "/sleep", produces = MediaType.TEXT_PLAIN)
    String sleep() {
		try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
        return messageService.sayHello("I just wake up.");
    }

    @Get(value = "/split/{name}", produces = MediaType.TEXT_PLAIN)
    String split(@NotBlank String name) {
		if (sleep_control++ % 5 == 0)
			try { Thread.sleep(SLEEP_TIME); } catch (Exception e) {}
        return messageService.sayHello(name);
    }
}
