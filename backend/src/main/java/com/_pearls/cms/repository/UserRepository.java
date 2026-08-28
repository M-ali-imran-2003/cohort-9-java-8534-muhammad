package com._pearls.cms.repository;

import com._pearls.cms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phone = :identifier")
    User findByEmailOrPhone(@Param("identifier") String identifier);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
