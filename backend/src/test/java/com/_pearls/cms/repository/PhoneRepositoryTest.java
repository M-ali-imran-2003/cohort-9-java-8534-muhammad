package com._pearls.cms.repository;

import com._pearls.cms.entity.Phone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PhoneRepositoryTest {

    @Autowired
    private PhoneRepository phoneRepository;

    @Test
    void testDeleteByContactIdSuccess() {

        // Arrange
        Long contactId = 50L;
        Phone saved = phoneRepository.save(new Phone("03009286388", "Work",contactId));
        Long generatedId = saved.getId();

        assertThat(phoneRepository.findById(generatedId)).isPresent();

        // Act
        phoneRepository.deleteByContactId(contactId);

        // Assert
        assertThat(phoneRepository.findById(generatedId)).isEmpty();
    }

    @Test
    void testDeleteByContactIdFail() {

        // Arrange
        Long contactId = 50L;
        Phone saved = phoneRepository.save(new Phone("03009286388", "Work",contactId));
        Long generatedId = saved.getId();

        assertThat(phoneRepository.findById(generatedId)).isPresent();

        // Act
        phoneRepository.deleteByContactId(60L);

        // Assert
        assertThat(phoneRepository.findById(generatedId)).isPresent();
    }

    @Test
    void testFindByContactIdSuccess() {

        // Arrange
        Long contactId = 50L;
        phoneRepository.save(new Phone("03009286388", "Work",contactId));
        phoneRepository.save(new Phone("03222496362", "Work",contactId));
        phoneRepository.save(new Phone("03349286388", "Office",60L));

        // Act
        List<Phone> phones = phoneRepository.findByContactId(contactId);

        // Assert
        assertThat(phones).isNotNull();
        assertThat(phones).hasSize(2);
        assertThat(phones)
                .extracting(Phone::getPhone)
                .containsExactlyInAnyOrder("03009286388", "03222496362");
    }

    @Test
    void testFindByContactIdFail() {

        // Arrange
        phoneRepository.save(new Phone("03222496362", "Office",60L));

        // Act
        List<Phone> phones = phoneRepository.findByContactId(10L);

        // Assert
        assertThat(phones).isNotNull();
        assertThat(phones).isEmpty();
    }
}
