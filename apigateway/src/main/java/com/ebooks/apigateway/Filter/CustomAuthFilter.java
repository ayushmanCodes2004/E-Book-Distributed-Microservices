package com.ebooks.apigateway.Filter;

import com.ebooks.apigateway.Exception.InvalidTokenException;
import com.ebooks.apigateway.Exception.MissingAuthorizationHeaderException;
import com.ebooks.apigateway.Util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RouteValidator validator;

    public CustomAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            boolean isSecured = validator.isSecured.test(exchange.getRequest());
            
            if (isSecured) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.warn("Missing Authorization Header for: {}", path);
                    throw new MissingAuthorizationHeaderException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {

//                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);

                } catch (Exception e) {
                    log.error("Invalid token for: {}", path);
                    throw new InvalidTokenException("Unauthorized access to application: Invalid or expired token", e);
                }
            } else {
                log.debug("Open endpoint - skipping auth for: {}", path);
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // custom config if needed
    }
}

