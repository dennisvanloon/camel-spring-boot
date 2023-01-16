package com.dvl.prometheus.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuoteProducerRoute extends RouteBuilder {

    private final String routeId = "quote-producer-route";

    private final String from = "cron:simpleCron?schedule=0/15 * * * * *";

    private final String to = "log:quoteLogger";

    private final Boolean autoStartup = true;

    @Override
    public void configure() {
            from(from)
                .routeId(routeId)
                .autoStartup(autoStartup)
                .to("micrometer:timer:com.dvl.prometheus.simple.timer?action=start")
                .pollEnrich("https://official-joke-api.appspot.com/random_joke")
                .to("micrometer:timer:com.dvl.prometheus.simple.timer?action=stop")
                .to(to);
    }

}
