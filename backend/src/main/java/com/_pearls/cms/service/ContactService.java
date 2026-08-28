package com._pearls.cms.service;

import com._pearls.cms.dto.*;
import com._pearls.cms.entity.Contact;
import com._pearls.cms.entity.Email;
import com._pearls.cms.entity.Phone;
import com._pearls.cms.exception.InvalidRequestException;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.ContactRepository;
import com._pearls.cms.repository.EmailRepository;
import com._pearls.cms.repository.PhoneRepository;
import com._pearls.cms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        if (page < 0) {
            throw new InvalidRequestException("Page number cannot be negative");
        }
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            log.warn("User with id not found");
            throw new ResourceNotFoundException("User not found with the id");
        }

        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")));
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

    public void exportContacts(Long userId, Writer writer) throws IOException {
        List<Contact> contacts = contactRepository.findAllByUserId(userId);

        Map<Long, List<Email>> emailsByContact = new HashMap<>();
        Map<Long, List<Phone>> phonesByContact = new HashMap<>();
        int maxEmails = 0;
        int maxPhones = 0;

        for (Contact contact : contacts) {
            List<Email> emails = emailRepository.findByContactId(contact.getId());
            List<Phone> phones = phoneRepository.findByContactId(contact.getId());
            emailsByContact.put(contact.getId(), emails);
            phonesByContact.put(contact.getId(), phones);
            maxEmails = Math.max(maxEmails, emails.size());
            maxPhones = Math.max(maxPhones, phones.size());
        }

        List<String> headers = new ArrayList<>(List.of("Title", "FirstName", "LastName"));
        for (int i = 1; i <= maxEmails; i++) {
            headers.add("Email" + i + "_Label");
            headers.add("Email" + i);
        }
        for (int i = 1; i <= maxPhones; i++) {
            headers.add("Phone" + i + "_Label");
            headers.add("Phone" + i);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(headers.toArray(new String[0]))
                .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Contact contact : contacts) {
                List<Object> row = new ArrayList<>();
                row.add(sanitizeCsvField(contact.getTitle()));
                row.add(sanitizeCsvField(contact.getFirstName()));
                row.add(sanitizeCsvField(contact.getLastName()));

                List<Email> emails = emailsByContact.get(contact.getId());
                for (int i = 0; i < maxEmails; i++) {
                    if (i < emails.size()) {
                        row.add(sanitizeCsvField(emails.get(i).getLabel()));
                        row.add(sanitizeCsvField(emails.get(i).getEmail()));
                    } else {
                        row.add("");
                        row.add("");
                    }
                }

                List<Phone> phones = phonesByContact.get(contact.getId());
                for (int i = 0; i < maxPhones; i++) {
                    if (i < phones.size()) {
                        row.add(sanitizeCsvField(phones.get(i).getLabel()));
                        row.add(sanitizeCsvField(phones.get(i).getPhone()));
                    } else {
                        row.add("");
                        row.add("");
                    }
                }

                printer.printRecord(row);
            }
        }
    }

    @Transactional
    public SuccessResponse importContacts(Long userId, InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        CSVParser parser;
        try {
            parser = new CSVParser(reader, format);
        } catch (IOException ex) {
            throw new InvalidRequestException("Unable to parse the uploaded CSV file: " + ex.getMessage());
        }

        Set<String> headerNames = new HashSet<>(parser.getHeaderNames());

        if (!headerNames.contains("Title") || !headerNames.contains("FirstName") || !headerNames.contains("LastName")) {
            throw new InvalidRequestException("CSV is missing required headers: Title, FirstName, LastName");
        }

        int maxEmailIndex = 0;
        int maxPhoneIndex = 0;
        for (String header : headerNames) {
            maxEmailIndex = Math.max(maxEmailIndex, extractIndex(header, "Email"));
            maxPhoneIndex = Math.max(maxPhoneIndex, extractIndex(header, "Phone"));
        }

        // Require BOTH headers present for every index 1..max — reject gaps like Email1/Email3
        for (int i = 1; i <= maxEmailIndex; i++) {
            if (!headerNames.contains("Email" + i) || !headerNames.contains("Email" + i + "_Label")) {
                throw new InvalidRequestException(
                        "CSV header for Email" + i + " is incomplete or missing; both Email" + i +
                                " and Email" + i + "_Label are required for a contiguous sequence up to Email" + maxEmailIndex);
            }
        }
        for (int i = 1; i <= maxPhoneIndex; i++) {
            if (!headerNames.contains("Phone" + i) || !headerNames.contains("Phone" + i + "_Label")) {
                throw new InvalidRequestException(
                        "CSV header for Phone" + i + " is incomplete or missing; both Phone" + i +
                                " and Phone" + i + "_Label are required for a contiguous sequence up to Phone" + maxPhoneIndex);
            }
        }

        List<ContactRequest> requests = new ArrayList<>();

        List<CSVRecord> records;
        try {
            records = parser.getRecords();
        } catch (Exception ex) {
            throw new InvalidRequestException("Unable to read CSV rows: " + ex.getMessage());
        }

        for (CSVRecord record : records) {
            ContactRequest request = new ContactRequest();
            String title = unsanitizeCsvField(record.get("Title"));
            String firstName = unsanitizeCsvField(record.get("FirstName"));
            String lastName = unsanitizeCsvField(record.get("LastName"));

            if (title == null || title.isBlank() || firstName == null || firstName.isBlank()
                    || lastName == null || lastName.isBlank()) {
                throw new InvalidRequestException("Row " + record.getRecordNumber() + " is missing required fields");
            }

            request.setTitle(title);
            request.setFirstName(firstName);
            request.setLastName(lastName);

            List<EmailDto> emails = new ArrayList<>();
            for (int i = 1; i <= maxEmailIndex; i++) {
                String label = unsanitizeCsvField(record.get("Email" + i + "_Label"));
                String email = unsanitizeCsvField(record.get("Email" + i));
                if (label != null && !label.isBlank() && email != null && !email.isBlank()) {
                    emails.add(new EmailDto(label, email));
                }
            }
            request.setEmails(emails);

            List<PhoneDto> phones = new ArrayList<>();
            for (int i = 1; i <= maxPhoneIndex; i++) {
                String label = unsanitizeCsvField(record.get("Phone" + i + "_Label"));
                String phone = unsanitizeCsvField(record.get("Phone" + i));
                if (label != null && !label.isBlank() && phone != null && !phone.isBlank()) {
                    phones.add(new PhoneDto(label, phone));
                }
            }
            request.setPhones(phones);

            if (emails.isEmpty() || phones.isEmpty()) {
                throw new InvalidRequestException("Row " + record.getRecordNumber() + " must have at least one email and one phone");
            }

            requests.add(request);
        }

        for (ContactRequest request : requests) {
            addContact(userId, request);
        }

        log.info("Imported {} contacts for user {}", requests.size(), userId);
        return new SuccessResponse("Imported " + requests.size() + " contacts successfully");
    }

    private int extractIndex(String header, String prefix) {
        String suffix;
        if (header.startsWith(prefix)) {
            suffix = header.substring(prefix.length());
        } else {
            return 0;
        }
        if (suffix.endsWith("_Label")) {
            suffix = suffix.substring(0, suffix.length() - "_Label".length());
        }
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String sanitizeCsvField(String value) {
        if (value != null && !value.isEmpty() && "=+-@".indexOf(value.charAt(0)) != -1) {
            return "'" + value;
        }
        return value;
    }

    private String unsanitizeCsvField(String value) {
        if (value != null && value.length() > 1
                && value.charAt(0) == '\'' && "=+-@".indexOf(value.charAt(1)) != -1) {
            return value.substring(1);
        }
        return value;
    }

}
