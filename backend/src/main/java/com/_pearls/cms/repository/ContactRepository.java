package com._pearls.cms.repository;

import com._pearls.cms.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT DISTINCT c FROM Contact c " +
            "LEFT JOIN Email e ON e.contactId = c.id " +
            "LEFT JOIN Phone p ON p.contactId = c.id " +
            "WHERE c.userId = :userId " +
            "AND (CAST(:search AS string) IS NULL OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
            "OR p.phone LIKE CONCAT('%', CAST(:search AS string), '%'))")
    Page<Contact> findByUserIdAndSearch(@Param("userId") Long userId,
                                        @Param("search") String search,
                                        Pageable pageable);

    Optional<Contact> findByIdAndUserId(Long contactId, Long userId);

    List<Contact> findAllByUserId(Long userId);
}