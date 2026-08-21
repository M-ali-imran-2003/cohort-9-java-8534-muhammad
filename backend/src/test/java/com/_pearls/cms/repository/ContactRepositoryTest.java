package com._pearls.cms.repository;

import com._pearls.cms.entity.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByUserIdAndSearchNoFilterFound() {
        contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran", 50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran", 60L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());

        Page<Contact> contactPage = contactRepository.findByUserIdAndSearch(50L, null, pageable);

        assertThat(contactPage).isNotNull();
        assertThat(contactPage.getContent()).hasSize(1);
        assertThat(contactPage.getTotalElements()).isEqualTo(2);
        assertThat(contactPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void testFindByUserIdAndSearchNoFilterNotFound() {
        contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 1);

        Page<Contact> contactPage = contactRepository.findByUserIdAndSearch(40L, null, pageable);

        assertThat(contactPage.isEmpty()).isTrue();
    }

    @Test
    void testFindByUserIdAndSearchMatchesFirstName() {
        contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Momin", 50L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 5, Sort.by("firstName"));

        Page<Contact> result = contactRepository.findByUserIdAndSearch(50L, "imran", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Imran");
    }

    @Test
    void testFindByUserIdAndSearchMatchesLastName() {
        contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Momin", 50L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 5);

        Page<Contact> result = contactRepository.findByUserIdAndSearch(50L, "riaz", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Riaz");
    }

    @Test
    void testFindByUserIdAndSearchNoMatch() {
        contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 5);

        Page<Contact> result = contactRepository.findByUserIdAndSearch(50L, "xyz", pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdInvalidId() {
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));

        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId() + 10L, 50L);

        assertThat(contact).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdInvalidUserId() {
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));

        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId(), 10L);

        assertThat(contact).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdFound() {
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz", 50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran", 50L, LocalDateTime.now()));

        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId(), saved.getUserId());

        assertThat(contact).isPresent();
        assertThat(contact.get().getFirstName()).isEqualTo("Imran");
    }
}