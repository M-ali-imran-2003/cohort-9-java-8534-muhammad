package com._pearls.cms.repository;

import com._pearls.cms.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmailOrPhoneFoundEmail() {
        User user = new User();
        user.setName("Ali");
        user.setPhone("03009286388");
        user.setEmail("ali@test.com");
        user.setPassword("12345");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        User found =  userRepository.findByEmailOrPhone("ali@test.com");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("ali@test.com");
    }

    @Test
    void testFindByEmailOrPhoneFoundPhone() {
        User user = new User();
        user.setName("Ali");
        user.setPhone("03009286388");
        user.setEmail("ali@test.com");
        user.setPassword("12345");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        User found =  userRepository.findByEmailOrPhone("03009286388");
        assertThat(found).isNotNull();
        assertThat(found.getPhone()).isEqualTo("03009286388");
    }

    @Test
    void testFindByEmailOrPhoneFoundNothing() {
        User found =  userRepository.findByEmailOrPhone("abc123");
        assertThat(found).isNull();
    }

    @Test
    void testExistsByEmailTrue() {
        User user = new User();
        user.setName("Ali");
        user.setPhone("03009286388");
        user.setEmail("ali@test.com");
        user.setPassword("12345");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        boolean exists =  userRepository.existsByEmail("ali@test.com");
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmailFalse() {
        boolean exists =  userRepository.existsByEmail("ali@test.com");
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByPhoneTrue() {
        User user = new User();
        user.setName("Ali");
        user.setPhone("03009286388");
        user.setEmail("ali@test.com");
        user.setPassword("12345");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        boolean exists =  userRepository.existsByPhone("03009286388");
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByPhoneFalse() {
        boolean exists =  userRepository.existsByPhone("03009286388");
        assertThat(exists).isFalse();
    }
}
