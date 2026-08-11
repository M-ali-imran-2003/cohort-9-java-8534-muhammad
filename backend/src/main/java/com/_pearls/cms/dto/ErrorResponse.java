package com._pearls.cms.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String,String> fieldErrors;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    public ErrorResponse(String code, String message, String path, Map<String, String> fieldErrors) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = fieldErrors;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

}
