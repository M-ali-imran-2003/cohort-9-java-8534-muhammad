package com._pearls.cms.repository;

import com._pearls.cms.entity.Email;
import com._pearls.cms.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone,Long> {

    List<Phone> findByContactId(Long contactId);
    void deleteByContactId(Long contactId);

}
