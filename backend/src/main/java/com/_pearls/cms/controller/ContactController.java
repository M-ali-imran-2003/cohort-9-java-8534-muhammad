package com._pearls.cms.controller;

import com._pearls.cms.dto.ContactListResponse;
import com._pearls.cms.dto.ContactRequest;
import com._pearls.cms.dto.ContactResponse;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.InvalidRequestException;
import com._pearls.cms.service.ContactService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("api/contacts")
@RestController
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/get-all-contacts")
    public ResponseEntity<Page<ContactListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal User user) {
        if (page < 0) {
            throw new InvalidRequestException("Page number cannot be negative");
        }
        Page<ContactListResponse> contacts = contactService.findAllContacts(user.getId(), page, search);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/get-contact/{contactId}")
    public ResponseEntity<ContactResponse> get(
            @PathVariable Long contactId,
            @AuthenticationPrincipal User user) {
        ContactResponse contact = contactService.findContact(user.getId(),contactId);

        return new ResponseEntity<>(contact,HttpStatus.OK);
    }

    @PostMapping("/add-contact")
    public ResponseEntity<SuccessResponse> add(@AuthenticationPrincipal User user, @RequestBody @Valid ContactRequest contactRequest){

        SuccessResponse response = contactService.addContact(user.getId(),contactRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-contact/{contactId}")
    public ResponseEntity<SuccessResponse> delete(@PathVariable Long contactId,
                                                  @AuthenticationPrincipal User user){

        SuccessResponse response = contactService.deleteContact(contactId, user.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-contact/{contactId}")
    public ResponseEntity<SuccessResponse> update(@PathVariable Long contactId,
                                                  @AuthenticationPrincipal User user,
                                                  @RequestBody @Valid ContactRequest contactRequest){

        SuccessResponse response = contactService.updateContact(contactId, user.getId(), contactRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/export-contacts")
    public void export(@AuthenticationPrincipal User user, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"contacts.csv\"");
        contactService.exportContacts(user.getId(), response.getWriter());
    }
}
