package com.politikos.apigateway.util;

import com.politikos.apigateway.handler.InvalidTokenException;
import com.politikos.apigateway.handler.MissingAuthorizationHeaderException;
import com.politikos.apigateway.handler.UnauthorizedAccessException;
import com.politikos.apigateway.validator.Routes;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final Routes validator;
    private final JwtService service;

    public AuthenticationFilter(Routes validator, JwtService service) {
        super(Config.class);
        this.validator = validator;
        this.service = service;
    }

    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new MissingAuthorizationHeaderException();
                }

                String jwt = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
                if (jwt != null && jwt.startsWith("Bearer ")) {
                    jwt = jwt.substring(7);
                }

                try {
                    service.validateToken(jwt);
                    // Do further processing or validation if required
                } catch (InvalidTokenException e) {
                    // Log the error or return a specific response
                    throw new UnauthorizedAccessException("Invalid access token");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
