package com._pearls.cms.service;

import com._pearls.cms.dto.*;
import com._pearls.cms.entity.Contact;
import com._pearls.cms.entity.Email;
import com._pearls.cms.entity.Phone;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.ContactRepository;
import com._pearls.cms.repository.EmailRepository;
import com._pearls.cms.repository.PhoneRepository;
import com._pearls.cms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public SuccessResponse addContact(Long userId, ContactRequest contactRequest)
    {
        boolean exists = userRepository.existsById(
                userId
        );
        if (exists) {
            Contact contact = new Contact();
            contact.setTitle(contactRequest.getTitle());
            contact.setFirstName(contactRequest.getFirstName());
            contact.setLastName(contactRequest.getLastName());
            contact.setUserId(userId);
            contact.setCreatedAt(LocalDateTime.now());
            contactRepository.save(contact);

            for (EmailDto e : contactRequest.getEmails()){
                Email email = new Email();
                email.setContactId(contact.getId());
                email.setEmail(e.getEmail());
                email.setLabel(e.getLabel());
                emailRepository.save(email);
            }

            for (PhoneDto p : contactRequest.getPhones()){
                Phone phone = new Phone();
                phone.setContactId(contact.getId());
                phone.setPhone(p.getPhone());
                phone.setLabel(p.getLabel());
                phoneRepository.save(phone);
            }
            log.info("Complete Contact Added for the user");
            return new SuccessResponse("Contact Added Successfully");
       }
        else {
            log.warn("User with the id not found");
            throw new ResourceNotFoundException("User not found");
        }

    }

    public Page<ContactListResponse> findAllContact(Long userId,int page) {
        boolean exists = userRepository.existsById(
                userId
        );
        if (exists) {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Contact> contacts = contactRepository.findByUserId(userId, pageable);

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
        else {
            log.warn("User with id not found");
            throw new ResourceNotFoundException("User not found with the id");
        }
    }

    public ContactResponse findContact(Long userId, Long contactId) {
        Optional<Contact> contact = contactRepository.findByIdAndUserId(
                contactId,
                userId
        );
        if(contact.isPresent()){
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
            return new ContactResponse(contact.get().getId(), contact.get().getTitle(),contact.get().getFirstName(),contact.get().getLastName(),emails,phones,contact.get().getCreatedAt());
        }
        else {
            log.warn("Contact Not Found with the id or user id");
            throw new ResourceNotFoundException("Contact not found");
        }
    }

    @Transactional
    public SuccessResponse deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        emailRepository.deleteByContactId(contactId);
        phoneRepository.deleteByContactId(contactId);
        contactRepository.delete(contact);

        log.info("Contact deleted");
        return new SuccessResponse("Contact deleted successfully");
    }

    @Transactional
    public SuccessResponse updateContact(Long contactId, Long userId, ContactRequest contactRequest) {

        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contact.setTitle(contactRequest.getTitle());
        contact.setFirstName(contactRequest.getFirstName());
        contact.setLastName(contactRequest.getLastName());
        contactRepository.save(contact);

        emailRepository.deleteByContactId(contactId);
        phoneRepository.deleteByContactId(contactId);

        for (EmailDto e : contactRequest.getEmails()){
            Email email = new Email();
            email.setContactId(contact.getId());
            email.setEmail(e.getEmail());
            email.setLabel(e.getLabel());
            emailRepository.save(email);
        }

        for (PhoneDto p : contactRequest.getPhones()){
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
