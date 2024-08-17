package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginDTO {
    private String username;
    private String password;
    @JsonProperty("jwt")
    private String jwt;

    public LoginDTO(String username, String password, String jwt) {
        this.username = username;
        this.password = password;
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginDTO() {
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
