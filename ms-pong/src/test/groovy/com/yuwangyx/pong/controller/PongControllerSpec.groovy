package com.yuwangyx.pong.controller

import com.yuwangyx.pong.service.PongService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.mockito.Mockito.when

@WebFluxTest(controllers = [PongController])
class PongControllerSpec extends Specification {

    @Autowired
    WebTestClient webClient

    @MockBean
    PongService pongService

    def "test response success"() {
        given:
        when(pongService.handlePong()).thenReturn(ServerResponse.ok().body(Mono.just("World"), String.class))

        when:
        def result = webClient.get()
                .uri("/pong")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(String.class)

        then:
        def responseBody = result.getResponseBody().blockFirst()
        responseBody == "World"
    }

    def "test response 429"() {
        given:
        when(pongService.handlePong()).thenReturn(ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).body(Mono.just("429"), String.class))

        when:
        def result = webClient.get()
                .uri("/pong")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .returnResult(String.class)

        then:
        def responseBody = result.getResponseBody().blockFirst()
        responseBody == "429"
    }
}
