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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "/greeting GET: Default response is successful, /greeting, Hello World!, , ",
            "/greeting GET: Name claim is used from JWT, /greeting, Hello Frank!, Frank, ",
            "/greetingForKing GET: JWT with role of King defined is authenticated, /greetingForKing, Hello Your Majesty!, , king"
    })
    void getEndpointTestsForValidJwts(String testName, String testEndpoint, String expectedBody, String userName, String role) throws JOSEException {

        String testJwt = buildSignedJwt(false, role, userName).serialize();

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET(testEndpoint).bearerAuth(testJwt), String.class);
        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isEqualTo(expectedBody);
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "/greeting GET: expired JWT returns `unauthorized` HTTP status, /greeting, true, creator , UNAUTHORIZED",
            "/greetingForKing GET: rejects request without `king` role, /greetingForKing, false, peasant, FORBIDDEN"
    })
    void getEndpointTestsForInvalidSignedJwts(String testName, String testEndpoint, boolean expiredJwt, String roles, String expectedHttpStatus) throws JOSEException {

        String testJwt = buildSignedJwt(expiredJwt, roles, null).serialize();

        assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET(testEndpoint).bearerAuth(testJwt), String.class))
                .isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
                    HttpClientResponseException castException = (HttpClientResponseException) e;
                    Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.valueOf(expectedHttpStatus));
                });
    }

    @Test
    void getEndpointRejectsUnsignedJunkJwt() {

        assertThatException().isThrownBy(() -> client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth("junk jwt"), String.class))
                .isInstanceOf(HttpClientResponseException.class).satisfies(e -> {
                    HttpClientResponseException castException = (HttpClientResponseException) e;
                    Assertions.assertThat((CharSequence) castException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    private SignedJWT buildSignedJwt(boolean expired, String roles, String name) throws JOSEException {

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
                .claim("roles", roles)
                .claim("name", name)
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);
        return signedJWT;
    }
}
