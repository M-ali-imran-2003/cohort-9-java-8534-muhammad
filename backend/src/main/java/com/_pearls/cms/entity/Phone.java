package com._pearls.cms.entity;

import jakarta.persistence.*;

@Entity(name = "contact_phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "phone_label")
    private String label;

    @Column(name = "contact_id")
    private Long contactId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Phone(Long id, String phone, String label, Long contactId) {
        this.id = id;
        this.phone = phone;
        this.label = label;
        this.contactId = contactId;
    }

    public Phone(String phone, String label, Long contactId) {
        this.phone = phone;
        this.label = label;
        this.contactId = contactId;
    }

    public Phone(){}
}