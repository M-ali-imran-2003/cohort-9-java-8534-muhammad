package com._pearls.cms.repository;

import com._pearls.cms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmailOrPhone(String email, String phone);
}
