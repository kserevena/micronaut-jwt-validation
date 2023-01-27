package com.qumu;

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

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class ApiTest {

    // Valid JWT signed with HS256 algorithm using secret `pleaseChangeThisSecretForANewOne`
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.6cD3MnZmX2xyEAWyh-GgGD11TX8SmvmHVLknuAIJ8yE";
    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void greetingGetEndpointRespondsToAuthorizedRequests() {

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/greeting").bearerAuth(VALID_TOKEN), String.class);
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
