package com._pearls.cms.controller;

import com._pearls.cms.dto.ContactListResponse;
import com._pearls.cms.dto.ContactRequest;
import com._pearls.cms.dto.ContactResponse;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.Contact;
import com._pearls.cms.entity.User;
import com._pearls.cms.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal User user) {
        Page<ContactListResponse> contacts = contactService.findAllContact(user.getId(),page);

        return new ResponseEntity<>(contacts,HttpStatus.OK);
    }

    @GetMapping("/get-contact/{contactId}")
    public ResponseEntity<ContactResponse> getAll(
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
}
