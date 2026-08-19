package com._pearls.cms.repository;

import com._pearls.cms.entity.Contact;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Long> {
    Page<Contact> findByUserId(Long userId, Pageable pageable);
    Optional<Contact> findByIdAndUserId(Long contactId, Long userId);
}
