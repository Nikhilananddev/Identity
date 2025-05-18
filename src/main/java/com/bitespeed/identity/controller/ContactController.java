package com.bitespeed.identity.controller;

import com.bitespeed.identity.dto.IdentifyRequest;
import com.bitespeed.identity.dto.IdentifyResponse;
import com.bitespeed.identity.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identify")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@Valid @RequestBody IdentifyRequest request) {
        IdentifyResponse response = contactService.identify(request);
        return ResponseEntity.ok(response);
    }
}
