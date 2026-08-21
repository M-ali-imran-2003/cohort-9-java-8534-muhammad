package com._pearls.cms.service;

import com._pearls.cms.dto.*;
import com._pearls.cms.entity.Contact;
import com._pearls.cms.entity.Email;
import com._pearls.cms.entity.Phone;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.ContactRepository;
import com._pearls.cms.repository.EmailRepository;
import com._pearls.cms.repository.PhoneRepository;
import com._pearls.cms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
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

    @Transactional
    public SuccessResponse addContact(Long userId, ContactRequest contactRequest) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User with the id not found");
            throw new ResourceNotFoundException("User not found");
        }

        Contact contact = new Contact();
        contact.setTitle(contactRequest.getTitle());
        contact.setFirstName(contactRequest.getFirstName());
        contact.setLastName(contactRequest.getLastName());
        contact.setUserId(userId);
        contact.setCreatedAt(LocalDateTime.now());
        contactRepository.save(contact);

        for (EmailDto e : contactRequest.getEmails()) {
            Email email = new Email();
            email.setContactId(contact.getId());
            email.setEmail(e.getEmail());
            email.setLabel(e.getLabel());
            emailRepository.save(email);
        }

        for (PhoneDto p : contactRequest.getPhones()) {
            Phone phone = new Phone();
            phone.setContactId(contact.getId());
            phone.setPhone(p.getPhone());
            phone.setLabel(p.getLabel());
            phoneRepository.save(phone);
        }

        log.info("Complete Contact Added for the user");
        return new SuccessResponse("Contact Added Successfully");
    }

    public Page<ContactListResponse> findAllContacts(Long userId, int page, String search) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User with id not found");
            throw new ResourceNotFoundException("User not found with the id");
        }

        Pageable pageable = PageRequest.of(page, 5, Sort.by("firstName", "lastName"));
        Page<Contact> contacts = contactRepository.findByUserIdAndSearch(userId, search, pageable);

        return contacts.map(contact -> {
            ContactListResponse response = new ContactListResponse();
            response.setId(contact.getId());
            response.setTitle(contact.getTitle());
            response.setFirstName(contact.getFirstName());
            response.setLastName(contact.getLastName());
            response.setCreatedAt(contact.getCreatedAt());
            return response;
        });
    }

    public ContactResponse findContact(Long userId, Long contactId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User not found");
            throw new ResourceNotFoundException("User not found with the id");
        }
        Contact contact = contactRepository.findByIdAndUserId(
                contactId,
                userId
        ).orElseThrow(() -> {
            log.warn("contact id not found");
            return new ResourceNotFoundException("Contact not found");
        });

            List<EmailDto> emails = emailRepository.findByContactId(contactId).
                    stream().map(email -> {
                        EmailDto response = new EmailDto();
                        response.setEmail(email.getEmail());
                        response.setLabel(email.getLabel());
                        return response;
                    }).toList();

            List<PhoneDto> phones = phoneRepository.findByContactId(contactId).
                    stream().map(phone -> {
                        PhoneDto response = new PhoneDto();
                        response.setPhone(phone.getPhone());
                        response.setLabel(phone.getLabel());
                        return response;
                    }).toList();
            return new ContactResponse(contact.getId(), contact.getTitle(), contact.getFirstName(), contact.getLastName(), emails, phones, contact.getCreatedAt());
    }

    @Transactional
    public SuccessResponse deleteContact(Long contactId, Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User not found");
            throw new ResourceNotFoundException("User not found with the id");
        }
            Contact contact = contactRepository.findByIdAndUserId(
                    contactId,
                    userId
            ).orElseThrow(() -> {
                log.warn("contact not found");
                return new ResourceNotFoundException("Contact not found with the id");
            });

            emailRepository.deleteByContactId(contactId);
            phoneRepository.deleteByContactId(contactId);
            contactRepository.delete(contact);

            log.info("Contact deleted");
            return new SuccessResponse("Contact deleted successfully");

    }

    @Transactional
    public SuccessResponse updateContact(Long contactId, Long userId, ContactRequest contactRequest) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User with id not found");
            throw new ResourceNotFoundException("User not found with the id");
        }
            Contact contact = contactRepository.findByIdAndUserId(
                    contactId,
                    userId
            ).orElseThrow(() -> {
                log.warn("Contact not found");
                return new ResourceNotFoundException("contact not found with the id");
            });

            contact.setTitle(contactRequest.getTitle());
            contact.setFirstName(contactRequest.getFirstName());
            contact.setLastName(contactRequest.getLastName());
            contactRepository.save(contact);

            emailRepository.deleteByContactId(contactId);
            phoneRepository.deleteByContactId(contactId);

            for (EmailDto e : contactRequest.getEmails()) {
                Email email = new Email();
                email.setContactId(contact.getId());
                email.setEmail(e.getEmail());
                email.setLabel(e.getLabel());
                emailRepository.save(email);
            }

            for (PhoneDto p : contactRequest.getPhones()) {
                Phone phone = new Phone();
                phone.setContactId(contact.getId());
                phone.setPhone(p.getPhone());
                phone.setLabel(p.getLabel());
                phoneRepository.save(phone);
            }

            log.info("Contact Updated");
            return new SuccessResponse("Contact updated successfully");

    }

}
