package com.yuwangyx.ping.service.impl

import com.yuwangyx.ping.compoment.RateLimiter
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

class PingServiceSpec extends Specification {

    @Subject
    PingServiceImpl pingService

    def webClientBuilder = Mock(WebClient.Builder)
    def webClient = Mock(WebClient)
    def rocketMQTemplate = Mock(RocketMQTemplate)
    def specificUriSpec = Mock(WebClient.RequestHeadersUriSpec)
    def responseSpec = Mock(WebClient.ResponseSpec)
    def rateLimiter = Mock(RateLimiter)


    def setup() {
        webClientBuilder.baseUrl("http://localhost:18088/pong") >> webClientBuilder
        webClientBuilder.build() >> webClient
        pingService = new PingServiceImpl(webClientBuilder, rocketMQTemplate, rateLimiter, "test", "testIp")
        webClient.mutate() >> webClientBuilder
        webClientBuilder.defaultHeader(_, _) >> webClientBuilder
        webClient.get() >> specificUriSpec
        specificUriSpec.retrieve() >> responseSpec
    }

    def "test ping with rate limiter allowed"() {
        given:
        rateLimiter.allowRequest() >> Mono.just(true)
        webClient.mutate() >> webClientBuilder
        webClientBuilder.defaultHeader(_, _) >> webClientBuilder
        responseSpec.bodyToMono(String) >> Mono.just("World")

        when:
        def result = pingService.ping().block()

        then:
        1 * rocketMQTemplate.convertAndSend("pingpong_topic", _)
        result == "World"
    }

    def "test ping with rate limiter not allowed"() {
        given:
        rateLimiter.allowRequest() >> Mono.just(false)

        when:
        def result = pingService.ping().block()

        then:
        1 * rocketMQTemplate.convertAndSend("pingpong_topic", _)
        result == "Rate Limited"
    }

    def "test ping with 429 error"() {
        given:
        rateLimiter.allowRequest() >> Mono.just(true)
        def exception = new WebClientResponseException("Too Many Requests", 429, "Too Many Requests", null, null, null)
        def mono = Mono.error(exception)
        responseSpec.bodyToMono(String) >> mono

        when:
        def result = pingService.ping().block()

        then:
        1 * rocketMQTemplate.convertAndSend("pingpong_topic", _)
        result == "Throttled"
    }

    def "test ping with other error"() {
        given:
        rateLimiter.allowRequest() >> Mono.just(true)
        def exception = new WebClientResponseException("Internal Server Error", 500, "Internal Server Error", null, null, null)
        def mono = Mono.error(exception)
        responseSpec.bodyToMono(String) >> mono

        when:
        def result = pingService.ping().block()

        then:
        1 * rocketMQTemplate.convertAndSend("pingpong_topic", _)
        result == "Throttled"
    }
}
