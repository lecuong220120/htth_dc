package com.example.demo.Obj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseDctk {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("success")
    private boolean success;
    public ResponseDctk(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseDctk(Integer code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    @Override
    public String toString() {
        return "ResponseDctk{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
