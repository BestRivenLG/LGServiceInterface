package org.example.entity;

import lombok.Getter;

public class RespEmptyResult {

    @Getter
    private String msg;

    private  String status;

    private long code;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
