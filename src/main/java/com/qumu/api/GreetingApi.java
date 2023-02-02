package com.qumu.api;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.ServerAuthentication;
import io.micronaut.security.rules.SecurityRule;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GreetingApi {

    @Get(uri = "/greeting", produces = MediaType.TEXT_PLAIN)
    public String getGreeting(Principal principal) {

        // The JWT provided to access this endpoint will optionally have a claim named `name`
        // Here we cast the Principal object to it's specific type so we can access the claims in the JWT
        ServerAuthentication sa = (ServerAuthentication) principal;
        String jwtName = (String) sa.getAttributes().get("name");

        String userName = "World";

        if (StringUtils.isNotEmpty(jwtName)) {
            userName = jwtName;
        }

        return "Hello " + userName + "!";
    }

    @Get(uri = "/greetingForKing", produces = MediaType.TEXT_PLAIN)
    @RolesAllowed("king")
    public String getGreetingForKing() {
        return "Hello Your Majesty!";
    }
}
