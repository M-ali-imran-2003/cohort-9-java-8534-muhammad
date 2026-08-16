package com._pearls.cms.dto;

import java.time.LocalDateTime;

public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime joinedAt;

    public ProfileResponse(Long id, String name, String email, String phone, LocalDateTime joinedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
