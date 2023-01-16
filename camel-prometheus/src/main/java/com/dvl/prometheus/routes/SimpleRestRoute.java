package com.dvl.prometheus.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class SimpleRestRoute extends RouteBuilder {

    private final Environment env;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SimpleRestRoute(Environment env) {
        this.env = env;
    }

    public void configure() {

        restConfiguration()
                .contextPath(env.getProperty("camel.servlet.mapping.contextPath", "/rest/*"))
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Spring Boot Camel API.")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api")
                .port(env.getProperty("server.port", "8080"))
                .bindingMode(RestBindingMode.json);

        rest("/hello")
                .consumes(APPLICATION_JSON_VALUE)
                .produces(APPLICATION_JSON_VALUE)
                .post("/")
                .to("direct:hello");

        from("direct:hello")
                .process(this::setResponse)
                .to("log:hellologger");
    }

    private void setResponse(Exchange exchange) {
        JsonNode payload = objectMapper.convertValue(exchange.getIn().getBody(), JsonNode.class);
        JsonNode message = payload.get("message");
        if(message != null && message.textValue().equals("error")) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
            exchange.getIn().setBody("{\"response\": \"You supplied the wrong payload\"}");
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getIn().setBody("{\"response\": \"Everything fine\"}");
        }
    }

}
