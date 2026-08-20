package com._pearls.cms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "contact_emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "email_label")
    private String label;

    @Column(name = "contact_id")
    private Long contactId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Email(Long id, String email, String label, Long contactId) {
        this.id = id;
        this.email = email;
        this.label = label;
        this.contactId = contactId;
    }
    public Email(String email, String label, Long contactId) {
        this.email = email;
        this.label = label;
        this.contactId = contactId;
    }


    public Email(){}
}