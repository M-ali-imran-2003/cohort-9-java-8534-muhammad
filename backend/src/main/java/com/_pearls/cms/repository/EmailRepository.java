package com._pearls.cms.repository;

import com._pearls.cms.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findByContactId(Long contactId);
    void deleteByContactId(Long contactId);
}
