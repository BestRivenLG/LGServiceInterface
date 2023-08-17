package org.example.entity;

public enum RespErrorCode {

    OK(0, "OK"),
    ERROR(1001, "ERROR"),

    SUCCESS(200, "Success"),
    UNREGISTER(10001, "Unregistered"),

    INVAILTOKEN(10002, "Token已失效，请重新登录");

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
