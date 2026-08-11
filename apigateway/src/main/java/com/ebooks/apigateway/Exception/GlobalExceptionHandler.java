package com.ebooks.apigateway.Exception;

import com.ebooks.apigateway.DTO.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(1)  // Changed from -1 to 1 to let circuit breaker handle exceptions first
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse;

        if (ex instanceof UnauthorizedException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ex.getMessage(),
                    LocalDateTime.now()
            );
        } else if (ex instanceof MissingAuthorizationHeaderException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ex.getMessage(),
                    LocalDateTime.now()
            );
        } else if (ex instanceof InvalidTokenException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ex.getMessage(),
                    LocalDateTime.now()
            );
        } else if (ex instanceof ExpiredJwtException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "JWT token has expired",
                    LocalDateTime.now()
            );
        } else if (ex instanceof MalformedJwtException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid JWT token format",
                    LocalDateTime.now()
            );
        } else if (ex instanceof SignatureException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "JWT signature validation failed",
                    LocalDateTime.now()
            );
        } else if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Service or route not found: " + ex.getMessage(),
                    LocalDateTime.now()
            );
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            response.setStatusCode(rse.getStatusCode());
            errorResponse = new ErrorResponse(
                    rse.getStatusCode().value(),
                    rse.getReason() != null ? rse.getReason() : "Gateway error",
                    LocalDateTime.now()
            );
        } else if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            errorResponse = new ErrorResponse(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Service is temporarily unavailable. Please try again later.",
                    LocalDateTime.now()
            );
        } else if (ex.getMessage() != null && ex.getMessage().contains("timeout")) {
            response.setStatusCode(HttpStatus.GATEWAY_TIMEOUT);
            errorResponse = new ErrorResponse(
                    HttpStatus.GATEWAY_TIMEOUT.value(),
                    "Gateway timeout: Service took too long to respond",
                    LocalDateTime.now()
            );
        } else if (ex instanceof RuntimeException && ex.getMessage() != null) {
            if (ex.getMessage().contains("missing authorization header")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                errorResponse = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Missing authorization header",
                        LocalDateTime.now()
                );
            } else if (ex.getMessage().contains("un authorized access")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                errorResponse = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized access to application",
                        LocalDateTime.now()
                );
            } else {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                errorResponse = new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                );
            }
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An unexpected error occurred: " + (ex.getMessage() != null ? ex.getMessage() : "Unknown error"),
                    LocalDateTime.now()
            );
        }

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            bytes = "{\"status\":500,\"message\":\"Error processing exception\"}".getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
