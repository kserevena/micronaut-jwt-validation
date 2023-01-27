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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class ApiTest {

    @Value("${security.hs256.secret}")
    String jwtSigningSecret;

    SignedJWT signedJWT;
    @Inject
    @Client("/")
    HttpClient client;

    /**
     * Build a valid signed JWT for use in tests using secret provided in config
     */
    @BeforeEach
    void setup() throws JOSEException {
        MACSigner signer = new MACSigner(jwtSigningSecret);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("testUser")
                .build();
        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);
    }

    @Test
    void greetingGetEndpointRespondsToAuthorizedRequests() {

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth(signedJWT.serialize()), String.class);
        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isEqualTo("Hello World!");
    }

    @Test
    void greetingGetEndpointRejectsUnauthorizedRequests() {

        Assertions.assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth("a bad token"), String.class)).isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
            HttpClientResponseException castException = (HttpClientResponseException) e;
            Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });
    }
}
