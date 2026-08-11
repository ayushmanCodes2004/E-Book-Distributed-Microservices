package com.ebooks.apigateway.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallBackController {

    @RequestMapping(value = "/book", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> bookFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        Map<String, String> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Book Service is temporarily unavailable. Please try again later.");
        response.put("service", "book-service");
        return Mono.just(response);
    }

    @RequestMapping(value = "/order", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> orderFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        Map<String, String> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Order Service is temporarily unavailable. Please try again later.");
        response.put("service", "order-service");
        return Mono.just(response);
    }

    @RequestMapping(value = "/auth", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> authFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        Map<String, String> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Auth Service is temporarily unavailable. Please try again later.");
        response.put("service", "auth-service");
        return Mono.just(response);
    }

    @RequestMapping(value = "/payment", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> paymentFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        Map<String, String> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Payment Service is temporarily unavailable. Please try again later.");
        response.put("service", "payment-service");
        return Mono.just(response);
    }

}
