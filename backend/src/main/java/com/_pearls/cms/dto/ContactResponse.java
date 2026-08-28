package com._pearls.cms.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ContactResponse {

    private Long id;

    private String title;

    private String firstName;

    private String lastName;

    private List<EmailDto> emails;

    private List<PhoneDto> phones;

    private LocalDateTime createdAt;

    public ContactResponse(Long id, String title, String firstName, String lastName, List<EmailDto> emails, List<PhoneDto> phones, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emails = emails;
        this.phones = phones;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
