package com._pearls.cms.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
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
}
