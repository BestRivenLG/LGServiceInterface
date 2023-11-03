package org.example.entity;

public enum RespErrorCode {

    OK(0, "OK", ""),
    ERROR(1001, "ERROR", ""),


    SUCCESS(200, "Success", ""),
    UNREGISTER(10001, "Unregistered", ""),

    INVAILTOKEN(10002, "The Token is invalid. Please log in again", ""),

    USERNAMEERROR(10003, "UserNameFormatError", "请输入4到16个字符，支持英文/数字，首位必须英文"),

    PASSWORDERROR(10004, "PasswordFormatError", "请输入6到16位，至少包含英文/数字/符号中的两种");


    private final long code;
    private final String message;

    private final String detail;

    RespErrorCode(long code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }
}
