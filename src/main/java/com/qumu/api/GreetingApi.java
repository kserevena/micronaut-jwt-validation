package com.qumu.api;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.annotation.security.RolesAllowed;

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GreetingApi {

    @Get(uri = "/greeting", produces = MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello World!";
    }

    @Get(uri = "/greetingForKing", produces = MediaType.TEXT_PLAIN)
    @RolesAllowed("king")
    public String getGreetingForKing() {
        return "Hello Your Majesty!";
    }
}
