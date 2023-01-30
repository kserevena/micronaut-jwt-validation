package com.qumu;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@MicronautTest
class ApiTest {

    @Value("${security.hs256.secret}")
    String jwtSigningSecret;

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void greetingGetEndpointRespondsToAuthorizedRequests() throws JOSEException {

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth(validJwt()), String.class);
        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isEqualTo("Hello World!");
    }

    @Test
    void greetingGetEndpointRejectsUnauthorizedRequests() {

        assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth("a bad token"), String.class)).isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
            HttpClientResponseException castException = (HttpClientResponseException) e;
            Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });
    }

    @Test
    void greetingGetEndpointRejectsExpiredJwts() {

        assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth(expiredJwt()), String.class)).isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
            HttpClientResponseException castException = (HttpClientResponseException) e;
            Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });

    }

    @Test
    void greetingForKingGetAcceptsJwtWithKingRole() throws JOSEException {

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/greetingForKing").bearerAuth(validJwtWithRoles("king")), String.class);
        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isEqualTo("Hello Your Majesty!");
    }

    @Test
    void greetingForKingGetRejectsJwtWithoutKingRole() {

        assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET("/greetingForKing").bearerAuth(validJwtWithRoles("peasant")), String.class)).isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
            HttpClientResponseException castException = (HttpClientResponseException) e;
            Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        });
    }

    /**
     * Build a valid signed JWT for use in tests using secret provided in config
     *
     * @return valid signed JWT
     */
    private String validJwt() throws JOSEException {
        return buildSignedJwt(false, null).serialize();
    }

    /**
     * @return An expired JWT with valid signature
     */
    private String expiredJwt() throws JOSEException {
        return buildSignedJwt(true, null).serialize();
    }

    /**
     * Build a valid signed JWT with a specified list of roles
     *
     * @param roles Space separated list of roles to put in JWT
     */
    private String validJwtWithRoles(String roles) throws JOSEException {
        return buildSignedJwt(false, roles).serialize();
    }

    private SignedJWT buildSignedJwt(boolean expired, String role) throws JOSEException {

        Instant now = Instant.now();
        if (expired) {
            now = now.minusSeconds(30);
        } else {
            now = now.plusSeconds(1000);
        }

        MACSigner signer = new MACSigner(jwtSigningSecret);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("testUser")
                .expirationTime(Date.from(now))
                .claim("roles", role)
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);
        return signedJWT;
    }
}
