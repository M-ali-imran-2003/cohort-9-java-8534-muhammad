package com._pearls.cms.dto;

import java.time.LocalDateTime;

public class ContactListResponse {
    private Long id;
    private String title;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;

    public ContactListResponse(Long id, String title, String firstName, String lastName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }
    public ContactListResponse(){}

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
