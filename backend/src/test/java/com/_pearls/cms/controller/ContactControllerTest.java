package com._pearls.cms.controller;

import com._pearls.cms.dto.ContactRequest;
import com._pearls.cms.dto.EmailDto;
import com._pearls.cms.dto.PhoneDto;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.service.ContactService;
import com._pearls.cms.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ContactService contactService;

    private static final Long TEST_USER_ID = 50L;

    private Authentication testAuth() {
        User principal = new User();
        principal.setId(TEST_USER_ID);
        return new UsernamePasswordAuthenticationToken(
                principal, null, AuthorityUtils.NO_AUTHORITIES);
    }

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
    void addContactSuccess() throws Exception {
        ContactRequest request = buildRequest();
        when(contactService.addContact(eq(TEST_USER_ID), any(ContactRequest.class)))
                .thenReturn(new SuccessResponse("Contact Added Successfully"));

        mockMvc.perform(post("/api/contacts/add-contact")
                        .with(authentication(testAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void addContactInvalidBody() throws Exception {
        ContactRequest invalid = new ContactRequest();
        invalid.setTitle("");
        invalid.setFirstName("");
        invalid.setLastName("");
        invalid.setEmails(List.of());
        invalid.setPhones(List.of());

        mockMvc.perform(post("/api/contacts/add-contact")
                        .with(authentication(testAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateContactSuccess() throws Exception {
        ContactRequest request = buildRequest();
        when(contactService.updateContact(eq(1L), eq(TEST_USER_ID), any(ContactRequest.class)))
                .thenReturn(new SuccessResponse("Contact updated successfully"));

        mockMvc.perform(put("/api/contacts/update-contact/1")
                        .with(authentication(testAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    void deleteContactSuccess() throws Exception {
        when(contactService.deleteContact(1L, TEST_USER_ID))
                .thenReturn(new SuccessResponse("Contact deleted successfully"));

        mockMvc.perform(delete("/api/contacts/delete-contact/1")
                        .with(authentication(testAuth())))
                .andExpect(status().isOk());
    }

}