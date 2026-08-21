package com._pearls.cms.dto;

import jakarta.validation.constraints.NotBlank;

public class EmailDto {

    @NotBlank(message = "email label is required")
    private String label;

    @NotBlank(message = "email is required")
    private String email;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailDto(String label, String email) {
        this.label = label;
        this.email = email;
    }

    public EmailDto(){}
}
