package com._pearls.cms.dto;


public class RegisterRequest {

    private String name;
    private String email;
    private String phone;
    private String password;

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPassword() {
        return this.password;
    }
}
