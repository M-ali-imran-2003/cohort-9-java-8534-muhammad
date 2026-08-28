package com._pearls.cms.dto;

import jakarta.validation.constraints.NotBlank;

public class PhoneDto {

    @NotBlank(message = "phone label is required")
    private String label;

    @NotBlank(message = "phone is required")
    private String phone;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhoneDto(String label, String phone) {
        this.label = label;
        this.phone = phone;
    }
    public PhoneDto(){}
}
