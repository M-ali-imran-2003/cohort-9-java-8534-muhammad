package com._pearls.cms.service;

import com._pearls.cms.dto.ContactListResponse;
import com._pearls.cms.repository.ContactRepository;
import com._pearls.cms.repository.EmailRepository;
import com._pearls.cms.repository.PhoneRepository;
import com._pearls.cms.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final EmailRepository emailRepository;
    private final ContactRepository contactRepository;
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    public ContactService(EmailRepository emailRepository, ContactRepository contactRepository, PhoneRepository phoneRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.contactRepository = contactRepository;
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
    }

}
