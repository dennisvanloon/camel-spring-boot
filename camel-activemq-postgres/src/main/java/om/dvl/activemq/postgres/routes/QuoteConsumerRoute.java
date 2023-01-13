package om.dvl.activemq.postgres.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuoteConsumerRoute extends RouteBuilder {

    private final String routeId = "quote-consumer-route";

    private final String from = "activemq:quotes";

    private final String to = "log:quoteLogger";

    private final Boolean autoStartup = true;

    @Override
    public void configure() {
            from(from)
                .routeId(routeId)
                .autoStartup(autoStartup)
                .to(to);
    }

}
