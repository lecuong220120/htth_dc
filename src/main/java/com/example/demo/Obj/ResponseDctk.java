package com.example.demo.Obj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseDctk {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;

    public ResponseDctk(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseDctk() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
