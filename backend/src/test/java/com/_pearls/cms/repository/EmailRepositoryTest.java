package com._pearls.cms.repository;

import com._pearls.cms.entity.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmailRepositoryTest {

    @Autowired
    private EmailRepository emailRepository;

    @Test
    void testDeleteByContactIdSuccess() {

        // Arrange
        Long contactId = 50L;
        Email saved = emailRepository.save(new Email("ali@gmail.com", "Work",contactId));
        Long generatedId = saved.getId();

        assertThat(emailRepository.findById(generatedId)).isPresent();

        // Act
        emailRepository.deleteByContactId(contactId);

        // Assert
        assertThat(emailRepository.findById(generatedId)).isEmpty();
    }

    @Test
    void testDeleteByContactIdFail() {

        // Arrange
        Long contactId = 50L;
        Email saved = emailRepository.save(new Email("ali@gmail.com", "Work",contactId));
        Long generatedId = saved.getId();

        assertThat(emailRepository.findById(generatedId)).isPresent();

        // Act
        emailRepository.deleteByContactId(60L);

        // Assert
        assertThat(emailRepository.findById(generatedId)).isPresent();
    }

    @Test
    void testFindByContactIdSuccess() {

        // Arrange
        Long contactId = 50L;
        emailRepository.save(new Email("ali@gmail.com", "Work",contactId));
        emailRepository.save(new Email("momin@gmail.com", "Work",contactId));
        emailRepository.save(new Email("umar@gmail.com", "Office",60L));

        // Act
        List<Email> emails = emailRepository.findByContactId(contactId);

        // Assert
        assertThat(emails).isNotNull();
        assertThat(emails).hasSize(2);
        assertThat(emails)
                .extracting(Email::getEmail)
                .containsExactlyInAnyOrder("ali@gmail.com", "momin@gmail.com");
    }

    @Test
    void testFindByContactIdFail() {

        // Arrange
        emailRepository.save(new Email("umar@gmail.com", "Office",60L));

        // Act
        List<Email> emails = emailRepository.findByContactId(10L);

        // Assert
        assertThat(emails).isNotNull();
        assertThat(emails).isEmpty();
    }
}
