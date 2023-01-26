package com.qumu.api;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class GreetingApi {

    @Get(uri = "/greeting", produces = MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello World!";
    }
}
