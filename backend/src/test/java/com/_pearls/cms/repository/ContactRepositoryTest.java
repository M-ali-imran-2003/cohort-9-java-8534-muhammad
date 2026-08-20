package com._pearls.cms.repository;

import com._pearls.cms.entity.Contact;
import com._pearls.cms.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByUserIdFound() {

        // Arrange
        contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran",50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran",60L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());

        // Act
        Page<Contact> contactPage = contactRepository.findByUserId(50L, pageable);

        // Assert
        assertThat(contactPage).isNotNull();
        assertThat(contactPage.getContent()).hasSize(1);
        assertThat(contactPage.getTotalElements()).isEqualTo(2);
        assertThat(contactPage.getTotalPages()).isEqualTo(2);
        assertThat(contactPage.getContent().get(0).getFirstName()).isEqualTo("Imran");
    }

    @Test
    void testFindByUserIdNotFound() {

        // Arrange
        contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran",50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran",60L, LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());

        // Act
        Page<Contact> contactPage = contactRepository.findByUserId(40L, pageable);

        // Assert
        assertThat(contactPage).isNotNull();
        assertThat(contactPage.isEmpty()).isTrue();    }

    @Test
    void testFindByIdAndUserIdInvalidId() {

        // Arrange
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));

        // Act
        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId()+10L, 50L);

        // Assert
        assertThat(contact).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdInvalidUserId() {

        // Arrange
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));

        // Act
        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId(), 10L);

        // Assert
        assertThat(contact).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdInvalidBoth() {

        // Arrange
        Contact saved = contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));

        // Act
        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId()+10L, 10L);

        // Assert
        assertThat(contact).isEmpty();
    }

    @Test
    void testFindByIdAndUserIdFound() {

        // Arrange
        Contact saved=contactRepository.save(new Contact("Mr", "Imran", "Riaz",50L, LocalDateTime.now()));
        contactRepository.save(new Contact("Mr", "Ali", "Imran",50L, LocalDateTime.now()));

        // Act
        Optional<Contact> contact = contactRepository.findByIdAndUserId(saved.getId(), saved.getUserId());

        // Assert
        assertThat(contact).isPresent();
        assertThat(contact.get().getId()).isEqualTo(saved.getId());
        assertThat(contact.get().getUserId()).isEqualTo(saved.getUserId());
        assertThat(contact.get().getFirstName()).isEqualTo("Imran");
    }
}
