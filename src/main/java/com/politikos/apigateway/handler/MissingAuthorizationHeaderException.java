package com.politikos.apigateway.handler;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException() {
        super("Missing authorization header");
    }
}