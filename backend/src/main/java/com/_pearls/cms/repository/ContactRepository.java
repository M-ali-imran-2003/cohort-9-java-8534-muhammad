package com._pearls.cms.repository;

import com._pearls.cms.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Long> {
    List<Contact> findByUserId(Long userId);
    Optional<Contact> findByIdAndUserId(Long id, Long userId);
}
