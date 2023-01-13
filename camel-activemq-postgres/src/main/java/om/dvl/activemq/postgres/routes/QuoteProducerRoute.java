package om.dvl.activemq.postgres.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuoteProducerRoute extends RouteBuilder {

    private final String routeId = "quote-producer-route";

    private final String from = "cron:simpleCron?schedule=0/10 * * * * *";

    private final String to = "activemq:quotes";

    private final Boolean autoStartup = true;

    @Override
    public void configure() {
            from(from)
                .routeId(routeId)
                .autoStartup(autoStartup)
                .pollEnrich("https://official-joke-api.appspot.com/random_joke")
                .to(to);
    }

}
