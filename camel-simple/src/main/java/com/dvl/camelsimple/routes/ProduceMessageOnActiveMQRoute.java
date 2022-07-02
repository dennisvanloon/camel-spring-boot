package com.dvl.camelsimple.routes;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ProduceMessageOnActiveMQRoute extends RouteBuilder {

    private final String routeId = "produce-message-on-amq";

    @PropertyInject(value = "route.produce-message-on-amq.from")
    private String from;

    @PropertyInject(value = "route.produce-message-on-amq.to")
    private String to;

    @PropertyInject(value = "route.produce-message-on-amq.auto-startup", defaultValue = "true")
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
