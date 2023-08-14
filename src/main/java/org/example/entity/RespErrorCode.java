package org.example.entity;

public enum RespErrorCode {

    SUCCESS(0, "Success"),
    FAILURE(1001, "Failure"),
    UNREGISTER(10001, "Unregistered");

    private final long code;
    private final String message;

    RespErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
