package org.example.common;

public enum RespErrorCode {

    OK(0, "OK", "Success", ""),
    ERROR(1001, "ERROR", "", ""),


    SUCCESS(200, "Success", "", ""),
    UNREGISTER(10001, "Unregistered", "The user name does not exist", ""),

    INVAILTOKEN(10002, "InvailToken", "The Token is invalid. Please log in again", ""),

    USERNAMEERROR(10003, "UserNameFormatError", "Please enter 4 to 16 characters, support English / digits, the first must be English", "请输入4到16个字符，支持英文/数字，首位必须英文"),

    PASSWORDERROR(10004, "PasswordFormatError", "Please enter 6 to 16 characters, including at least two of English/numbers/symbols", "请输入6到16位，至少包含英文/数字/符号中的两种");


    private final long code;
    private final String message;

    private final String status;

    private final String detail;

    RespErrorCode(long code, String status, String message, String detail) {
        this.code = code;
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}
