package com.qumu;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class ApiTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void greetingApiResponseAsExpected() {

        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/greeting"), String.class);
        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isEqualTo("Hello World!");
    }
}
