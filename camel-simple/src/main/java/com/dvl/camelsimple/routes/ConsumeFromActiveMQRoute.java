package com.dvl.camelsimple.routes;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.dvl.camelsimple.routes.Constants.ENDPOINT_ACTIVE_MQ;
import static com.dvl.camelsimple.routes.Constants.ENDPOINT_TO;

@Component
public class ConsumeFromActiveMQRoute extends RouteBuilder {

    private final String routeId = "consume-message-from-amq";

    @PropertyInject(value = ENDPOINT_ACTIVE_MQ)
    private String from;

    @PropertyInject(value = ENDPOINT_TO)
    private String to;

    @PropertyInject(value = "route.consume-message-from-amq.auto-startup", defaultValue = "false")
    private Boolean autoStartup;

    @Override
    public void configure() {
            from(from)
                .routeId(routeId)
                .autoStartup(autoStartup)
                .log("Consuming message from the queue: ${body}")
                .to(to);
    }

}
