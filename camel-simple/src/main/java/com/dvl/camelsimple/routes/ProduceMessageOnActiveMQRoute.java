package com.dvl.camelsimple.routes;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.dvl.camelsimple.routes.Constants.ENDPOINT_ACTIVE_MQ;
import static com.dvl.camelsimple.routes.Constants.ENDPOINT_FROM;

@Component
public class ProduceMessageOnActiveMQRoute extends RouteBuilder {

    private final String routeId = "produce-message-on-amq";

    @PropertyInject(value = ENDPOINT_FROM)
    private String from;

    @PropertyInject(value = ENDPOINT_ACTIVE_MQ)
    private String to;

    @PropertyInject(value = "route.produce-message-on-amq.auto-startup", defaultValue = "false")
    private Boolean autoStartup;

    @Override
    public void configure() {
            from(from)
                .routeId(routeId)
                .autoStartup(autoStartup)
                .setBody(simple("Current time is ${header.firedTime}"))
                .log("Putting message on the queue: ${body}")
                .to(to);
    }

}
