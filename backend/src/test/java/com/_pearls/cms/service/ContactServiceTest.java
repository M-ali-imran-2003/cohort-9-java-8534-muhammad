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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EmailRepository emailRepository;
    @Mock private ContactRepository contactRepository;
    @Mock private PhoneRepository phoneRepository;

    @InjectMocks
    private ContactService contactService;

    private ContactRequest buildRequest() {
        ContactRequest request = new ContactRequest();
        request.setTitle("Mr");
        request.setFirstName("Ali");
        request.setLastName("Imran");
        request.setEmails(List.of(new EmailDto("Work", "ali@work.com")));
        request.setPhones(List.of(new PhoneDto("Work", "03001234567")));
        return request;
    }

    @Test
    void addContactSuccess() {
        Long userId = 50L;
        ContactRequest request = buildRequest();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> {
            Contact c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        SuccessResponse response = contactService.addContact(userId, request);

        assertNotNull(response);
        assertEquals("Contact Added Successfully", response.getMessage());
        verify(contactRepository, times(1)).save(any(Contact.class));
        verify(emailRepository, times(1)).save(any(Email.class));
        verify(phoneRepository, times(1)).save(any(Phone.class));
    }

    @Test
    void addContactUserNotFound() {
        Long userId = 50L;
        ContactRequest request = buildRequest();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.addContact(userId, request));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void findAllContactsSuccessNoSearch() {
        Long userId = 50L;
        Contact contact = new Contact("Mr", "Ali", "Imran", userId, LocalDateTime.now());
        contact.setId(1L);
        Page<Contact> page = new PageImpl<>(List.of(contact));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByUserIdAndSearch(eq(userId), isNull(), any(Pageable.class)))
                .thenReturn(page);

        Page<ContactListResponse> result = contactService.findAllContacts(userId, 0, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Ali", result.getContent().get(0).getFirstName());
    }

    @Test
    void findAllContactsSuccessWithSearch() {
        Long userId = 50L;
        Contact contact = new Contact("Mr", "Ali", "Imran", userId, LocalDateTime.now());
        contact.setId(1L);
        Page<Contact> page = new PageImpl<>(List.of(contact));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByUserIdAndSearch(eq(userId), eq("ali"), any(Pageable.class)))
                .thenReturn(page);

        Page<ContactListResponse> result = contactService.findAllContacts(userId, 0, "ali");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findAllContactsUserNotFound() {
        Long userId = 50L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.findAllContacts(userId, 0, null));
    }

    @Test
    void findContactSuccess() {
        Long userId = 50L;
        Long contactId = 1L;
        Contact contact = new Contact("Mr", "Ali", "Imran", userId, LocalDateTime.now());
        contact.setId(contactId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.of(contact));
        when(emailRepository.findByContactId(contactId))
                .thenReturn(List.of(new Email("ali@work.com", "Work", contactId)));
        when(phoneRepository.findByContactId(contactId))
                .thenReturn(List.of(new Phone("03001234567", "Work", contactId)));

        ContactResponse response = contactService.findContact(userId, contactId);

        assertNotNull(response);
        assertEquals("Ali", response.getFirstName());
        assertEquals(1, response.getEmails().size());
        assertEquals(1, response.getPhones().size());
    }

    @Test
    void findContactNotFound() {
        Long userId = 50L;
        Long contactId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.findContact(userId, contactId));
    }

    @Test
    void deleteContactSuccess() {
        Long userId = 50L;
        Long contactId = 1L;
        Contact contact = new Contact("Mr", "Ali", "Imran", userId, LocalDateTime.now());
        contact.setId(contactId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.of(contact));

        SuccessResponse response = contactService.deleteContact(contactId, userId);

        assertNotNull(response);
        assertEquals("Contact deleted successfully", response.getMessage());
        verify(emailRepository, times(1)).deleteByContactId(contactId);
        verify(phoneRepository, times(1)).deleteByContactId(contactId);
        verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    void deleteContactNotFound() {
        Long userId = 50L;
        Long contactId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.deleteContact(contactId, userId));
        verify(contactRepository, never()).delete(any(Contact.class));
    }

    @Test
    void updateContactSuccess() {
        Long userId = 50L;
        Long contactId = 1L;
        Contact existing = new Contact("Mr", "Ali", "Imran", userId, LocalDateTime.now());
        existing.setId(contactId);
        ContactRequest request = buildRequest();
        request.setFirstName("Updated");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.of(existing));

        SuccessResponse response = contactService.updateContact(contactId, userId, request);

        assertNotNull(response);
        assertEquals("Contact updated successfully", response.getMessage());
        assertEquals("Updated", existing.getFirstName());
        verify(emailRepository, times(1)).deleteByContactId(contactId);
        verify(phoneRepository, times(1)).deleteByContactId(contactId);
        verify(emailRepository, times(1)).save(any(Email.class));
        verify(phoneRepository, times(1)).save(any(Phone.class));
    }

    @Test
    void updateContactNotFound() {
        Long userId = 50L;
        Long contactId = 1L;
        ContactRequest request = buildRequest();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(contactRepository.findByIdAndUserId(contactId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.updateContact(contactId, userId, request));
        verify(contactRepository, never()).save(any(Contact.class));
    }
}