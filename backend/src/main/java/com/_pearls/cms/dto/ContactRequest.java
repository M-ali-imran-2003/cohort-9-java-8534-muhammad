package com._pearls.cms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ContactRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "first name is required")
    private String firstName;

    @NotBlank(message = "last name is required")
    private String lastName;

    @NotEmpty
    @Valid
    private List<EmailDto> emails;

    @NotEmpty
    @Valid
    private List<PhoneDto> phones;

    public List<EmailDto> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailDto> emails) {
        this.emails = emails;
    }

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ContactRequest(String title, String firstName, String lastName, List<EmailDto> emails, List<PhoneDto> phones) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emails = emails;
        this.phones = phones;
    }

    public ContactRequest() {
    }
}
